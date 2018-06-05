/**
 * Copyright 2018 SPeCS.
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

package pt.up.fe.specs.lara.unit;

import static pt.up.fe.specs.lara.unit.LaraUnitOptions.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.arguments.ArgumentsParser;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraUnitLauncher {

    private static final ArgumentsParser ARGUMENTS_PARSER = new ArgumentsParser()
            .add(BASE_FOLDER, "--workspace", "-p")
            .add(TEST_FOLDER, "--test", "-t")
            .add(WEAVER_CLASS, "--weaver", "-w")
            .add(METRICS, "--metrics", "-m");

    public static void main(String[] args) {
        execute(args);
    }

    public static int execute(String[] args) {
        SpecsSystem.programStandardInit();

        return ARGUMENTS_PARSER.execute(LaraUnitLauncher::execute, Arrays.asList(args));
        /*
        App laraUnitApp = buildApp();
        
        return JOptionsUtils.executeApp(laraUnitApp, Arrays.asList(args));
        */
    }

    /*
    private static App buildApp() {
    
        StoreDefinition definition = LaraUnitOptions.STORE_DEFINITION;
        AppPersistence persistence = new XmlPersistence(definition);
        AppKernel kernel = LaraUnitLauncher::execute;
    
        return App.newInstance(definition, persistence, kernel);
    }
    */

    /**
     * The main method of the app.
     * 
     * @return
     */
    public static int execute(DataStore options) {

        // Get the base folder
        File baseFolder = options.get(LaraUnitOptions.BASE_FOLDER);

        // Get the test folder
        File testFolder = options.hasValue(LaraUnitOptions.TEST_FOLDER) ? options.get(LaraUnitOptions.TEST_FOLDER)
                : null;

        String weaverClassname = options.get(LaraUnitOptions.WEAVER_CLASS);
        if (weaverClassname.isEmpty()) {
            weaverClassname = DefaultWeaver.class.getName();
        }

        boolean logMetrics = options.get(LaraUnitOptions.METRICS);

        WeaverEngine weaverEngine = null;
        try {
            Class<?> weaverEngineClass = Class.forName(weaverClassname);
            weaverEngine = (WeaverEngine) weaverEngineClass.newInstance();
        } catch (Exception e) {
            SpecsLogs.msgInfo("Could not create weaver engine:");
            String message = e.getMessage();
            if (message.isEmpty()) {
                message = "Could not find class '" + weaverClassname + "', please verify if the classpath is correct";
            }

            SpecsLogs.msgInfo(message);
            return -1;
        }

        LaraUnitTester laraUnitTester = new LaraUnitTester(weaverEngine, logMetrics);

        LaraUnitReport laraUnitResport = laraUnitTester.testFolder(baseFolder, testFolder);

        SpecsLogs.msgInfo("\nLaraUnit test report");
        SpecsLogs.msgInfo(laraUnitResport.getReport());

        return laraUnitResport.isSuccess() ? 0 : -1;

    }

    public static int execute(DataStore dataStore, String weaverClassname) {

        DataStore laraUnitData = DataStore.newInstance("LaraUnitData");

        laraUnitData.add(LaraUnitOptions.WEAVER_CLASS, weaverClassname);

        File testFile = dataStore.get(LaraiKeys.LARA_FILE);

        laraUnitData.add(LaraUnitOptions.TEST_FOLDER, testFile);

        List<File> includes = dataStore.get(LaraiKeys.INCLUDES_FOLDER).getFiles();
        if (includes.isEmpty()) {
            SpecsLogs.msgInfo("Expected one include, the base folder of the tests");
            return -1;
        }
        laraUnitData.add(LaraUnitOptions.BASE_FOLDER, includes.get(0));

        SpecsLogs.debug("Launching lara-unit with the following options: " + laraUnitData);

        return LaraUnitLauncher.execute(laraUnitData);
    }
}
