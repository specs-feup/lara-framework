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

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

public interface LaraUnitOptions {

    static String getAppName() {
        return "LARA Unit Tester";
    }

    /**
     * Folder that will be the LARA workspace folder. By default returns ./
     */
    DataKey<File> BASE_FOLDER = KeyFactory.folder("lara-unit-base", false)
            .setLabel("LARA base folder");

    /**
     * Folder with files to test. Can be empty.
     */
    // DataKey<File> TEST_FOLDER = KeyFactory.folder("lara-unit-test", false)
    DataKey<File> TEST_FOLDER = KeyFactory.file("lara-unit-test")
            // .setLabel("Folder with tests")
            .setLabel("Path with tests")
            // Disables default
            .setDefault(() -> null);

    /**
     * The full class name of the weaver to be used. It must be present in the classpath
     */
    DataKey<String> WEAVER_CLASS = KeyFactory.string("lara-unit-weaver")
            .setLabel("Weaver class name");

    DataKey<Boolean> METRICS = KeyFactory.bool("metrics")
            .setLabel("Enable logging of metrics");

    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder(getAppName())
            .addKeys(BASE_FOLDER, TEST_FOLDER, WEAVER_CLASS, METRICS)
            .build();
}
