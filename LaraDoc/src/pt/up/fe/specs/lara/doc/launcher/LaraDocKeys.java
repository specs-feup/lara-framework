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

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import pt.up.fe.specs.util.utilities.StringList;

public interface LaraDocKeys {

    // Packages + Pastas de sources OR String + File
    DataKey<StringList> SOURCES = KeyFactory.stringList("sources")
            .setLabel("Sources (<packageName>:<folder>)");

    // Language Specification
    // DataStoreList? (resource, folder...)

    // DataStoreList de credenciais para fazer upload (FTP, SFTP...)

    StoreDefinition STORE_DEFINITION = new StoreDefinitionBuilder("LaraDoc")
            .addKeys(SOURCES)
            .build();

}
