/**
 * Copyright 2021 SPeCS.
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

import java.util.concurrent.TimeUnit;

import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanServer;
import org.gearman.GearmanWorker;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import larai.LaraI;
import pt.up.fe.specs.gearman.GearmanUtils;
import pt.up.fe.specs.gearman.specsworker.GenericSpecsWorker;
import pt.up.fe.specs.gearman.utils.GearmanSecurityManager;

public class WeaverServer {

    private final WeaverEngine engine;
    private final int port;

    public WeaverServer(WeaverEngine engine, int port) {
        this.engine = engine;
        this.port = port;
    }

    public WeaverServer(WeaverEngine engine) {
        this(engine, 4733);
    }

    public void execute(String[] args) {

        // Set SecurityManager to catch potential System.exit() call from workers
        System.setSecurityManager(new GearmanSecurityManager());

        // Create a new gearman system using 8 threads
        final Gearman gearman = Gearman.createGearman();

        // Create a GearmanServer on port 4733, to avoid conflits with already running gearman jobs on the same machine
        // int port = 4733;
        // if (args.length > 0) {
        // try {
        // port = Integer.parseInt(args[0]);
        // } catch (Exception e) {
        // throw new RuntimeException("Expected an integer with the port as an argument", e);
        // }
        // }

        GearmanServer server = GearmanUtils.newServer(gearman, port, args);

        // Create as many workers as threads
        var workers = Runtime.getRuntime().availableProcessors();

        // Remaning workers
        for (int i = 0; i < workers; i++) {

            // Create a GearmanWorker object
            final GearmanWorker worker = gearman.createGearmanWorker();
            worker.addServer(server);

            // Add weaver function
            worker.addFunction("weaver", getWeaverWorker(engine));
        }

    }

    private static GearmanFunction getWeaverWorker(WeaverEngine engine) {
        return new GenericSpecsWorker("weaver", runWeaver(engine),
                message -> message, 365, TimeUnit.DAYS);
    }

    public static GearmanFunction runWeaver(WeaverEngine engine) {
        return (function, data, callback) -> {

            String dataString = new String(data);
            Gson gson = new GsonBuilder().create();
            var args = gson.fromJson(dataString, String[].class);

            LaraI.setServerMode();
            new WeaverLauncher(engine).launch(args);

            return "Execution ended successfully".getBytes();
        };
    }
}
