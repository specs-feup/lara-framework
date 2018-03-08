/**
 * Copyright 2018 SPeCS.
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

package pt.up.fe.specs.lara.unit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.lara.interpreter.weaver.interf.WeaverEngine;

import com.google.common.base.Preconditions;

import larai.LaraI;
import pt.up.fe.specs.lara.doc.aspectir.AspectIrElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.AspectElement;
import pt.up.fe.specs.lara.doc.aspectir.elements.FunctionDeclElement;
import pt.up.fe.specs.lara.doc.data.LaraDocModule;
import pt.up.fe.specs.lara.doc.data.LaraDocTop;
import pt.up.fe.specs.lara.doc.parser.LaraDocParser;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.utilities.Replacer;

public class LaraUnitHarnessBuilder implements AutoCloseable {

    private final WeaverEngine weaverEngine;
    private final File baseFolder;
    private final LaraArgs globalArguments;

    private Collection<File> filesToDelete;
    private File temporaryFolder;

    public LaraUnitHarnessBuilder(WeaverEngine weaverEngine, File baseFolder, LaraArgs globalArguments) {
        this.weaverEngine = weaverEngine;
        this.baseFolder = baseFolder;
        this.globalArguments = globalArguments;

        this.filesToDelete = new ArrayList<>();
        temporaryFolder = SpecsIo.mkdir(SpecsIo.getTempFolder(), "LaraUnitTestFolder");
        // Clean contents of folder
        SpecsIo.deleteFolderContents(temporaryFolder);
    }

    // public Pair<File, String[]> buildTestAndArguments(File testFile) {
    // return null;
    // }

    // public File buildTest(File testFile) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    /*
    public String[] buildArguments(File testFile) {
        // Create test harness
        File testHarnessFile = new File(SpecsIo.getTempFolder(), UUID.randomUUID().toString());
        filesToDelete.add(testHarnessFile);
    
        // First argument is the harness test
    
        // Add global arguments
    
        // Add test arguments
    
        // Add base folder as LARA include
    
        // Return args
    
        // LaraArgs testArguments = new LaraArgs();
        //
        // // Add lara file to test as first argument
        //
        // // Check if there is a custom args file
        // String customArgsFilename = SpecsIo.removeExtension(testFile) + LaraArgs.getArgsExtension();
        // File customArgsFile = new File(testFile.getParentFile(), customArgsFilename);
        //
        // if (customArgsFile.isFile()) {
        // testArguments = testArguments.copy();
        // globalArguments.addArgs(customArgsFile);
        // }
    
        // TODO Auto-generated method stub
        return null;
    }
    */

    public Iterable<LaraUnitHarness> buildTests(File testFile) {
        // Get all methods in test file to iterate over

        return () -> new LaraUnitHarnessIterator();
    }

    private class LaraUnitHarnessIterator implements Iterator<LaraUnitHarness> {

        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public LaraUnitHarness next() {

            return new LaraUnitHarness(null);
        }

    }

    public boolean testFile(File testFile) {

        // Build arguments for the test file
        List<String> testFileArgs = buildFileArgs(testFile);

        LaraDocModule module = getModule(testFile);

        String importPath = module.getImportPath();
        List<AspectIrElement> elements = module.getDocumentation().getTopLevelElements().stream()
                .filter(element -> element instanceof AspectElement || element instanceof FunctionDeclElement)
                .collect(Collectors.toList());

        boolean allPassed = true;
        for (AspectIrElement element : elements) {
            boolean passed = testElement(element, testFileArgs, importPath);
            if (!passed) {
                allPassed = false;
            }
        }

        // Import path
        // System.out.println("IMPORT PATH:" + module.getImportPath());
        // System.out.println("DOC:" + module.getDocumentation());

        // - Detect if function or aspect
        // - If has no arguments
        // - import
        // - name of the function

        return allPassed;
    }

    private boolean testElement(AspectIrElement element, List<String> testFileArgs, String importPath) {
        File testAspect = buildTestAspect(element, importPath);

        // Prepend args with generated aspect file
        String[] args = new String[testFileArgs.size() + 1];
        args[0] = testAspect.getAbsolutePath();
        for (int i = 0; i < testFileArgs.size(); i++) {
            args[i + 1] = testFileArgs.get(i);
        }

        // Call LaraI
        boolean success = false;
        try {
            success = LaraI.exec(args, weaverEngine);
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            String testName = importPath + "::" + element.getName();
            // Get primary cause
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }

            SpecsLogs.msgInfo("Test '" + testName + "' failed: " + cause.getMessage());
            success = false;
        }

        // After testing, delete file
        SpecsIo.delete(testAspect);

        return success;
    }

    private File buildTestAspect(AspectIrElement element, String importPath) {
        boolean isAspect = element instanceof AspectElement;

        // If not aspect, confirm that it is a function
        if (!isAspect) {
            Preconditions.checkArgument(element instanceof FunctionDeclElement);
        }

        String testCall = element.getName() + "()";
        if (isAspect) {
            testCall = "call " + testCall;
        }

        Replacer replacer = new Replacer(LaraUnitResource.TEST_ASPECT);
        replacer.replace("<IMPORT_PATH>", importPath);
        replacer.replace("<TEST_CALL>", testCall);

        String suffix = isAspect ? "_aspect" : "_function";

        File testFile = new File(temporaryFolder, "test" + suffix + ".lara");
        SpecsIo.write(testFile, replacer.toString());
        return testFile;
    }

    public LaraDocModule getModule(File testFile) {
        // Use LaraDoc to get aspects/functions to test from the file
        LaraDocParser laraDocParser = new LaraDocParser(null, weaverEngine.getLanguageSpecification());

        laraDocParser.addPath("Testing", testFile);
        LaraDocTop laraDocTree = laraDocParser.buildLaraDoc();

        // Get module
        List<LaraDocModule> modules = laraDocTree.getDescendants(LaraDocModule.class);
        SpecsCheck.checkArgument(modules.size() == 1, () -> "Expected a single module:" + laraDocTree);
        return modules.get(0);

    }

    private List<String> buildFileArgs(File testFile) {
        // Copy base args file
        LaraArgs testArgs = globalArguments.copy();

        // Add test file arguments
        testArgs.addLocalArgs(testFile);

        // Add base folder as include
        testArgs.addInclude(baseFolder);
        // Add test folder as include
        testArgs.addInclude(SpecsIo.getParent(testFile));

        return testArgs.getCurrentArgs();
    }

    @Override
    public void close() {
        SpecsIo.deleteFolder(temporaryFolder);
    }

    // @Override
    // public void close() throws IOException {
    // // Delete temporary files
    // for (File temporaryFile : filesToDelete) {
    // SpecsIo.delete(temporaryFile);
    // }
    // }

}
