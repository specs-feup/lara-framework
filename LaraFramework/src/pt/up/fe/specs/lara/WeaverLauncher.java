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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.google.gson.Gson;

import larai.LaraI;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.SpecsSystem;

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

        copyAndProcessApiFolder(engine.getApiManager().getCoreFolder(), outputFolder, keepLara);
        for (var apiFolder : engine.getApiManager().getNpmApiFolders()) {
            copyAndProcessApiFolder(apiFolder, outputFolder, keepLara);
        }

        return true;
    }

    private void copyAndProcessApiFolder(File apiFolder, File outputFolder, boolean keepLara) {

        SpecsLogs.info("Extracting APIs in folder '" + apiFolder.getAbsolutePath() + "' to '"
                + outputFolder.getAbsolutePath() + "'");

        var apiFiles = SpecsIo.getFilesRecursive(apiFolder);

        for (var apiFile : apiFiles) {
            var fileLocation = SpecsIo.getRelativePath(apiFolder, apiFile);

            var destinationFile = new File(outputFolder, fileLocation);
            var fileContents = SpecsIo.read(apiFile);

            SpecsLogs.info("Writing file " + destinationFile);
            SpecsIo.write(destinationFile, fileContents);
        }

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
    }

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
        Function<String[], WeaverResult> weaverExecutor = weaverCommand.isEmpty() ? this::executeSafe
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

            // Launch tasks
            List<ForkJoinTask<WeaverResult>> tasks = new ArrayList<>();
            for (var weaverArgs : adaptedArgs) {
                tasks.add(customThreadPool.submit(() -> weaverExecutor.apply(weaverArgs)));
            }

            // Stop accepting tasks, allows threads to be reclaimed
            customThreadPool.shutdown();

            // Wait for tasks
            for (var task : tasks) {
                var result = task.get();

                var e = result.getException().orElse(null);

                if (e == null) {
                    continue;
                }

                // If there is an exception, look for the results file and write an error json
                try (StringWriter stringWriter = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(stringWriter)) {

                    e.printStackTrace(printWriter);
                    var stackTrace = stringWriter.toString();

                    var taskArgs = result.getArgs();

                    // Get JSON results file
                    var indexOfR = taskArgs.length - 2;

                    if (taskArgs[indexOfR] != "-r") {
                        throw new RuntimeException(
                                "Expected second to last argument to be '-r': " + Arrays.toString(taskArgs));
                    }

                    var resultsFile = taskArgs[indexOfR + 1];

                    var results = new LinkedHashMap<String, Object>();
                    var lastCause = SpecsSystem.getLastCause(e);
                    var causeMessage = lastCause.getMessage() != null ? lastCause.getMessage() : "<no cause message>";
                    results.put("error", SpecsStrings.escapeJson(causeMessage));
                    results.put("args", args);
                    results.put("stackTrace", SpecsStrings.escapeJson(stackTrace));

                    // var resultsReturn = new HashMap<>();
                    // // Must be inside an array
                    // resultsReturn.put("output", "[" + new Gson().toJson(results) + "]");

                    SpecsIo.write(new File(resultsFile), new Gson().toJson(results));

                    SpecsLogs.info("Exception during weaver execution:\n" + stackTrace);
                } catch (Exception ex) {
                    SpecsLogs.info("Exception while retrieving error information: " + e);
                    ex.printStackTrace();
                }
            }

            // Find the file for each execution
            return collectResults(resultFiles);
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
            // System.out.println("CONTENTS:\n" + SpecsIo.read(resultFile));
            results.add(SpecsIo.read(resultFile));
        }

        return results.toArray(size -> new String[size]);
    }

    private WeaverResult executeSafe(String[] args) {
        try {
            // Create new WeaverEngine
            var weaverEngineConstructor = getDefaultConstructor();
            var weaverLauncher = new WeaverLauncher(weaverEngineConstructor.newInstance());
            return new WeaverResult(args, weaverLauncher.launch(args));

        } catch (Exception e) {
            // throw new RuntimeException("Could not execute", e);
            // SpecsLogs.info("Exception during weaver execution: " + e);
            // e.printStackTrace();
            return new WeaverResult(args, e);
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

    private WeaverResult executeOtherJvm(String[] args, List<String> weaverCommand, File workingDir) {
        try {
            List<String> newArgs = new ArrayList<>();
            newArgs.addAll(weaverCommand);
            newArgs.addAll(Arrays.asList(args));

            var result = SpecsSystem.run(newArgs, workingDir);

            return new WeaverResult(args, result == 0);

        } catch (Exception e) {
            return new WeaverResult(args, e);
        }
    }

    public static String[] executeParallelStatic(String[][] args, int threads, List<String> weaverCommand,
            String workingDir) {

        return new WeaverLauncher(WeaverEngine.getThreadLocalWeaver()).executeParallel(args, threads, weaverCommand,
                workingDir);
    }

}
