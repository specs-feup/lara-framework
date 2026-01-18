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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.defaultweaver.options;

import java.io.File;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

public interface DefaulWeaverKeys {
    DataKey<Boolean> TEST_BOOLEAN = KeyFactory.bool("testing bool").setLabel("Test Boolean");

    DataKey<String> TEST_STRING = KeyFactory.string("testing string").setLabel("Test String");
    DataKey<File> TEST_FILE = KeyFactory.file("testing file").setLabel("Main Aspect");

}
