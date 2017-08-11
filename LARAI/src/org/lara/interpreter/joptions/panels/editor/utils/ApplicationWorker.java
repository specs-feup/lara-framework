/*
 * Copyright 2010 SPeCS Research Group.
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

package org.lara.interpreter.joptions.panels.editor.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSwing;

/**
 * Launches a Task in a different thread.
 *
 * TODO: Extract Runnable ApplicationRunner from this class.
 *
 * @param T
 *            the type of argument the task receives. Old version used {@link DataStore}
 * @author Joao Bispo
 */
public abstract class ApplicationWorker<T> {

    private ExecutorService workerExecutor;

    public ApplicationWorker() {

	workerExecutor = null;
    }

    /**
     * Executes the application in another thread.
     * 
     * @param options
     */
    public void execute(T options) {

	// Run
	ExecutorService monitor = Executors.newSingleThreadExecutor();
	monitor.submit(() -> runner(options));

    }

    /**
     * To be run on Monitor thread, so the Gui is not waiting for the result of task.
     *
     * @param options
     */
    private void runner(T setup) {
	// event before task
	start();

	// Create task
	Callable<Integer> task = getTask(setup);

	// Submit task
	workerExecutor = Executors.newSingleThreadExecutor();
	Future<Integer> future = workerExecutor.submit(task);

	// Check if task finishes
	Integer result = null;
	try {
	    result = future.get();
	} catch (InterruptedException ex) {
	    Thread.currentThread().interrupt(); // ignore/reset
	} catch (ExecutionException ex) {
	    if (!workerExecutor.isShutdown()) {
		showExceptionMessage(ex);
	    }
	}

	if (result == null) {
	    SpecsLogs.msgInfo("Application execution could not proceed.");
	} else if (result.compareTo(0) != 0) {
	    SpecsLogs.msgInfo("*Application Stopped*");
	    SpecsLogs.msgLib("Worker return value: " + result);
	}

	// event after task
	end();
    }

    /**
     * Runt the start event
     */
    private void start() {
	SpecsSwing.runOnSwing(new Runnable() {

	    @Override
	    public void run() {
		onStart();
	    }
	});

    }

    /**
     * Run the "end" event
     */
    private void end() {
	SpecsSwing.runOnSwing(new Runnable() {

	    @Override
	    public void run() {
		onEnd();
	    }
	});

    }

    /**
     * Builds a task out of the application
     * 
     * @return
     */
    protected abstract Callable<Integer> getTask(T setup);

    /**
     * Event that executes before the task starts
     */
    protected abstract void onStart();

    /**
     * Event that executes after the task starts, even if it failed to execute
     */
    protected abstract void onEnd();

    public void shutdown() {
	if (workerExecutor == null) {
	    SpecsLogs.getLogger().warning("Application is not running.");
	    return;
	}

	workerExecutor.shutdownNow();
    }

    private static void showExceptionMessage(ExecutionException ex) {
	String prefix = " happened while executing the application";

	Throwable ourCause = ex.getCause();

	if (ourCause == null) {
	    SpecsLogs.msgWarn("\nAn Exception" + prefix + ", but could not get cause.");
	} else {

	    SpecsLogs.msgInfo("");
	    SpecsLogs.msgWarn(ourCause.toString(), ourCause);
	}
    }

}
