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

import org.lara.interpreter.aspectir.Aspects;

import pt.up.fe.specs.lara.doc.aspectir.AspectIrDoc;
import pt.up.fe.specs.lara.doc.data.LaraDocFiles;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.data.LaraDocPackage;
import pt.up.fe.specs.util.SpecsSystem;

public class AspectIrDocMain {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();
        String inputPath = "C:\\Users\\JoaoBispo\\Desktop\\shared\\repositories-programming\\lara-framework\\LaraApi\\src-lara-base\\lara\\";
        // String inputPath = "C:\\Users\\JoaoBispo\\Desktop\\shared\\antarex\\lara-dse\\";

        LaraDocFiles laraDocFiles = new LaraDoc(new File(inputPath), new File("./")).buildLaraDoc();

        for (LaraDocPackage laraPackage : laraDocFiles.getPackages()) {
            for (LaraDocModule module : laraPackage.getModules()) {
                // Add info about a module to the same AspectIrDoc
                AspectIrDoc laraDoc = new AspectIrDoc();

                // Parse files
                for (File laraFile : module.getLaraFiles()) {
                    Optional<Aspects> aspectIr = LaraToJs.parseLara(laraFile);
                    if (!aspectIr.isPresent()) {
                        continue;
                    }

                    laraDoc.parse(aspectIr.get());
                }
            }
        }
    }

}