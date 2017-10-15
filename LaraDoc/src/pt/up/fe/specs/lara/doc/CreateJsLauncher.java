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

package pt.up.fe.specs.lara.doc;

import java.io.File;

import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.lara.doc.jsdocgen.DummyGenerator;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.SpecsSystem;

public class CreateJsLauncher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();

        if (args.length < 2) {
            SpecsLogs.msgInfo(
                    "Expects at least two arguments, the input path and the output folder. Passed " + args.length);
            return;
        }

        String inputPath = args[0];
        String outputFolder = args[1];
        // String inputFolder =
        // "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara-base\\lara\\";
        // String inputFolder = "C:\\Users\\JoaoBispo\\Desktop\\shared\\antarex\\lara-dse\\";
        // String outputFolder = "C:\\Users\\JoaoBispo\\Desktop\\jstest\\auto-js-laradse-v2";

        // new LaraDoc(new DefaultWeaver(), new File(inputFolder), new File(outputFolder)).convertFiles();
        long laraDocStart = System.nanoTime();
        LaraDocFiles laraDocFiles = new LaraDoc(new File(inputPath)).buildLaraDoc();
        System.out.println(SpecsStrings.takeTime("LaraDocFiles", laraDocStart));

        long laraDocGeneratorStart = System.nanoTime();
        LaraDocHtmlGenerator generator = new LaraDocHtmlGenerator(new DummyGenerator(),
                // LaraDocGenerator generator = new LaraDocGenerator(new DocumentationGenerator(),
                SpecsIo.mkdir(outputFolder));
        generator.generateDoc(laraDocFiles);

        System.out.println(SpecsStrings.takeTime("LaraDocGenerator", laraDocGeneratorStart));

    }

}
