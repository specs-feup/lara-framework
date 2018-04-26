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

import java.io.File;
import java.util.Arrays;

import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.JOptionsUtils;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.App;
import org.suikasoft.jOptions.app.AppKernel;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraUnitLauncher {

    public static void main(String[] args) {
        execute(args);
    }

    public static int execute(String[] args) {
        SpecsSystem.programStandardInit();

        App laraUnitApp = buildApp();

        return JOptionsUtils.executeApp(laraUnitApp, Arrays.asList(args));
    }

    private static App buildApp() {

        StoreDefinition definition = LaraUnitOptions.STORE_DEFINITION;
        AppPersistence persistence = new XmlPersistence(definition);
        AppKernel kernel = LaraUnitLauncher::execute;

        return App.newInstance(definition, persistence, kernel);
    }

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
}
