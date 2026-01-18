/**
 * Copyright 2016 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.joptions.config.interpreter;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import javax.swing.JFileChooser;

import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

public interface LaraiKeys {

    DataKey<File> LARA_FILE = KeyFactory.file("aspect",  "js", "mjs").setLabel("Aspect");

    DataKey<String> ASPECT_ARGS = KeyFactory.string("argv").setLabel("Aspect Arguments")
            .setCustomGetter(LaraIKeyFactory::customGetterLaraArgs);

    DataKey<FileList> WORKSPACE_FOLDER = LaraIKeyFactory.fileList("workspace", JFileChooser.FILES_AND_DIRECTORIES,
            Collections.emptyList())
            .setLabel("Sources");

    DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("output", false)
            .setLabel("Output Folder")
            // According to LaraIDataStore
            .setDefault(() -> new File("."));

    DataKey<OptionalFile> LOG_FILE = LaraIKeyFactory.optionalFile("log", false).setLabel("Use Log File");

    DataKey<Boolean> DEBUG_MODE = KeyFactory.bool("debug").setLabel("Debug Mode");

    DataKey<FileList> JAR_PATHS = LaraIKeyFactory
            .fileList("jarPaths", JFileChooser.FILES_AND_DIRECTORIES, Set.of("jar"))
            .setLabel("Paths to JARs")
            .setDefault(FileList::newInstance);

    DataKey<Boolean> SHOW_HELP = KeyFactory.bool("help").setLabel("Show Help");

    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("LaraI Options")
            .addKeys(LARA_FILE, ASPECT_ARGS, WORKSPACE_FOLDER, OUTPUT_FOLDER,
                    LOG_FILE, DEBUG_MODE, JAR_PATHS, SHOW_HELP)
            .build();

}
