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
import pt.up.fe.specs.lara.doc.jsdoc.JsDocTagName;
import pt.up.fe.specs.lara.doc.parser.LaraDocParser;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.utilities.Replacer;

public class LaraUnitHarnessBuilder implements AutoCloseable {

    private final WeaverEngine weaverEngine;
    private final File baseFolder;
    private final LaraArgs globalArguments;

    private File temporaryFolder;
    private boolean logMetrics;

    public LaraUnitHarnessBuilder(WeaverEngine weaverEngine, File baseFolder, LaraArgs globalArguments) {
        this.weaverEngine = weaverEngine;
        this.baseFolder = baseFolder;
        this.globalArguments = globalArguments;

        temporaryFolder = SpecsIo.mkdir(SpecsIo.getTempFolder(), "LaraUnitTestFolder");
        logMetrics = false;
        // Clean contents of folder
        SpecsIo.deleteFolderContents(temporaryFolder);
    }

    public List<TestResult> testFile(File testFile) {

        List<TestResult> testResults = new ArrayList<>();

        // Build arguments for the test file
        List<String> testFileArgs = buildFileArgs(testFile);
        SpecsLogs.debug(() -> "Test file arguments: " + testFileArgs);

        LaraDocModule module = getModule(testFile);

        String importPath = module.getImportPath();
        List<AspectIrElement> elements = module.getDocumentation().getTopLevelElements().stream()
                // Only aspects and functions
                .filter(element -> element instanceof AspectElement || element instanceof FunctionDeclElement)
                // Only if marked as test
                .filter(element -> element.getComment().hasTag(JsDocTagName.TEST))
                .collect(Collectors.toList());

        for (AspectIrElement element : elements) {
            TestResult testResult = testElement(element, testFileArgs, importPath);
            testResults.add(testResult);
        }

        return testResults;
    }

    private TestResult testElement(AspectIrElement element, List<String> testFileArgs, String importPath) {
        File testAspect = buildTestAspect(element, importPath);

        String testName = importPath + "." + element.getName();

        // Prepend args with generated aspect file
        String[] args = new String[testFileArgs.size() + 1];
        args[0] = testAspect.getAbsolutePath();
        for (int i = 0; i < testFileArgs.size(); i++) {
            args[i + 1] = testFileArgs.get(i);
        }

        // Call LaraI
        boolean success = false;
        Throwable cause = null;

        long tic = System.nanoTime();
        long toc = -1;
        try {
            success = LaraI.exec(args, weaverEngine);
            toc = System.nanoTime();
        } catch (Exception e) {
            // Measure failed test time
            toc = System.nanoTime();

            // Flag failure
            success = false;

            // Store cause
            cause = e;

            // Get primary cause
            Throwable firstCause = cause;
            while (firstCause.getCause() != null) {
                firstCause = firstCause.getCause();
            }

            SpecsLogs.msgInfo("[FAIL] Test '" + testName + "': " + firstCause.getMessage());

        }

        long nanoTime = toc - tic;

        // After testing, delete file
        SpecsIo.delete(testAspect);

        return success ? TestResult.success(importPath, element.getName(), nanoTime)
                : TestResult.fail(importPath, element.getName(), nanoTime, cause);
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

        // If -of option is not set, add option with name of the test file as argument
        if (!testArgs.hasArg("-of")) {
            testArgs.addArg("-of");
            testArgs.addArg(SpecsIo.removeExtension(testFile));
        }

        // If no verbose level is set, automatically set it to 2 (warnings)
        if (!testArgs.hasArg("-b")) {
            testArgs.addArg("-b");
            testArgs.addArg("2");
        }

        // If log metrics is enabled and there is no -e option, add it
        if (logMetrics && !testArgs.hasArg("-e")) {
            testArgs.addArg("-e");
            testArgs.addArg("metrics/" + SpecsIo.removeExtension(testFile) + ".json");
        }

        return testArgs.getCurrentArgs();
    }

    @Override
    public void close() {
        SpecsIo.deleteFolder(temporaryFolder);
    }

    public void setLogMetrics(boolean logMetrics) {
        this.logMetrics = logMetrics;
    }

}
