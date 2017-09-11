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

package org.lara.interpreter.weaver.generator.options;

import java.io.File;

import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.generator.options.utils.ClassProvider;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

public interface WeaverGeneratorKeys {
    DataKey<String> WEAVER_NAME = KeyFactory.string("Weaver Name", GenConstants.getDefaultWeaverName());
    DataKey<File> LANGUAGE_SPECIFICATION_FOLDER = KeyFactory.folder("Language Specification Folder")
            .setDefault(() -> GenConstants.getDefaultXMLDir());
    DataKey<Boolean> ABSTRACT_GETTERS = KeyFactory.bool("Abstract Getters");
    DataKey<Boolean> ADD_EVENTS = KeyFactory.bool("Add weaving events");
    DataKey<Boolean> IMPL_MODE = KeyFactory.bool("Create \"<attr>Impl\" methods");
    DataKey<ClassProvider> NODE_TYPE = WeaverGeneratorKeyFactory.classProvider("Node Class")
            .setDefault(() -> ClassProvider.newInstance(Object.class.getName()));
    DataKey<String> PACKAGE = KeyFactory.string("Package", GenConstants.getDefaultPackage());
    DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("Output Folder", true)
            .setDefault(() -> GenConstants.getDefaultOutputDir());

    DataKey<Boolean> SHOW_HELP = KeyFactory.bool("show_help");
    // DataKey<OptionsParser> CLI_PARSER = KeyFactory.object("cli_parser", OptionsParser.class);

}
