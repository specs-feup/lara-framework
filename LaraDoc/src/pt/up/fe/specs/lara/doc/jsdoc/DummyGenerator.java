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

package pt.up.fe.specs.lara.doc.jsdoc;

import java.io.File;
import java.util.List;
import java.util.Optional;

import pt.up.fe.specs.util.SpecsIo;

public class DummyGenerator implements JsDocGenerator {

    private int counter;

    public DummyGenerator() {
        counter = 0;
    }

    @Override
    public Optional<File> generate(List<File> inputFiles, File outputFolder) {
        // Copy files to the corresponding folder
        File commandFolder = SpecsIo.mkdir(outputFolder.getParentFile(), "js");
        counter++;
        inputFiles.stream().forEach(file -> SpecsIo.copy(file, new File(commandFolder, file.getName())));

        return Optional.empty();
    }

    // @Override
    // public void generate() {
    //
    // SpecsSystem.runProcess(commandQueue, false, true);
    //
    // // Create single command
    // // commandQueue.stream().map(DocumentationGenerator::commandToString).collect(Collectors.joining(" && "));
    //
    // // Delete temporary folder
    // SpecsIo.deleteFolderContents(documentationFolder);
    // }

    // private static String commandToString(List<String> command) {
    // return command.stream().map(cmd -> cmd.contains(" ") ? "\"" + cmd + "\"" : cmd)
    // .collect(Collectors.joining(" "));
    // }
}
