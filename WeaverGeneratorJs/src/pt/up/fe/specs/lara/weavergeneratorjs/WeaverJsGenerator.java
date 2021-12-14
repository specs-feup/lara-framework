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

package pt.up.fe.specs.lara.weavergeneratorjs;

import java.io.File;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppKernel;

public class WeaverJsGenerator implements AppKernel {

    /// DATAKEYS BEGIN

    public static final DataKey<String> WEAVER_NAME = KeyFactory.string("name").setLabel("Name of the weaver")
            .setDefault(null);

    public static final DataKey<String> PACKAGE_NAME = KeyFactory.string("packageJava")
            .setLabel("Package of the Java files")
            .setDefault(null);

    public static final DataKey<String> NODE_CLASS = KeyFactory.string("nodeClass")
            .setLabel("Fully-qualified name of the class of the base AST node")
            .setDefault(null);

    public static final DataKey<File> LANG_SPEC = KeyFactory.existingFolder("langspec")
            .setLabel(
                    "Location of the language-specification files (i.e. location of joinPointModel.xml, artifacts.xml and actionModel.xml")
            .setDefault(null);

    public static final DataKey<File> OUTPUT_JAVA = KeyFactory.folder("outputJava")
            .setLabel("Output folder for generated Java files")
            .setDefault(() -> new File("./src"));

    public static final DataKey<File> OUTPUT_JS = KeyFactory.folder("outputJs")
            .setLabel("Output folder for generated JavaScript files")
            .setDefault(() -> new File("./src-js"));

    /// DATAKEYS END

    @Override
    public int execute(DataStore options) {
        return 0;
    }

}
