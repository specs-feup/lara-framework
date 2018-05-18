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
import java.util.Arrays;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.arguments.ArgumentsParser;

import pt.up.fe.specs.util.SpecsSystem;

public class LaraDocLauncher {

    public static final DataKey<File> OUTPUT_FOLDER = KeyFactory.folder("output_folder", true)
            .setLabel("The folder where the documentation files will be written");

    public static final DataKey<String> EXCLUDE_PREFIX = KeyFactory.string("exclude_prefix")
            .setLabel("If present, will exclude all code elements that start with the given prefix");

    public static final DataKey<String> WEAVER_CLASS = KeyFactory.string("weaver_class")
            .setLabel("The class of the LARA weaver we are generating documentation for");

    private static final ArgumentsParser ARGUMENTS_PARSER = new ArgumentsParser()
            .add(OUTPUT_FOLDER, "--output", "-o")
            .add(EXCLUDE_PREFIX, "--exclude", "-e")
            .add(WEAVER_CLASS, "--weaver", "-w");

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();

        ARGUMENTS_PARSER.execute(data -> 0, Arrays.asList(args));

    }

    public static void main_old(String[] args) {
        ARGUMENTS_PARSER.execute(data -> {
            System.out.print(data);
            return 0;
        }, Arrays.asList(args));

        /*
        String inputFolder = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara-base\\";
        // String inputFolder = "C:\\Users\\JoaoBispo\\Desktop\\shared\\antarex\\lara-dse\\";
        // String outputFolder = "C:\\Users\\JoaoBispo\\Desktop\\jstest\\auto-js-laradse-v2";
        String outputFolder = "C:\\Users\\JoaoBispo\\Desktop\\jstest\\laradoc-lara_api";
        
        // new LaraDoc(new DefaultWeaver(), new File(inputFolder), new File(outputFolder)).convertFiles();
        long laraDocStart = System.nanoTime();
        LaraDocFiles laraDocFiles = new LaraDoc().addPath("Default Package", new File(inputFolder)).buildLaraDoc();
        System.out.println(SpecsStrings.takeTime("LaraDocFiles", laraDocStart));
        
        long laraDocGeneratorStart = System.nanoTime();
        LaraDocHtmlGenerator generator = new LaraDocHtmlGenerator(new JsDocNodeGenerator(),
                // LaraDocGenerator generator = new LaraDocGenerator(new DocumentationGenerator(),
                SpecsIo.mkdir(outputFolder));
        generator.generateDoc(laraDocFiles);
        
        System.out.println(SpecsStrings.takeTime("LaraDocGenerator", laraDocGeneratorStart));
        // System.out.println("LARA DOC FILES:" + laraDocFiles);
         *
         */
    }

}
