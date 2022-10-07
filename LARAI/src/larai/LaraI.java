/*
 * Copyright 2013 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */
package larai;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.lara.interpreter.Interpreter;
import org.lara.interpreter.cli.CLIConfigOption;
import org.lara.interpreter.cli.LaraCli;
import org.lara.interpreter.cli.OptionsConverter;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.cli.OptionsParser.ExecutionMode;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraIDataStore;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.gui.LaraLauncher;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.profile.BasicWeaverProfiler;
import org.lara.interpreter.profile.ReportField;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.MessageConstants;
import org.lara.interpreter.utils.Tools;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import larac.LaraC;
import larac.utils.output.Output;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.lara.LaraSystemTools;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.importer.LaraImporter;
import pt.up.fe.specs.tools.lara.exception.BaseException;
import pt.up.fe.specs.tools.lara.trace.CallStackTrace;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.Replacer;
import pt.up.fe.specs.util.utilities.SpecsThreadLocal;
import tdrc.utils.Pair;

/**
 * An interpreter for the LARA language, which converts the Aspect-IR into a javascript representation and runs that
 * script. This is used in REFLECT as an outer-loop for the project-flow and also for design-space exploration.
 * Furthermore, one can have a weaver which can be used with this interpreter. For that, one should implement the
 * interface org.reflect.larai.IWeaver, available in this project, and follow its instructions, and change the weaver
 * with the -w/--weaver option with the name of that implementation (E.g.: -weaver org.specs.Matisse) OR invoke
 * LARAI.exec(2)/LARAI.exec(5)
 *
 * @author Tiago
 */
public class LaraI {
    public static final double LARA_VERSION = 3.1; // Since we are using GraalVM
    public static final String LARAI_VERSION_TEXT = "Lara interpreter version: " + LaraI.LARA_VERSION;
    public static final String DEFAULT_WEAVER = DefaultWeaver.class.getName();
    public static final String PROPERTY_JAR_PATH = LaraC.PROPERTY_JAR_PATH;

    private static final ThreadLocal<Boolean> RUNNING_GUI = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> SERVER_MODE = ThreadLocal.withInitial(() -> false);

    // TODO: Put LARASystem.class eventually
    private static final Collection<Class<?>> FORBIDDEN_CLASSES = Arrays.asList(ProcessBuilder.class,
            LaraSystemTools.class);

    public static boolean isRunningGui() {
        return RUNNING_GUI.get();
    }

    public static boolean isServerMode() {
        return SERVER_MODE.get();
    }

    public static void setServerMode() {
        SERVER_MODE.set(true);
    }

    /**
     * Thread-scope DataStore
     */
    private static final SpecsThreadLocal<DataStore> THREAD_LOCAL_WEAVER_DATA = new SpecsThreadLocal<>(DataStore.class);

    public static DataStore getThreadLocalData() {
        return THREAD_LOCAL_WEAVER_DATA.get();
    }

    /**
     * Thread-scope LaraC
     */
    private static final SpecsThreadLocal<LaraI> THREAD_LOCAL_LARAI = new SpecsThreadLocal<>(LaraI.class);

    public static LaraI getThreadLocalLarai() {
        return THREAD_LOCAL_LARAI.get();
    }

    private LaraIDataStore options;
    private MasterWeaver weaver;
    public Output out = new Output();
    private boolean quit = false;

    // private LaraIOptionParser opts = new LaraIOptionParser();
    private Document aspectIRDocument;
    private Interpreter interpreter;
    private Aspects asps = null;
    private StringBuilder js = new StringBuilder();
    private WeaverProfiler weavingProfile;

    private final WeaverEngine weaverEngine;
    private int mainLaraTokens = -1;

    private static Supplier<Long> timeProvider = System::currentTimeMillis;

    /**
     * Create a new LaraI with the input datastore
     *
     * @param dataStore
     * @param weaverEngine
     */
    private LaraI(DataStore dataStore, WeaverEngine weaverEngine) {
        this.weaverEngine = weaverEngine;
        setOptions(new LaraIDataStore(this, dataStore, weaverEngine));

        // final boolean continueRun = LaraIOptionsSetter.setOptions(this, jarLoc, dataStore);
        quit = false;
        weavingProfile = weaverEngine.getWeaverProfiler();

        if (weavingProfile == null) {
            weavingProfile = BasicWeaverProfiler.emptyProfiler();
        }
    }

    public static LaraI newInstance(DataStore dataStore, WeaverEngine weaverEngine) {
        return new LaraI(dataStore, weaverEngine);
    }

    public static LaraI newInstance(WeaverEngine weaverEngine) {
        DataStore data = DataStore.newInstance("EmptyLarai");
        data.add(LaraiKeys.LARA_FILE, new File(""));
        return LaraI.newInstance(data, weaverEngine);
    }

    public WeaverEngine getWeaverEngine() {
        return weaverEngine;
    }

    public JsEngine getScriptEngine() {
        return weaverEngine.getScriptEngine();
    }

    /**
     * Executes larai with a Weaving engine implementing {@link WeaverEngine}, and the language specification language
     * specification. The datastore must contain the options available in {@link LaraiKeys}
     *
     * @param dataStore
     * @param weaverEngine
     * @param langSpec
     * @return
     */
    // public static boolean exec(DataStore dataStore, Class<? extends WeaverEngine> weaverEngine) {
    // try {
    // return exec(dataStore, weaverEngine.newInstance());
    // } catch (Exception e) {
    // throw new RuntimeException(
    // "Could not instantiate weaver engine with class '" + weaverEngine.getClass() + "'", e);
    // }
    // }
    /**
     * Executes larai with a Weaving engine implementing {@link WeaverEngine}.
     * <p>
     * The datastore contains the options available in {@link LaraiKeys} and the options given by
     * {@link WeaverEngine#getOptions()}.
     *
     * @param dataStore
     * @param weaverEngine
     * @return
     */
    public static boolean exec(DataStore dataStore, WeaverEngine weaverEngine) {

        // Create new instance of the weaver engine, to avoid reuse of information between consecutive runs
        var newWeaverEngine = SpecsSystem.newInstance(weaverEngine.getClass());

        // Launch weaver on another thread, to guarantee that there are no conflicts in ThreadLocal variables
        // var result = SpecsSystem.executeOnThreadAndWait(() -> execPrivate(dataStore, weaverEngine));
        var result = SpecsSystem.executeOnThreadAndWait(() -> execPrivate(dataStore, newWeaverEngine));
        return result == null ? false : result;
    }

    private static boolean execPrivate(DataStore dataStore, WeaverEngine weaverEngine) {

        prepareDataStore(dataStore, weaverEngine);

        MessageConstants.order = 1;
        larac.utils.output.MessageConstants.order = 1;

        LaraI larai = null;
        try {

            if (dataStore == null) {
                throw new NullPointerException("The DataStore cannot be null");
            }

            long start = getCurrentTime();
            THREAD_LOCAL_WEAVER_DATA.setWithWarning(dataStore);

            // Check if unit-testing mode
            if (dataStore.get(LaraiKeys.UNIT_TEST_MODE)) {
                return weaverEngine.executeUnitTestMode(dataStore);
            }

            larai = new LaraI(dataStore, weaverEngine);

            if (larai.options.isDebug()) {
                larai.out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, ". LARA Options"));
                larai.out.println(dataStore);
            }

            if (!larai.quit) {
                THREAD_LOCAL_LARAI.setWithWarning(larai);
                larai.interpret(weaverEngine);
            }

            long end = getCurrentTime() - start;

            larai.getWeavingProfile().report(ReportField.TOTAL_TIME, (int) end);
            larai.out.println(MessageConstants.getElapsedTimeMessage(end, "LARA total time"));
            larai.interpreter.exportMetrics();
            larai.out.close();
            return true;

            // } catch (final Throwable e) {
        } catch (Exception e) {
            // throw new RuntimeException("Exception during interpretation", e);
            // throw new RuntimeException(e);

            throw treatExceptionInInterpreter(larai, e);

            // var finalException = treatExceptionInInterpreter(larai, e);
            // System.out.println(finalException);
        } finally {
            if (WeaverEngine.isWeaverSet()) {
                WeaverEngine.removeWeaver();
            }

            THREAD_LOCAL_WEAVER_DATA.removeWithWarning();
            if (larai != null) {
                THREAD_LOCAL_LARAI.removeWithWarning();
            }

        }

        // return false;
    }

    private static void prepareDataStore(DataStore dataStore, WeaverEngine weaverEngine) {
        // String weaverName = weaverEngine.getName().orElse("<unnamed weaver>");
        String weaverName = weaverEngine.getName();
        StoreDefinition weaverKeys = new StoreDefinitionBuilder(weaverName)
                // Add LaraI keys
                .addDefinition(LaraiKeys.STORE_DEFINITION)
                // Add weaver custom keys
                .addDefinition(weaverEngine.getStoreDefinition())
                .build();

        dataStore.setStoreDefinition(weaverKeys);
    }

    // public static boolean exec(String[] args, Class<? extends WeaverEngine> weaverEngine) {
    // return exec(args, weaverEngine.newInstance());
    // }
    /**
     * Executes larai with a Weaving engine implementing {@link WeaverEngine}. The varargs are converted into a
     * DataStore
     *
     * @param args
     * @param weaverEngine
     * @return
     */
    public static boolean exec(String[] args, WeaverEngine weaverEngine) {
        // Launch weaver on another thread, to guarantee that there are no conflicts in ThreadLocal variables
        LaraiResult result = SpecsSystem.executeOnThreadAndWait(() -> execPrivate(args, weaverEngine));
        RUNNING_GUI.set(result.get(LaraiResult.IS_RUNNING_GUI));
        return result.get(LaraiResult.IS_SUCCESS);
    }

    public static LaraiResult execPrivate(String[] args, WeaverEngine weaverEngine) {
        SpecsLogs.debug("Weaver command-line arguments: " + Arrays.stream(args).collect(Collectors.joining(" ")));

        // Set weaver (e.g. for help message to access name and build number)
        weaverEngine.setWeaver();

        // Reset global state
        MessageConstants.order = 1;
        if (CLIConfigOption.ALLOW_GUI && OptionsParser.guiMode(args)) {

            LaraLauncher.launchGUI(weaverEngine, Optional.empty());
            // return true;
            return LaraiResult.newInstance(true, true);
        }

        try {
            // Collection<Option> configOptions = OptionsParser.buildConfigOptions();
            // Collection<Option> mainOptions = OptionsParser.buildLaraIOptionGroup();
            // OptionsParser.addExtraOptions(mainOptions, weaverEngine.getOptions());
            //
            // Options finalOptions = new Options();
            //
            // configOptions.forEach(finalOptions::addOption); // So the config options appear on the top
            // mainOptions.forEach(finalOptions::addOption);

            Options finalOptions = LaraCli.getCliOptions(weaverEngine);

            CommandLine cmd = OptionsParser.parse(args, finalOptions);
            if (LaraIUtils.printHelp(cmd, finalOptions)) {
                // return true;
                return LaraiResult.newInstance(true, false);
            }

            // ExecutionMode mode = OptionsParser.getExecMode(args[0], cmd, mainOptions, finalOptions);
            ExecutionMode mode = OptionsParser.getExecMode(args[0], cmd, finalOptions);
            DataStore dataStore;
            boolean success;
            boolean isRunningGui;
            switch (mode) {
            // case UNIT_TEST:
            // return weaverEngine.executeUnitTestMode(Arrays.asList(args));
            case CONFIG: // convert configuration file to data store and run
                // System.out.println("CONFIG ARGS:" + Arrays.toString(args));
                dataStore = OptionsConverter.configFile2DataStore(weaverEngine, cmd);
                success = execPrivate(dataStore, weaverEngine);
                isRunningGui = false;
                break;
            // return execPrivate(dataStore, weaverEngine);
            case CONFIG_GUI: // get the configuration file and execute GUI
                File guiFile = OptionsParser.getConfigFile(cmd);
                LaraLauncher.launchGUI(weaverEngine, Optional.of(guiFile));
                success = true;
                isRunningGui = true;
                break;
            case OPTIONS: // convert options to data store and run
                // SpecsLogs.debug("Received args: " + Arrays.toString(args));

                dataStore = OptionsConverter.commandLine2DataStore(args[0], cmd, weaverEngine.getOptions());

                // return execPrivate(dataStore, weaverEngine);
                success = execPrivate(dataStore, weaverEngine);
                isRunningGui = false;
                break;
            case CONFIG_OPTIONS: // convert configuration file to data store, override with extra options and run
                dataStore = OptionsConverter.configExtraOptions2DataStore(args[0], cmd, weaverEngine);
                // return execPrivate(dataStore, weaverEngine);
                success = execPrivate(dataStore, weaverEngine);
                isRunningGui = false;
                break;
            case GUI:
                LaraLauncher.launchGUI(weaverEngine, Optional.empty());
                success = true;
                isRunningGui = true;
                break;
            default:
                throw new NotImplementedException(mode);
            }

            return LaraiResult.newInstance(success, isRunningGui);

        } catch (final Exception e) {
            // throw new RuntimeException("", e);
            throw prettyRuntimeException(e);
        } finally {
            if (WeaverEngine.isWeaverSet()) {
                WeaverEngine.removeWeaver();
            }
        }
        // return true;
    }

    /**
     * Treats the given exception and closes the required streams
     *
     * @param laraInterp
     * @param e
     * @return
     */
    private static RuntimeException treatExceptionInInterpreter(LaraI laraInterp, Throwable e) {

        if (laraInterp == null) {
            LaraIException ex = new LaraIException("Exception before LARA Interpreter was initialized", e);
            return prettyRuntimeException(ex);
        }

        laraInterp.out.close();
        laraInterp.quit = true;

        if (laraInterp.options.useStackTrace()) {

            if (laraInterp.interpreter == null) {

                return prettyRuntimeException(e);
            }

            CallStackTrace stackStrace = laraInterp.interpreter.getStackStrace();
            if (stackStrace.isEmpty()) {

                return prettyRuntimeException(e);
            }

            return prettyRuntimeException(e, stackStrace);
        }
        return prettyRuntimeException(e);

    }

    private static RuntimeException prettyRuntimeException(Throwable e, CallStackTrace stackStrace) {
        BaseException laraException;

        if (!(e instanceof BaseException)) {
            laraException = new LaraIException("During LARA Interpreter execution ", e);
        } else {
            laraException = (BaseException) e;

        }
        //
        // BaseException laraException = (BaseException) e;
        return laraException.generateRuntimeException(stackStrace);
    }

    /**
     * Builds the pretty printed runtime exception if the exception given in of type {@link BaseException}.
     * <p>
     * If a normal expcetion is given, then it outputs the same exception.
     *
     * @param e
     * @return
     */
    private static RuntimeException prettyRuntimeException(Throwable e) {

        BaseException laraException;

        if (!(e instanceof BaseException)) {
            laraException = new LaraIException("During LARA Interpreter execution ", e);
        } else {
            laraException = (BaseException) e;
        }
        //
        // BaseException laraException = (BaseException) e;
        return laraException.generateRuntimeException();
    }

    /**
     * Initialize the AspectIR structure
     *
     * @throws Exception
     * @throws DOMException
     */
    private void startAspectIR() throws DOMException, Exception {
        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, ". Loading Aspect-IR"));
        // this.out.println(larac.utils.output.MessageConstants.FILE_READ + this.options.getLaraFile().getName());
        // try {
        asps = new Aspects(aspectIRDocument, "");
        // out.println("-----------Converted Aspect-IR-----------");
        // asps.print(out.getOutStream(), 0);
        // } catch (Exception e) {
        // throw new LARAIException(this.options.getAspectOriginalName(), "Could
        // not parse Aspect-IR", e);
        // }
    }

    // public Pair<Document, LaraC> compileWithLARAC(File fileName, LanguageSpecificationV2 langSpec,
    // LaraIDataStore options,
    // Output out) throws Exception {
    //
    // }

    /**
     * Compile the lara file with LARAC, according to a {@link LanguageSpecificationV2}
     *
     * @param fileName
     * @param langSpec
     * @param options
     * @param out
     * @return
     * @throws Exception
     */
    public Pair<Document, LaraC> compileWithLARAC(File fileName, LanguageSpecificationV2 langSpec,
            LaraIDataStore options,
            Output out) throws Exception {

        // Process Lara Bundles in include folders
        // includesFolder = processLaraBundles(includesFolder);

        String path = options.getOutputDir().getPath();

        FileList includeDirs = options.getProcessedIncludeDirs(getWeaverEngine());

        // LaraIDataStore.processIncludeDirs(includeDirs);
        /*
        // Process LARA Bundles
        // LaraBundle laraBundle = new LaraBundle(getWeaverEngine().getLanguages(), getWeaverEngine().getWeaverNames());
        LaraBundle laraBundle = new LaraBundle(getWeaverEngine().getWeaverNames(), options.getBundleTags());
        FileList processedIncludeDirs = laraBundle.process(includeDirs);
        
        // Process LARA Resources
        LaraResource laraResource = new LaraResource(getWeaverEngine());
        processedIncludeDirs = laraResource.process(processedIncludeDirs);
        */
        String encodedIncludes = includeDirs.encode();
        // String encodedIncludes = options.getIncludeDirs().encode();
        List<String> preprocess = new ArrayList<>();
        preprocess.add(fileName.getPath());
        preprocess.add("-o");
        preprocess.add(path);
        if (!encodedIncludes.trim().isEmpty()) {
            preprocess.add("-i");
            preprocess.add(encodedIncludes);
        }

        // lara files as resources
        // List<ResourceProvider> laraAPIs = new ArrayList<>(ResourceProvider.getResources(LaraApiResource.class));
        // System.out.println("LARA APIS :" + IoUtils.getResource(laraAPIs2.get(0)));
        // laraAPIs.addAll(options.getLaraAPIs());
        List<ResourceProvider> laraAPIs = options.getLaraAPIs();
        if (!laraAPIs.isEmpty()) {
            preprocess.add("-r");
            String resources = laraAPIs.stream().map(LaraI::getOriginalResource)
                    .collect(Collectors.joining(SpecsIo.getUniversalPathSeparator()));
            preprocess.add(resources);
        }

        if (options.isDebug()) {
            preprocess.add("-d");
        }

        final LaraC lara = new LaraC(preprocess.toArray(new String[0]), langSpec, out);
        // lara.compileAndSave();
        // Document compile = lara.getAspectIRXmlRepresentation();
        // return compile;
        Document compile = lara.compile();
        this.setNumMainLaraTokens(lara.getNumTokens());
        out.println("Processed " + getNumMainLaraTokens() + " tokens from the LARA file.");

        return Pair.newInstance(compile, lara);
        // return compile;

    }

    private static String getOriginalResource(ResourceProvider resource) {
        if (resource instanceof LaraResourceProvider) {
            return ((LaraResourceProvider) resource).getOriginalResource();
        }

        return resource.getResource();
    }

    private LaraC compile(LanguageSpecificationV2 languageSpecification) {

        // final String aspectIR_name = this.options.getAspectIR_name();
        final String extention = SpecsIo.getExtension(options.getLaraFile());

        Document aspectIR;
        LaraC larac = null;
        if (extention.equals("lara")) {
            try {
                var result = compileWithLARAC(options.getLaraFile(), languageSpecification, options,
                        out);

                aspectIR = result.getLeft();
                larac = result.getRight();

                quit = aspectIR == null;
                if (quit) {
                    throw new Exception("problems during compilation");
                }

            } catch (Exception e) {
                throw new LaraIException(options.getLaraFile(), "Compilation problem", e);
            }
            // if (this.quit) {
            // return;
            // }
            // options.setAspectIR_name(aspectIR_name.replace(".lara", ".xml"));

        } else {
            try {
                aspectIR = Aspects.readDocument(options.getLaraFile().getAbsolutePath());
            } catch (Exception e) {
                throw new LaraIException(options.getLaraFile(), "Reading aspect-ir problem", e);
            }
        }

        aspectIRDocument = aspectIR;
        return larac;
    }

    private void interpret(WeaverEngine weaverEngine) throws Exception {
        String engineWorkingDir = SpecsIo.getWorkingDir().getAbsolutePath();
        var configFolder = getThreadLocalData().get(JOptionKeys.CURRENT_FOLDER_PATH);
        if (!configFolder.isEmpty()) {
            engineWorkingDir = configFolder.get();
        }

        Path path = Paths.get(engineWorkingDir);

        JsEngine engine = createJsEngine(options.getJsEngine(), path);

        // Set javascript engine in WeaverEngine
        weaverEngine.setScriptEngine(engine);

        // NashornScriptEngine engine = (NashornScriptEngine) new NashornScriptEngineFactory()
        // .getScriptEngine(new String[] { "--global-per-engine" });

        // weaverEngine.setScriptEngine(engine);
        // try {
        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Initializing Interpreter"));
        // final ImporterTopLevel scope = new ImporterTopLevel(cx);

        List<File> workspaceSources = new ArrayList<>();

        workspaceSources.addAll(options.getWorkingDir().getFiles());
        // workspaceSources.addAll(options.getExtraSources());

        final FileList folderApplication = FileList.newInstance(workspaceSources);

        // final FileList folderApplication = options.getWorkingDir();

        // if (!folderApplication.exists()) {
        // throw new LaraIException(options.getLaraFile().getName(), "application folder does not exist",
        // new FileNotFoundException(folderApplication.getAbsolutePath()));
        // }
        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Loading Weaver"));
        long begin = getCurrentTime();
        weaver = new MasterWeaver(this, weaverEngine, folderApplication, engine);

        try {
            // Create interpreter
            interpreter = new Interpreter(this, engine);
        } catch (Exception e) {
            throw new LaraIException(options.getLaraFile().getName(), "Problem creating the interpreter", e);
        }

        boolean isWorking = weaver.begin();

        long end = getCurrentTime() - begin;
        getWeavingProfile().report(ReportField.INIT_TIME, (int) end);
        out.println(MessageConstants.getElapsedTimeMessage(end));

        if (!isWorking) {
            finish(engine);
            return;
        }

        try {
            // Start interpretation

            final String extension = SpecsIo.getExtension(options.getLaraFile());

            // If JS file
            if (Arrays.stream(JsFileType.values()).anyMatch(type -> type.getExtension().equals(extension))) {
                interpreter.executeMainAspect(options.getLaraFile());

                postMainJsExecution();

                // Close weaver
                weaver.close();
            }
            // If LARA file
            else {
                compile(weaverEngine.getLanguageSpecificationV2());
                startAspectIR();
                final StringBuilder mainCall = interpreter.interpretLara(asps);
                interpreter.executeMainAspect(mainCall);
            }

            String main = options.getMainAspect();
            if (main == null) {

                main = asps.main;
            }

            weaver.eventTrigger().triggerWeaver(Stage.END, getWeaverArgs(), folderApplication.getFiles(), main,
                    options.getLaraFile().getPath());
            finish(engine);
        } catch (Exception e) {

            // Close weaver
            weaver.close();

            // Rethrow exception
            throw e;
            // throw new RuntimeException("Exception during weaving:", e);
        }

    }

    private void postMainJsExecution() {

        // If report file enabled, eval writing the outputs using weaver.Script
        var outputJsonFile = options.getReportFile();
        if (outputJsonFile.isUsed()) {
            var template = new Replacer(() -> "org/lara/interpreter/outputResult.js.template");
            template.replace("<OUTPUT_JSON_PATH>", SpecsIo.normalizePath(outputJsonFile.getFile()));
            interpreter.evaluate(template.toString(), JsFileType.NORMAL, "LaraI.postMainJsExecution() - output json");
        }

    }

    private void finish(JsEngine engine) {
        // if cleaning is needed
    }

    // private NashornScriptEngine createJsEngine() {
    // // System.out.println("RESTRIC MODE:" + getOptions().isRestricMode());
    // // If restric mode is enabled, use ClassFilter
    // if (getOptions().isRestricMode()) {
    // return (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine(new RestrictModeFilter());
    // }
    //
    // return (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine();
    //
    // // NashornScriptEngine engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
    // // return engine;
    //
    // }

    private JsEngine createJsEngine(JsEngineType engineType) {
        return createJsEngine(engineType, null);
    }

    private JsEngine createJsEngine(JsEngineType engineType, Path engineWorkingDirectory) {
        // return new GraalvmJsEngine();
        if (getOptions().isRestricMode()) {
            return engineType.newEngine(engineType, FORBIDDEN_CLASSES, engineWorkingDirectory);
            // return new NashornEngine(FORBIDDEN_CLASSES);
        }
        return engineType.newEngine(engineType, Collections.emptyList(), engineWorkingDirectory);
        // return new NashornEngine();
    }

    public DataStore getWeaverArgs() {
        return options.getWeaverArgs();
    }

    /**
     * @param js
     *            the js to set
     */
    public void setJs(StringBuilder js) {
        this.js = js;
    }

    /**
     * @param js
     *            the js to append
     */
    public void appendJs(StringBuilder js) {
        this.js.append(js);
    }

    /**
     * @return the js
     */
    public StringBuilder getJs() {
        return js;
    }

    /*
     * public static void die(String error) { ErrorMsg.say(error);
     *
     * throw new RuntimeException(error); }
     */
    public Aspects getAsps() {
        return asps;
    }

    public void setAsps(Aspects asps) {
        this.asps = asps;
    }

    public Tools getTools() {
        return options.getTools();
    }

    /**
     * @return the weaver
     */
    public MasterWeaver getWeaver() {
        return weaver;
    }

    /**
     * @param weaver
     *            the weaver to set
     */
    public void setWeaver(MasterWeaver weaver) {
        this.weaver = weaver;
    }

    /**
     * @return the options
     */
    public LaraIDataStore getOptions() {
        return options;
    }

    /**
     * @param laraIDataStore
     *            the options to set
     */
    public void setOptions(LaraIDataStore laraIDataStore) {
        options = laraIDataStore;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public static String getVersion() {
        return LaraI.LARAI_VERSION_TEXT;
    }

    /**
     * Execute larai with the input arguments and the default weaver
     *
     * @param args
     */
    public static void main(String args[]) {
        SpecsSystem.programStandardInit();
        // SpecsProperty.ShowStackTrace.applyProperty("true");
        exec(args);
    }

    public static boolean exec(String args[]) {
        return exec(args, new DefaultWeaver());
    }

    /**
     * @deprecated Check if this method can be replaced with getWeaverEngine()
     * @return
     */
    @Deprecated
    public WeaverEngine getEngine() {
        if (weaver.getEngine() != getWeaverEngine()) {
            throw new RuntimeException("Weaver engines are not the same instance, should this happen?");
        }
        return weaver.getEngine();
    }

    public WeaverProfiler getWeavingProfile() {
        return weavingProfile;
    }

    public static long getCurrentTime() {

        return timeProvider.get();
    }

    public int getNumMainLaraTokens() {
        return mainLaraTokens;
    }

    public void setNumMainLaraTokens(int mainLaraTokens) {
        this.mainLaraTokens = mainLaraTokens;
    }

    public static AspectClassProcessor buildAspectProcessor(WeaverEngine weaver, JsEngine jsEngine) {

        // Create LARA Interpreter
        DataStore laraiConfig = DataStore.newInstance("LaraCompiler");
        laraiConfig.set(LaraiKeys.LARA_FILE, new File(""));
        LaraI larai = LaraI.newInstance(laraiConfig, weaver);

        // Create MasterWeaver
        FileList folderApplication = FileList.newInstance();
        MasterWeaver masterWeaver = new MasterWeaver(larai, weaver, folderApplication, jsEngine);
        larai.setWeaver(masterWeaver);

        // Enables auto-loading of search APIs
        // larai.getOptions().getWeaverArgs().set(LaraiKeys.API_AUTOLOAD, true);

        // Create interpreter
        Interpreter interpreter = new Interpreter(larai, jsEngine);
        // Interpreter interpreter = new Interpreter(larai, jsEngine, false);
        larai.setInterpreter(interpreter);
        // larai.getInterpreter().getImportProcessor().importAndInitialize();

        masterWeaver.simpleBegin();

        var aspectProcessor = AspectClassProcessor.newInstance(interpreter);
        return aspectProcessor;
    }

    /**
     * Loads a LARA import, using the same format as the imports in LARA files (e.g. weaver.Query).
     * 
     * <p>
     * Does not verify if import has already been imported.
     * 
     * @param importName
     */
    public static void loadLaraImport(String importName) {

        var weaverEngine = WeaverEngine.getThreadLocalWeaver();

        var laraImporter = getLaraImporter();
        var laraImports = laraImporter.getLaraImports(importName);

        if (laraImports.isEmpty()) {
            throw new RuntimeException("Could not find files for import '" + importName + "'");
        }

        // Import JS code
        for (var laraImport : laraImports) {
            // SpecsLogs.debug("Loading LARA Import '" + laraImport.getFilename() + "'");
            weaverEngine.getScriptEngine().eval(laraImport.getCode(), laraImport.getFileType(),
                    laraImport.getFilename() + " (LARA import '" + importName + "')");
        }

    }

    public static LaraImporter getLaraImporter() {
        var weaverEngine = WeaverEngine.getThreadLocalWeaver();
        var larai = LaraI.getThreadLocalLarai();

        // Prepare includes
        var includes = new LinkedHashSet<File>();

        // Add working directory
        includes.add(SpecsIo.getWorkingDir());

        // Add context folder, if present
        var configurationFolder = LaraI.getThreadLocalData().get(JOptionKeys.CURRENT_FOLDER_PATH)
                .map(File::new)
                .orElse(null);

        if (configurationFolder != null && configurationFolder.isDirectory()) {
            includes.add(configurationFolder);
        }

        // Add user includes
        includes.addAll(larai.getOptions().getProcessedIncludeDirs(weaverEngine).getFiles());

        var apis = larai.getOptions().getLaraAPIs();

        // Find files to import
        var laraImporter = new LaraImporter(LaraI.getThreadLocalLarai(), new ArrayList<>(includes), apis);

        return laraImporter;
    }

    public static Collection<String> getLaraImportInPackage(String packageName) {
        var laraImporter = getLaraImporter();
        return laraImporter.getImportsFromPackage(packageName);
    }
}
