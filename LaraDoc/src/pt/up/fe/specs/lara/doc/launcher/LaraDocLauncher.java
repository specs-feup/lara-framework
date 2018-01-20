/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.lara.doc.launcher;

import java.util.Arrays;
import java.util.List;

import org.suikasoft.jOptions.JOptionsUtils;
import org.suikasoft.jOptions.app.App;
import org.suikasoft.jOptions.app.AppKernel;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;

import pt.up.fe.specs.util.SpecsSystem;

public class LaraDocLauncher {

    public static void main(String[] args) {
        execute(Arrays.asList(args));
    }

    public static int execute(List<String> args) {
        SpecsSystem.programStandardInit();

        StoreDefinition definition = LaraDocKeys.STORE_DEFINITION;
        AppPersistence persistence = new XmlPersistence(definition);
        AppKernel kernel = new LaraDocKernel();

        App laradocApp = App.newInstance(definition, persistence, kernel);

        return JOptionsUtils.executeApp(laradocApp, args);
    }

}
