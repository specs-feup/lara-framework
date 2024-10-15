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

import larac.utils.output.Output;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.lara.interpreter.Interpreter;
import org.lara.interpreter.cli.LaraCli;
import org.lara.interpreter.cli.OptionsConverter;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.cli.OptionsParser.ExecutionMode;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.config.interpreter.LaraIDataStore;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
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
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.lara.LaraSystemTools;
import pt.up.fe.specs.lara.importer.LaraImporter;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import pt.up.fe.specs.util.utilities.Replacer;
import pt.up.fe.specs.util.utilities.SpecsThreadLocal;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public static final String PROPERTY_JAR_PATH = "lara.jarpath";
    private static final ThreadLocal<Boolean> RUNNING_GUI = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> SERVER_MODE = ThreadLocal.withInitial(() -> false);

    // TODO: Put LARASystem.class eventually
    private static final Collection<Class<?>> FORBIDDEN_CLASSES = Arrays.asList(ProcessBuilder.class,
            LaraSystemTools.class, Runtime.class);

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

    private Interpreter interpreter;
    private WeaverProfiler weavingProfile;

    private final WeaverEngine weaverEngine;

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
            throw new RuntimeException("Exception during interpretation", e);
            // throw new RuntimeException(e);

            // throw treatExceptionInInterpreter(larai, e);

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

        // Set store definition

        String weaverName = weaverEngine.getName();
        StoreDefinition weaverKeys = new StoreDefinitionBuilder(weaverName)
                // Add LaraI keys
                .addDefinition(LaraiKeys.STORE_DEFINITION)
                // Add weaver custom keys
                .addDefinition(weaverEngine.getStoreDefinition())
                .build();

        dataStore.setStoreDefinition(weaverKeys);

        // Set persistence, if not set
        if (dataStore.getPersistence().isEmpty()) {
            AppPersistence persistence = OptionsParser.getXmlPersistence(weaverKeys);
            dataStore.setPersistence(persistence);
        }

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


    /**
     * Converts an array of strings to the corresponding DataStore.
     *
     * @param objArgs
     * @param weaverEngine
     * @return A DataStore that corresponds to the given arguments, or empty if the arguments represent a GUI execution mode.
     */
    public static Optional<DataStore> convertArgsToDataStore(Object[] objArgs, WeaverEngine weaverEngine) {

        var args = new String[objArgs.length];
        for (int i = 0; i < objArgs.length; i++) {
            args[i] = objArgs[i].toString();
        }

        Options finalOptions = LaraCli.getCliOptions(weaverEngine);

        CommandLine cmd = OptionsParser.parse(args, finalOptions);

        ExecutionMode mode = OptionsParser.getExecMode(args[0], cmd, finalOptions);

        SpecsLogs.debug("Detected launch mode " + mode);

        return switch (mode) {
            // convert configuration file to data store and run
            case CONFIG -> Optional.of(OptionsConverter.configFile2DataStore(weaverEngine, cmd));

            // convert options to data store and run
            case OPTIONS ->
                    Optional.of(OptionsConverter.commandLine2DataStore(args[0], cmd, weaverEngine.getOptions()));

            // convert configuration file to data store, override with extra options and run
            case CONFIG_OPTIONS ->
                    Optional.of(OptionsConverter.configExtraOptions2DataStore(args[0], cmd, weaverEngine));
        };
    }

    private static LaraiResult execPrivate(String[] args, WeaverEngine weaverEngine) {
        SpecsLogs.debug(() -> "Weaver command-line arguments: " + Arrays.stream(args).collect(Collectors.joining(" ")));

        // Set weaver (e.g. for help message to access name and build number)
        weaverEngine.setWeaver();

        // Reset global state
        MessageConstants.order = 1;

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

            SpecsLogs.debug("Launching weaver in mode " + mode);

            switch (mode) {
                // case UNIT_TEST:
                // return weaverEngine.executeUnitTestMode(Arrays.asList(args));
                case CONFIG: // convert configuration file to data store and run
                    // System.out.println("CONFIG ARGS:" + Arrays.toString(args));
                    dataStore = OptionsConverter.configFile2DataStore(weaverEngine, cmd);
                    success = execPrivate(dataStore, weaverEngine);
                    isRunningGui = false;
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
                default:
                    throw new NotImplementedException(mode);
            }

            return LaraiResult.newInstance(success, isRunningGui);

        } catch (final Exception e) {
            throw new RuntimeException("Exception while executing LARA script", e);
            // throw prettyRuntimeException(e);
        } finally {
            if (WeaverEngine.isWeaverSet()) {
                WeaverEngine.removeWeaver();
            }
        }
        // return true;
    }


    private void interpret(WeaverEngine weaverEngine) throws Exception {
        String engineWorkingDir = SpecsIo.getWorkingDir().getAbsolutePath();
        var configFolder = getThreadLocalData().get(JOptionKeys.CURRENT_FOLDER_PATH);
        if (!configFolder.isEmpty()) {
            engineWorkingDir = configFolder.get();
        }

        Path path = Paths.get(engineWorkingDir);

        // JsEngine engine = createJsEngine(options.getJsEngine(), path, weaverEngine.getApisFolder());
        JsEngine engine = createJsEngine(options.getJsEngine(), path,
                weaverEngine.getApiManager().getNodeModulesFolder());

        // Set javascript engine in WeaverEngine
        weaverEngine.setScriptEngine(engine);

        // weaverEngine.setScriptEngine(engine);
        // try {
        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Initializing Interpreter"));
        // final ImporterTopLevel scope = new ImporterTopLevel(cx);


        // final FileList folderApplication = options.getWorkingDir();

        // if (!folderApplication.exists()) {
        // throw new LaraIException(options.getLaraFile().getName(), "application folder does not exist",
        // new FileNotFoundException(folderApplication.getAbsolutePath()));
        // }
        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Loading Weaver"));
        long begin = getCurrentTime();
        weaver = new MasterWeaver(this, weaverEngine, engine);

        try {
            // Create interpreter
            interpreter = new Interpreter(this, engine);
        } catch (Exception e) {
            throw new LaraIException(options.getLaraFile().getName(), "Problem creating the interpreter", e);
        }

        try {
            boolean isWorking = weaver.begin();
            long end = getCurrentTime() - begin;
            getWeavingProfile().report(ReportField.INIT_TIME, (int) end);
            out.println(MessageConstants.getElapsedTimeMessage(end));

            if (!isWorking) {
                finish(engine);
                return;
            }

        } catch (Exception e) {
            finish(engine);
            throw new LaraIException(options.getLaraFile().getName(), "Exception while calling weaver begin() method",
                    e);
            // SpecsLogs.info("Exception while calling weaver begin(): " + e.getMessage());
            // finish(engine);
            // return;
        }

        try {
            // Start interpretation

            final String extension = SpecsIo.getExtension(options.getLaraFile());

            // If JS file
            if (Arrays.stream(JsFileType.values()).anyMatch(type -> type.getExtension().equals(extension))) {

                // If aspect arguments present, load them to object laraArgs
                loadAspectArguments();
                interpreter.executeMainAspect(options.getLaraFile());
                postMainJsExecution();

                // Close weaver
                weaver.close();
            }

            String main = options.getMainAspect();

            weaver.eventTrigger().triggerWeaver(Stage.END, getWeaverArgs(), main,
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

    private void loadAspectArguments() {
        var jsonArgs = options.getAspectArgumentsStr();

        var init = jsonArgs.isEmpty() ? "{}" : jsonArgs;

        interpreter.evaluate("laraArgs = " + init + ";", "LARAI Preamble");
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

    private JsEngine createJsEngine(JsEngineType engineType, Path engineWorkingDirectory, File nodeModulesFolder) {

        OutputStream engineOutputStream = System.out;
        if (getOptions().isJavaScriptStream()) {
            engineOutputStream = this.out.getOutStream();
        }

        Collection<Class<?>> engineForbiddenClasses = Collections.emptyList();
        if (getOptions().isRestricMode()) {
            engineForbiddenClasses = FORBIDDEN_CLASSES;
        }

        return engineType.newEngine(engineType, engineForbiddenClasses, engineWorkingDirectory, nodeModulesFolder,
                engineOutputStream);
    }

    public DataStore getWeaverArgs() {
        return options.getWeaverArgs();
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
     * @param weaver the weaver to set
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
     * @param laraIDataStore the options to set
     */
    private void setOptions(LaraIDataStore laraIDataStore) {
        options = laraIDataStore;
    }

    public WeaverProfiler getWeavingProfile() {
        return weavingProfile;
    }

    public static long getCurrentTime() {
        return timeProvider.get();
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
            throw new RuntimeException("Could not find files for import '" + importName + "'. Current include paths: "
                    + laraImporter.getIncludes());
        }

        // Import JS code
        for (var laraImport : laraImports) {
            SpecsLogs.debug(
                    () -> "Loading LARA Import '" + laraImport.getFilename() + "' as " + laraImport.getFileType());

            var source = laraImport.getJsFile().map(file -> SpecsIo.normalizePath(file.getAbsolutePath())).orElse(laraImport.getFilename());

            // For some reason that we still don't know if an import comes from a resource
            // and the 'source' value does not have the following suffix, the class of the import will
            // not be found (at least in Linux, in Windows is ok).
            source = source + " (LARA import '" + importName + "' as " + laraImport.getFileType().toString() + ")";

            weaverEngine.getScriptEngine().eval(laraImport.getCode(), laraImport.getFileType(),
                    source);
        }

    }

    private static LaraImporter getLaraImporter() {
        var weaverEngine = WeaverEngine.getThreadLocalWeaver();

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

        // Finally, add weaver APIs (they have the lowest priority)
        weaverEngine.getApiManager().getNpmApiFolders().stream()
                .forEach(includes::add);

        // Find files to import
        var laraImporter = new LaraImporter(LaraI.getThreadLocalLarai(), new ArrayList<>(includes));

        return laraImporter;
    }
}
