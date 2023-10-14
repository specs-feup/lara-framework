/**
 * Copyright 2016 SPeCS.
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

package org.lara.interpreter.joptions.gui;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.lara.interpreter.cli.OptionsParser;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import larai.LaraI;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraLauncher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();

        launch(args, new DefaultWeaver());
    }

    /**
     * Launch LaraI with the given engine and the input arguments. If no arguments are given a GUI is launched
     *
     * <p>
     * Can throw exceptions.
     *
     * @param args
     * @param engine
     */
    public static boolean launch(String[] args, WeaverEngine engine) {
        return LaraI.exec(args, engine);
    }


    /**
     * Execute LaraI with the given configuration file and other options
     *
     * @param cmd
     */
    public static void launch(WeaverEngine weaverEngine, File configFile, boolean guiMode) {
        StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
        AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
        DataStore laraiStore = persistence.loadData(configFile);
        LaraI.exec(laraiStore, weaverEngine);
    }

    /**
     * Execute LaraI with the given configuration file and other options
     *
     * @param cmd
     */
    public static void launch(WeaverEngine weaverEngine, File configFile, boolean guiMode, String fileName,
            CommandLine cmd) {

        StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
        AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
        DataStore laraiStore = persistence.loadData(configFile);
        LaraI.exec(laraiStore, weaverEngine);
    }
}
