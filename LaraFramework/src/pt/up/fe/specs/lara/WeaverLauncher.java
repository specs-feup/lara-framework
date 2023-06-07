/**
 * Copyright 2019 SPeCS.
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

package pt.up.fe.specs.lara;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import larai.LaraI;
import pt.up.fe.specs.lara.doc.LaraDocLauncher;
import pt.up.fe.specs.lara.unit.LaraUnitLauncher;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * Utility methods what weavers can use to bootstrap execution.
 * 
 * @author JoaoBispo
 *
 */
public class WeaverLauncher {

    private static final String FLAG_KEEP_LARA = "--keepLara";

    private final WeaverEngine engine;

    private final Map<String, Function<String[], Boolean>> tasks;

    public WeaverLauncher(WeaverEngine engine) {
        this.engine = engine;
        tasks = buildTaskMap();
    }

    private Map<String, Function<String[], Boolean>> buildTaskMap() {
        var taskMap = new HashMap<String, Function<String[], Boolean>>();

        // If unit testing flag is present, run unit tester
        taskMap.put("-" + LaraiKeys.getUnitTestFlag(), args -> executeUnitTester(args));

        // If doc generator flag is present, run doc generator
        taskMap.put("-" + LaraiKeys.getDocGeneratorFlag(), args -> executeDocGenerator(args));

        // If server flag is present, run server
        taskMap.put("-" + LaraiKeys.getServerFlag(), args -> executeServer(args));

        // If api flag is present, copy APIs to a folder
        taskMap.put("-" + LaraiKeys.getApiFlag(), args -> executeApiExtractor(args));

        return taskMap;
    }

    private boolean executeApiExtractor(String[] args) {

        // Options
        var keepLara = false;

        var processedArgs = new ArrayList<String>();

        // Remove first argument (e.g. -api)
        IntStream.range(1, args.length)
                .mapToObj(i -> args[i])
                .forEach(processedArgs::add);

        int index = -1;

        index = processedArgs.indexOf("--help");
        if (index != -1) {
            SpecsLogs.info("<lara compiler> -api [--keepLara] <output folder>");
            return true;
        }

        while ((index = processedArgs.indexOf(FLAG_KEEP_LARA)) != -1) {
            processedArgs.remove(index);
            keepLara = true;
        }

        if (processedArgs.isEmpty()) {
            SpecsLogs.info("Expected output folder as parameter");
            return false;
        }

        var outputFolder = SpecsIo.mkdir(processedArgs.get(0));

        // Get APIs
        LaraI larai = LaraI.newInstance(engine);
        var api = new ArrayList<ResourceProvider>();
        api.addAll(larai.getOptions().getCoreScripts());
        api.addAll(larai.getOptions().getLaraAPIs());

        // Create LARA compiler
        var laraCompiler = new LaraCompiler(engine.getLanguageSpecificationV2());

        SpecsLogs.info("Extracting APIs to '" + outputFolder.getAbsolutePath() + "'");

        for (var apiFile : api) {
            var fileLocation = apiFile.getFileLocation();

            var destinationFile = new File(outputFolder, fileLocation);
            var fileContents = SpecsIo.getResource(apiFile);

            // If LARA file, first convert to JavaScript
            if (!keepLara && SpecsIo.getExtension(fileLocation).equals("lara")) {
                destinationFile = new File(outputFolder, SpecsIo.removeExtension(fileLocation) + ".js");

                fileContents = laraCompiler.compile(apiFile.getFilename(), fileContents);
            }

            SpecsLogs.info("Writing file " + destinationFile);
            SpecsIo.write(destinationFile, fileContents);
        }

        return true;
    }

    public boolean launchExternal(String[] args) {
        Thread t = Thread.currentThread();
        ClassLoader previousClassLoader = t.getContextClassLoader();
        var newClassloader = SpecsIo.class.getClassLoader();
        System.out.println("Unloading classloader " + previousClassLoader);
        System.out.println("Using classloader " + newClassloader);
        t.setContextClassLoader(newClassloader);
        boolean success = false;
        try {
            success = launch(args);
        } finally {
            t.setContextClassLoader(previousClassLoader);
        }

        return success;

    }

    /**
     * Launch Lara Interpreter with the given engine and the input arguments. If no arguments are given a GUI is
     * launched.
     *
     * <p>
     * Has support for LaraDoc and LaraUnit.
     *
     * @param args
     * @param engine
     */
    public boolean launch(String[] args) {

        // If no flags, just launch
        if (args.length == 0) {
            return LaraI.exec(args, engine);
        }

        var firstArg = args[0];

        // Check if first argument activates a predefined task
        var task = tasks.get(firstArg);

        if (task != null) {
            return task.apply(args);
        }

        // No predefined task, just execute as usual
        return LaraI.exec(args, engine);

        /*
        // If unit testing flag is present, run unit tester
        Optional<Boolean> unitTesterResult = runUnitTester(args);
        if (unitTesterResult.isPresent()) {
            return unitTesterResult.get();
        }
        
        // If doc generator flag is present, run doc generator
        Optional<Boolean> docGeneratorResult = runDocGenerator(args);
        if (docGeneratorResult.isPresent()) {
            return docGeneratorResult.get();
        }
        
        // If server flag is present, run server
        Optional<Boolean> serverResult = runServer(args);
        if (serverResult.isPresent()) {
            return serverResult.get();
        }
        
        return LaraI.exec(args, engine);
        */
    }

    // /**
    // * @deprecated
    // * @param args
    // * @return
    // */
    // @Deprecated
    // private Optional<Boolean> runUnitTester(String[] args) {
    // // Look for flag
    // String unitTestingFlag = "-" + LaraiKeys.getUnitTestFlag();
    //
    // int flagIndex = args.length == 0 ? -1 : unitTestingFlag.equals(args[0]) ? 0 : -1;
    //
    // // int flagIndex = IntStream.range(0, args.length)
    // // .filter(index -> unitTestingFlag.equals(args[index]))
    // // .findFirst()
    // // .orElse(-1);
    //
    // if (flagIndex == -1) {
    // return Optional.empty();
    // }
    //
    // List<String> laraUnitArgs = new ArrayList<>();
    // // laraUnitArgs.add("lara-unit-weaver=" + CxxWeaver.class.getName());
    // laraUnitArgs.add("--weaver");
    // laraUnitArgs.add(engine.getClass().getName());
    //
    // // laraUnitArgs.add("lara-unit-weaver=" + CxxWeaver.class.getName());
    // for (int i = flagIndex + 1; i < args.length; i++) {
    // laraUnitArgs.add(args[i]);
    // }
    //
    // SpecsLogs.debug("Launching lara-unit with flags '" + laraUnitArgs + "'");
    //
    // int unitResults = LaraUnitLauncher.execute(laraUnitArgs.toArray(new String[0]));
    //
    // return Optional.of(unitResults == 0);
    // }

    private Boolean executeUnitTester(String[] args) {

        // First index is the task flag
        int flagIndex = 0;

        List<String> laraUnitArgs = new ArrayList<>();
        // laraUnitArgs.add("lara-unit-weaver=" + CxxWeaver.class.getName());
        laraUnitArgs.add("--weaver");
        laraUnitArgs.add(engine.getClass().getName());

        // laraUnitArgs.add("lara-unit-weaver=" + CxxWeaver.class.getName());
        for (int i = flagIndex + 1; i < args.length; i++) {
            laraUnitArgs.add(args[i]);
        }

        SpecsLogs.debug("Launching lara-unit with flags '" + laraUnitArgs + "'");

        int unitResults = LaraUnitLauncher.execute(laraUnitArgs.toArray(new String[0]));

        return unitResults == 0;
    }

    // /**
    // * @deprecated
    // * @param args
    // * @return
    // */
    // @Deprecated
    // private Optional<Boolean> runDocGenerator(String[] args) {
    // // Look for flag
    // String docGeneratorFlag = "-" + LaraiKeys.getDocGeneratorFlag();
    //
    // int flagIndex = args.length == 0 ? -1 : docGeneratorFlag.equals(args[0]) ? 0 : -1;
    //
    // // int flagIndex = IntStream.range(0, args.length)
    // // .filter(index -> docGeneratorFlag.equals(args[index]))
    // // .findFirst()
    // // .orElse(-1);
    //
    // if (flagIndex == -1) {
    // return Optional.empty();
    // }
    //
    // List<String> laraDocArgs = new ArrayList<>();
    // laraDocArgs.add("--weaver");
    // laraDocArgs.add(engine.getClass().getName());
    //
    // for (int i = flagIndex + 1; i < args.length; i++) {
    // laraDocArgs.add(args[i]);
    // }
    //
    // SpecsLogs.debug("Launching lara-doc with flags '" + laraDocArgs + "'");
    //
    // int docResults = LaraDocLauncher.execute(laraDocArgs.toArray(new String[0]));
    //
    // return Optional.of(docResults != -1);
    // }

    private Boolean executeDocGenerator(String[] args) {

        // First index is the task flag
        int flagIndex = 0;

        List<String> laraDocArgs = new ArrayList<>();
        laraDocArgs.add("--weaver");
        laraDocArgs.add(engine.getClass().getName());

        for (int i = flagIndex + 1; i < args.length; i++) {
            laraDocArgs.add(args[i]);
        }

        SpecsLogs.debug("Launching lara-doc with flags '" + laraDocArgs + "'");

        int docResults = LaraDocLauncher.execute(laraDocArgs.toArray(new String[0]));

        return docResults != -1;
    }

    // /**
    // * @deprecated
    // * @param args
    // * @return
    // */
    // @Deprecated
    // private Optional<Boolean> runServer(String[] args) {
    // // Look for flag
    // String serverFlag = "-" + LaraiKeys.getServerFlag();
    //
    // int flagIndex = args.length == 0 ? -1 : serverFlag.equals(args[0]) ? 0 : -1;
    //
    // // int flagIndex = IntStream.range(0, args.length)
    // // .filter(index -> serverFlag.equals(args[index]))
    // // .findFirst()
    // // .orElse(-1);
    //
    // if (flagIndex == -1) {
    // return Optional.empty();
    // }
    //
    // SpecsLogs.info("Launching weaver " + engine.getName() + " in server mode");
    //
    // LaraI.setServerMode();
    //
    // // Remove flag
    // String[] newArgs = new String[args.length - 1];
    // int currentIndex = 0;
    // for (int i = 0; i < args.length; i++) {
    // if (i == flagIndex) {
    // continue;
    // }
    //
    // newArgs[currentIndex] = args[i];
    // currentIndex++;
    // }
    //
    // // Run server
    // new WeaverServer(engine).execute(newArgs);
    //
    // return Optional.of(true);
    // }

    private Boolean executeServer(String[] args) {

        // First index is the task flag
        int flagIndex = 0;

        SpecsLogs.info("Launching weaver " + engine.getName() + " in server mode");

        LaraI.setServerMode();

        // Remove flag
        String[] newArgs = new String[args.length - 1];
        int currentIndex = 0;
        for (int i = 0; i < args.length; i++) {
            if (i == flagIndex) {
                continue;
            }

            newArgs[currentIndex] = args[i];
            currentIndex++;
        }

        // Run server
        new WeaverServer(engine).execute(newArgs);

        return true;
    }

    public String[] executeParallel(String[][] args, int threads, List<String> weaverCommand) {
        return executeParallel(args, threads, weaverCommand, SpecsIo.getWorkingDir().getAbsolutePath());
    }

    /**
     * 
     * @param args
     * @param threads
     * @param weaverCommand
     * @param workingDir
     * @return an array with the same size as the number if args, with strings representing JSON objects that represent
     *         the outputs of the execution. The order of the results is the same as the args
     */
    public String[] executeParallel(String[][] args, int threads, List<String> weaverCommand, String workingDir) {

        var workingFolder = SpecsIo.sanitizeWorkingDir(workingDir);

        var customThreadPool = threads > 0 ? new ForkJoinPool(threads) : new ForkJoinPool();

        // Choose executor
        Function<String[], Boolean> clavaExecutor = weaverCommand.isEmpty() ? this::executeSafe
                : weaverArgs -> this.executeOtherJvm(weaverArgs, weaverCommand, workingFolder);

        SpecsLogs.info("Launching " + args.length + " instances of weaver " + engine.getName()
                + " in parallel, using a parallelism level of "
                + threads);

        if (!weaverCommand.isEmpty()) {
            SpecsLogs.info("Each weaver instance will run on a separate process, using the command " + weaverCommand);
        }

        // Create paths for the results
        List<File> resultFiles = new ArrayList<>();
        var resultsFolder = SpecsIo.getTempFolder("weaver_parallel_results_" + UUID.randomUUID());
        SpecsLogs.debug(() -> "Create temporary folder for storing results of weaver parallel execution: "
                + resultsFolder.getAbsolutePath());

        for (int i = 0; i < args.length; i++) {
            resultFiles.add(new File(resultsFolder, "weaver_parallel_result_" + i + ".json"));
        }

        try {

            // Adapt the args so that each execution produces a result file
            String[][] adaptedArgs = new String[args.length][];
            for (int i = 0; i < args.length; i++) {
                var newArgs = Arrays.copyOf(args[i], args[i].length + 2);
                newArgs[newArgs.length - 2] = "-r";
                newArgs[newArgs.length - 1] = resultFiles.get(i).getAbsolutePath();
                adaptedArgs[i] = newArgs;
            }

            // var results =

            // Launch tasks
            List<ForkJoinTask<Boolean>> tasks = new ArrayList<>();
            for (var weaverArgs : adaptedArgs) {
                tasks.add(customThreadPool.submit(() -> clavaExecutor.apply(weaverArgs)));
            }

            // Stop accepting tasks, allows threads to be reclaimed
            customThreadPool.shutdown();

            // Wait for tasks
            for (var task : tasks) {
                task.get();
            }

            // Arrays.asList(adaptedArgs).stream()
            // .map(clavaExecutor)
            // .forEach(null);
            // .collect(Collectors.toList())

            // customThreadPool.submit(

            // customThreadPool.submit(() -> Arrays.asList(adaptedArgs).parallelStream()
            // .map(clavaExecutor)
            // .collect(Collectors.toList())).get();

            // Find the file for each execution
            return collectResults(resultFiles);

            // return results.stream()
            // .filter(result -> result == false)
            // .findFirst()
            // .orElse(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return collectResults(resultFiles);
        } catch (ExecutionException e) {
            SpecsLogs.info("Unrecoverable exception while executing parallel instances of weaver " + engine.getName()
                    + ": " + e);
            return collectResults(resultFiles);
        } finally {
            SpecsIo.deleteFolder(resultsFolder);
        }

    }

    private String[] collectResults(List<File> resultFiles) {
        List<String> results = new ArrayList<>();
        for (var resultFile : resultFiles) {
            // If file does not exist, create empty object
            if (!resultFile.isFile()) {
                results.add("{}");
                continue;
            }

            results.add(SpecsIo.read(resultFile));
        }

        return results.toArray(size -> new String[size]);
    }

    private boolean executeSafe(String[] args) {
        try {
            // Create new WeaverEngine
            var weaverEngineConstructor = getDefaultConstructor();
            return new WeaverLauncher(weaverEngineConstructor.newInstance()).launch(args);

        } catch (Exception e) {
            // throw new RuntimeException("Could not execute", e);
            SpecsLogs.info("Exception during weaver execution: " + e);
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Constructor<WeaverEngine> getDefaultConstructor() {
        var constructors = engine.getClass().getConstructors();
        for (var constructor : constructors) {
            if (constructor.getParameterCount() != 0) {
                continue;
            }

            return (Constructor<WeaverEngine>) constructor;
        }

        throw new RuntimeException("Could not find default constructor for WeaverEngine " + engine.getClass());
    }

    private boolean executeOtherJvm(String[] args, List<String> weaverCommand, File workingDir) {
        try {
            // DEBUG
            // if (true) {
            // return ClavaWeaverLauncher.execute(args);
            // }

            List<String> newArgs = new ArrayList<>();
            // newArgs.add("java");
            // newArgs.add("-jar");
            // newArgs.add("Clava.jar");
            // newArgs.add("/usr/local/bin/clava");
            newArgs.addAll(weaverCommand);
            newArgs.addAll(Arrays.asList(args));

            // ClavaLog.info(() -> "Launching Clava on another JVM with command: " + newArgs);

            // var result = SpecsSystem.run(newArgs, SpecsIo.getWorkingDir());
            var result = SpecsSystem.run(newArgs, workingDir);

            return result == 0;

            // return execute(args);
        } catch (Exception e) {
            SpecsLogs.info("Exception during weaver execution: " + e);
            e.printStackTrace();
            return false;
        }
    }

    public static String[] executeParallelStatic(String[][] args, int threads, List<String> weaverCommand,
            String workingDir) {

        return new WeaverLauncher(WeaverEngine.getThreadLocalWeaver()).executeParallel(args, threads, weaverCommand,
                workingDir);
    }

}
