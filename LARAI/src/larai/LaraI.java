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
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.profile.BasicWeaverProfiler;
import org.lara.interpreter.profile.ReportField;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.utils.MessageConstants;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.suikasoft.jOptions.JOptionKeys;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import larac.utils.output.Output;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.lara.LaraSystemTools;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import pt.up.fe.specs.util.utilities.SpecsThreadLocal;

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
            larai.out.close();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Exception during interpretation", e);
        } finally {
            if (WeaverEngine.isWeaverSet()) {
                WeaverEngine.removeWeaver();
            }

            THREAD_LOCAL_WEAVER_DATA.removeWithWarning();
            if (larai != null) {
                THREAD_LOCAL_LARAI.removeWithWarning();
            }

        }
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

        try {
            Options finalOptions = LaraCli.getCliOptions(weaverEngine);

            CommandLine cmd = OptionsParser.parse(args, finalOptions);

            ExecutionMode mode = OptionsParser.getExecMode(args[0], cmd, finalOptions);
            DataStore dataStore;
            boolean success;
            boolean isRunningGui;
            switch (mode) {
            case CONFIG: // convert configuration file to data store and run
                dataStore = OptionsConverter.configFile2DataStore(weaverEngine, cmd);
                success = execPrivate(dataStore, weaverEngine);
                isRunningGui = false;
                break;
            case OPTIONS: // convert options to data store and run
                dataStore = OptionsConverter.commandLine2DataStore(args[0], cmd, weaverEngine.getOptions());
                success = execPrivate(dataStore, weaverEngine);
                isRunningGui = false;
                break;
            case CONFIG_OPTIONS: // convert configuration file to data store, override with extra options and run
                dataStore = OptionsConverter.configExtraOptions2DataStore(args[0], cmd, weaverEngine);
                success = execPrivate(dataStore, weaverEngine);
                isRunningGui = false;
                break;
            default:
                throw new NotImplementedException(mode);
            }

            return LaraiResult.newInstance(success, isRunningGui);

        } catch (final Exception e) {
            throw new RuntimeException("Exception while executing LARA script", e);
        } finally {
            if (WeaverEngine.isWeaverSet()) {
                WeaverEngine.removeWeaver();
            }
        }
    }

    private void interpret(WeaverEngine weaverEngine) throws Exception {
        String engineWorkingDir = SpecsIo.getWorkingDir().getAbsolutePath();
        var configFolder = getThreadLocalData().get(JOptionKeys.CURRENT_FOLDER_PATH);
        if (!configFolder.isEmpty()) {
            engineWorkingDir = configFolder.get();
        }

        Path path = Paths.get(engineWorkingDir);

        JsEngine engine = createJsEngine(options.getJsEngine(), path,
                weaverEngine.getApiManager().getNodeModulesFolder());

        // Set javascript engine in WeaverEngine
        weaverEngine.setScriptEngine(engine);

        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Initializing Interpreter"));

        List<File> workspaceSources = new ArrayList<>();

        final FileList folderApplication = FileList.newInstance(workspaceSources);

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
                // If aspect arguments present, load them to object laraArgs
                loadAspectArguments();

                interpreter.executeMainAspect(options.getLaraFile());

                // Close weaver
                weaver.close();
            }

            String main = options.getMainAspect();

            weaver.eventTrigger().triggerWeaver(Stage.END, getWeaverArgs(), folderApplication.getFiles(), main,
                    options.getLaraFile().getPath());
            finish(engine);
        } catch (Exception e) {
            weaver.close();
            throw e;
        }

    }

    private void loadAspectArguments() {
        var jsonArgs = options.getAspectArgumentsStr();

        var init = jsonArgs.isEmpty() ? "{}" : jsonArgs;

        interpreter.evaluate("laraArgs = " + init + ";", "LARAI Preamble");
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


        return engineType.newEngine(engineType, engineForbiddenClasses, engineWorkingDirectory, nodeModulesFolder, engineOutputStream);        
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
        exec(args);
    }

    public static boolean exec(String args[]) {
        return exec(args, new DefaultWeaver());
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
}
