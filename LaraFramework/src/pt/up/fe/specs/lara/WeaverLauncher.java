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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import larai.LaraI;
import pt.up.fe.specs.lara.doc.LaraDocLauncher;
import pt.up.fe.specs.lara.unit.LaraUnitLauncher;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

/**
 * Utility methods what weavers can use to bootstrap execution.
 * 
 * @author JoaoBispo
 *
 */
public class WeaverLauncher {

    private final WeaverEngine engine;

    public WeaverLauncher(WeaverEngine engine) {
        this.engine = engine;
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

        return LaraI.exec(args, engine);
    }

    private Optional<Boolean> runUnitTester(String[] args) {
        // Look for flag
        String unitTestingFlag = "-" + LaraiKeys.getUnitTestFlag();

        int flagIndex = IntStream.range(0, args.length)
                .filter(index -> unitTestingFlag.equals(args[index]))
                .findFirst()
                .orElse(-1);

        if (flagIndex == -1) {
            return Optional.empty();
        }

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

        return Optional.of(unitResults == 0);
    }

    private Optional<Boolean> runDocGenerator(String[] args) {
        // Look for flag
        String docGeneratorFlag = "-" + LaraiKeys.getDocGeneratorFlag();

        int flagIndex = IntStream.range(0, args.length)
                .filter(index -> docGeneratorFlag.equals(args[index]))
                .findFirst()
                .orElse(-1);

        if (flagIndex == -1) {
            return Optional.empty();
        }

        List<String> laraDocArgs = new ArrayList<>();
        laraDocArgs.add("--weaver");
        laraDocArgs.add(engine.getClass().getName());

        for (int i = flagIndex + 1; i < args.length; i++) {
            laraDocArgs.add(args[i]);
        }

        SpecsLogs.debug("Launching lara-doc with flags '" + laraDocArgs + "'");

        int docResults = LaraDocLauncher.execute(laraDocArgs.toArray(new String[0]));

        return Optional.of(docResults != -1);
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
            customThreadPool.submit(() -> Arrays.asList(adaptedArgs).parallelStream()
                    .map(clavaExecutor)
                    .collect(Collectors.toList())).get();

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
            SpecsLogs.info("Exception during weaver execution: " + e);
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
            return false;
        }
    }

    public static String[] executeParallelStatic(String[][] args, int threads, List<String> weaverCommand,
            String workingDir) {

        return new WeaverLauncher(WeaverEngine.getThreadLocalWeaver()).executeParallel(args, threads, weaverCommand,
                workingDir);
    }

}
