/**
 * Copyright 2020 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.lara.commonlang.generator;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;

public class CommonLangGeneratorLauncher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();

        if (args.length == 0) {
            System.out.println("Needs at least one argument, the output folder");
            return;
        }

        var outputFolder = SpecsIo.mkdir(args[0]);
        System.out.println("Using output folder: " + outputFolder.getAbsolutePath());
        new CommonLangGenerator(outputFolder).generate();
    }

}
