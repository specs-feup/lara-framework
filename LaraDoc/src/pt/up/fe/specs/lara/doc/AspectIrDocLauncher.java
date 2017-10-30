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

import pt.up.fe.specs.lara.doc.data.LaraDocTop;
import pt.up.fe.specs.lara.doc.jsdocgen.BasicHtmlGenerator;
import pt.up.fe.specs.lara.doc.parser.LaraDocParser;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;

public class AspectIrDocLauncher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();
        // String inputPath = "C:\\Users\\joaobispo\\Repositories\\lara-framework\\LaraApi\\src-lara-base\\lara\\";
        // String inputPath = "C:\\Users\\joaobispo\\Repositories\\lara-dse\\";
        // String inputPath =
        // "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara-base\\lara\\metrics\\ExecutionTimeMetric.lara";
        String laraApi = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara-base\\";
        String laraiApi = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LARAI\\src-lara";
        String clavaApi = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\clava\\ClavaLaraApi\\src-lara\\clava\\";

        String antarexApi = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\specs-lara\\ANTAREX\\AntarexClavaApi\\src-lara\\clava\\";

        String laraDse = "C:\\Users\\JoaoBispo\\Desktop\\shared\\antarex\\lara-dse\\";
        File outputFolder = SpecsIo.mkdir("C:\\Users\\joaobispo\\Desktop\\laradoc");
        // LaraDocFiles laraDocFiles = new LaraDoc()
        // // .addPath("Clava API", new File(laraApi))
        // // .addPath("Clava API", new File(laraiApi))
        // // .addPath("Clava API", new File(clavaApi))
        // // .addPath("ANTAREX API", new File(antarexApi))
        // .addPath("Lara DSE", new File(laraDse))
        // .buildLaraDoc();
        //
        // // Generate documentation
        // LaraDocHtmlGenerator generator = new LaraDocHtmlGenerator(new BasicHtmlGenerator(), outputFolder);
        // generator.generateDoc(laraDocFiles);

        LaraDocTop laraDocTop = new LaraDocParser()
                .addPath("Clava API", new File(laraApi))
                .addPath("Clava API", new File(laraiApi))
                .addPath("Clava API", new File(clavaApi))
                .addPath("ANTAREX API", new File(antarexApi))
                // .addPath("Lara DSE", new File(laraDse))
                .buildLaraDoc();

        System.out.println("LARA DOC TOP:\n" + laraDocTop);

        // Generate documentation
        LaraDocHtmlGenerator generator = new LaraDocHtmlGenerator(new BasicHtmlGenerator(), outputFolder);
        generator.generateDoc(laraDocTop);

        // System.out.println("TOP LEVEL PACKAGE:" + laraDocFiles.getTopLevelPackage());
        /*
        // Add documentation to modules
        // for (LaraDocPackage laraPackage : laraDocFiles.getPackages()) {
        // for (LaraDocModule module : laraPackage.getModules()) {
        for (LaraDocModule module : laraDocFiles.getModules()) {
            // Add info about a module to the same AspectIrDoc
            AspectIrDocBuilder laraDocBuilder = new AspectIrDocBuilder();
        
            // Parse files
            for (File laraFile : module.getLaraFiles()) {
                Optional<Aspects> aspectIr = LaraToJs.parseLara(laraFile);
                if (!aspectIr.isPresent()) {
                    continue;
                }
        
                laraDocBuilder.parse(aspectIr.get());
            }
        
            // Build AspectIrDoc and associate with module
            module.setDocumentation(laraDocBuilder.build());
        }
        // }
        // }
        */

    }

}
