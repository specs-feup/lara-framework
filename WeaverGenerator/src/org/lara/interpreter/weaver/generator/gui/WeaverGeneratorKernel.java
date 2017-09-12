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

package org.lara.interpreter.weaver.generator.gui;

import org.lara.interpreter.weaver.generator.options.WeaverGeneratorKeys;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppKernel;

public class WeaverGeneratorKernel implements AppKernel {

    @Override
    public int execute(DataStore options) {

	System.out.println("NODE TYPE:" + options.get(WeaverGeneratorKeys.NODE_TYPE));

	System.out.println("SPEC FOLDER:" + options.get(WeaverGeneratorKeys.LANGUAGE_SPECIFICATION_FOLDER));

	// Process options and/or
	// Execute application
	return 0;
    }

}
