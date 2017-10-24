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
import java.util.Optional;

import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrDocBuilder;
import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.jsdocgen.BasicHtmlGenerator;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;

public class AspectIrDocLauncher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();
        // String inputPath = "C:\\Users\\joaobispo\\Repositories\\lara-framework\\LaraApi\\src-lara-base\\lara\\";
        // String inputPath = "C:\\Users\\joaobispo\\Repositories\\lara-dse\\";
        String inputPath = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara-base\\";
        // String inputPath = "C:\\Users\\JoaoBispo\\Desktop\\shared\\antarex\\lara-dse\\";
        File outputFolder = SpecsIo.mkdir("C:\\Users\\joaobispo\\Desktop\\laradoc");
        LaraDocFiles laraDocFiles = new LaraDoc(new File(inputPath)).buildLaraDoc();

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

        // Generate documentation
        LaraDocHtmlGenerator generator = new LaraDocHtmlGenerator(new BasicHtmlGenerator(), outputFolder);
        generator.generateDoc(laraDocFiles);
    }

}
