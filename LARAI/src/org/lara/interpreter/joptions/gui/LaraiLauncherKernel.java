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

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.app.AppKernel;

import larai.LaraI;

public class LaraiLauncherKernel implements AppKernel {

    private final WeaverEngine engine;

    public LaraiLauncherKernel(WeaverEngine engine) {
        this.engine = engine;
    }

    @Override
    public int execute(DataStore options) {
        // // System.out.println(options);
        try {

            // Check if unit-testing mode
            if (options.get(LaraiKeys.UNIT_TEST_MODE)) {
                boolean result = engine.executeUnitTestMode(options.get(LaraiKeys.UNIT_TEST_ARGS));
                return result ? 0 : 1;
            }

            boolean exec = LaraI.exec(options, engine);
            return exec ? 0 : 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
