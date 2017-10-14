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

package pt.up.fe.specs.lara.doc.jsdocgen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class DocumentationGenerator implements JsDocGenerator {

    // private final Process shell;

    // private final List<List<String>> commandQueue;
    // private final List<String> commandQueue;
    // private final File documentationFolder;
    private int counter;

    public DocumentationGenerator() {
        // this.shell = SpecsPlatforms.getShell();
        // this.shell = null;
        counter = 0;
    }

    // public DocumentationGenerator(File temporaryBaseFolder) {
    // this.commandQueue = new ArrayList<>();
    // this.documentationFolder = SpecsIo.mkdir(temporaryBaseFolder, "jsdocumentation");
    // SpecsIo.deleteFolderContents(documentationFolder);
    // counter = 0;
    // }

    @Override
    // public File queue(List<File> inputFiles, File outputFolder) {
    public Optional<File> generate(List<File> inputFiles, File outputFolder) {
        // Copy files to a temporary folder
        File documentationFolder = new File(SpecsIo.getTempFolder(), "jsdocumentation");
        File commandFolder = SpecsIo.mkdir(documentationFolder, Integer.toString(counter));
        counter++;
        // SpecsIo.deleteFolderContents(documentationFolder);
        SpecsIo.deleteFolderContents(commandFolder);

        // inputFiles.stream().forEach(file -> SpecsIo.copy(file, new File(documentationFolder, file.getName())));
        // System.out.println("FILES:" + inputFiles);
        inputFiles.stream().forEach(file -> SpecsIo.copy(file, new File(commandFolder, file.getName())));

        List<String> command = new ArrayList<>();
        command.add("documentation.cmd");
        command.add("build");
        // command.add(documentationFolder.getAbsolutePath() + File.separator + "**");
        command.add(commandFolder.getAbsolutePath() + File.separator + "**");
        command.add("-f");
        command.add("html");
        command.add("-o");
        command.add(outputFolder.getAbsolutePath());
        /*
        // get stdin of shell
        BufferedWriter shellIn = new BufferedWriter(new OutputStreamWriter(shell.getOutputStream()));
        
        try {
            shellIn.write(command.stream().collect(Collectors.joining(" ")));
            shellIn.newLine();
            shellIn.flush();
        } catch (IOException e) {
            SpecsLogs.msgInfo("Error message: " + e.getMessage());
            return Optional.empty();
        }
        */
        // if (!commandQueue.isEmpty()) {
        // commandQueue.add("&&");
        // }
        //
        // commandQueue.addAll(command);

        // System.out.println("COMMAND:" + command.stream().collect(Collectors.joining(" ")));
        SpecsSystem.runProcess(command, false, true);

        // Delete folder
        // SpecsIo.deleteFolder(documentationFolder);

        File entryPoint = new File(outputFolder, "index.html");
        // return new File(outputFolder, "index.html");

        if (!entryPoint.isFile()) {
            SpecsLogs.msgInfo("[Documentation] Could not generate documentation for files " + inputFiles);
            return Optional.empty();
        }

        return Optional.of(entryPoint);
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
