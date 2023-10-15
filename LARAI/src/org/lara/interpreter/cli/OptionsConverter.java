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
package org.lara.interpreter.cli;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

public class OptionsConverter {

    public static DataStore configFile2DataStore(WeaverEngine weaverEngine, CommandLine cmd) {
        File file = OptionsParser.getConfigFile(cmd);
        StoreDefinition laraiDefinition = OptionsParser.getLaraStoreDefinition(weaverEngine);
        AppPersistence persistence = OptionsParser.getXmlPersistence(laraiDefinition);
        DataStore laraiStore = persistence.loadData(file);

        return laraiStore;
    }
}
