/**
 * Copyright 2016 SPeCS.
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

package org.lara.interpreter.tester;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.config.interpreter.VerboseLevel;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Interfaces.DataStore;

import larai.LaraI;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.providers.ResourceProvider;

public class WeaverTester<T extends WeaverEngine> {

    private static final boolean DEBUG = false;

    private static final String WORK_FOLDER = "java_weaver_output";
    private static final String WEAVED_FOLDER = "src-weaved";

    private final Class<T> weaverEngineClass;

    private final String basePackage;
    /*
    private final String compilerFlags;
    
    private boolean checkWeavedCodeSyntax;
    */
    private String srcPackage;
    private String resultPackage;
    private boolean keepWeavedFiles;

    private final DataStore customData;

    public WeaverTester(Class<T> weaverEngineClass, String basePackage) {
        this.weaverEngineClass = weaverEngineClass;

        // public JavaWeaverTester(String basePackage, Standard standard, String compilerFlags) {
        this.basePackage = basePackage;
        /*
        this.standard = standard;
        this.compilerFlags = compilerFlags;
        this.checkWeavedCodeSyntax = true;
        */
        srcPackage = null;
        resultPackage = null;
        keepWeavedFiles = false;

        customData = DataStore.newInstance(weaverEngineClass.getSimpleName() + " Test Extra Data");

    }

    public <D, ED extends D> WeaverTester<T> set(DataKey<D> key, ED value) {
        customData.set(key, value);
        return this;
    }

    protected DataStore getCustomData() {
        return customData;
    }

    public WeaverTester<T> setKeepWeavedFiles(boolean keepWeavedFiles) {
        this.keepWeavedFiles = keepWeavedFiles;

        return this;
    }

    /**
     * 
     * @param checkWeavedCodeSyntax
     * @return the previous value
     */
    /*
    public JavaWeaverTester setCheckWeavedCodeSyntax(boolean checkWeavedCodeSyntax) {
        this.checkWeavedCodeSyntax = checkWeavedCodeSyntax;
    
        return this;
    }
    */

    // public JavaWeaverTester(String basePackage, Standard standard) {
    // this(basePackage, standard, "");
    // }

    public WeaverTester<T> setResultPackage(String resultPackage) {
        this.resultPackage = sanitizePackage(resultPackage);

        return this;
    }

    public WeaverTester<T> setSrcPackage(String srcPackage) {
        this.srcPackage = sanitizePackage(srcPackage);

        return this;
    }

    private String sanitizePackage(String packageName) {
        String sanitizedPackage = packageName;
        if (!sanitizedPackage.endsWith("/")) {
            sanitizedPackage += "/";
        }

        return sanitizedPackage;
    }

    private ResourceProvider buildCodeResource(String codeResourceName) {
        StringBuilder builder = new StringBuilder();

        builder.append(basePackage);
        if (srcPackage != null) {
            builder.append(srcPackage);
        }

        builder.append(codeResourceName);

        return () -> builder.toString();
    }

    public void test(String laraResource, String... codeResource) {
        test(laraResource, Arrays.asList(codeResource));
    }

    public void test(String laraResource, List<String> codeResources) {
        SpecsLogs.msgInfo("\n---- Testing '" + laraResource + "' ----\n");
        List<ResourceProvider> codes = SpecsCollections.map(codeResources, this::buildCodeResource);

        File log = runJavaWeaver(() -> basePackage + laraResource, codes);
        String logContents = SpecsIo.read(log);

        StringBuilder expectedResourceBuilder = new StringBuilder();
        expectedResourceBuilder.append(basePackage);
        if (resultPackage != null) {
            expectedResourceBuilder.append(resultPackage);
        }
        expectedResourceBuilder.append(laraResource).append(".txt");

        String expectedResource = expectedResourceBuilder.toString();

        if (!SpecsIo.hasResource(expectedResource)) {
            SpecsLogs.msgInfo("Could not find resource '" + expectedResource
                    + "', skipping verification. Actual output:\n" + logContents);
            return;
        }

        assertEquals(normalize(SpecsIo.getResource(expectedResource)), normalize(logContents));

    }

    /**
     * Normalizes endlines
     *
     * @param resource
     * @return
     */
    private static String normalize(String string) {
        return SpecsStrings.normalizeFileContents(string, true);
    }

    private static String resourceNameMapper(String resourceName) {
        if (resourceName.endsWith(".test")) {
            return resourceName.substring(0, resourceName.length() - ".test".length());
        }

        return resourceName;
    }

    private File runJavaWeaver(ResourceProvider lara, List<ResourceProvider> code) {
        // Prepare folder
        File workFolder = SpecsIo.mkdir(WORK_FOLDER);
        SpecsIo.deleteFolderContents(workFolder);

        File outputFolder = null;
        if (keepWeavedFiles) {
            outputFolder = SpecsIo.mkdir(WEAVED_FOLDER);
        } else {
            outputFolder = SpecsIo.mkdir(workFolder, WEAVED_FOLDER);
        }

        // Prepare files
        code.forEach(resource -> resource.write(workFolder, true, WeaverTester::resourceNameMapper));
        File laraFile = lara.write(workFolder);

        DataStore data = DataStore.newInstance(weaverEngineClass.getSimpleName() + " Test");

        // Set LaraI configurations
        data.add(LaraiKeys.LARA_FILE, laraFile);
        data.add(LaraiKeys.OUTPUT_FOLDER, outputFolder);
        data.add(LaraiKeys.WORKSPACE_FOLDER, FileList.newInstance(workFolder));
        data.add(LaraiKeys.VERBOSE, VerboseLevel.warnings);
        data.add(LaraiKeys.LOG_JS_OUTPUT, Boolean.TRUE);
        data.add(LaraiKeys.LOG_FILE, OptionalFile.newInstance(getWeaverLog().getAbsolutePath()));

        T weaver;
        try {
            weaver = weaverEngineClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate weaver with class '" + weaverEngineClass + "'", e);
        }

        try {
            boolean result = LaraI.exec(data, weaver);
            // Check weaver executed correctly
            assertTrue(result);
        } catch (Exception e) {
            throw new RuntimeException("Problems during weaving", e);
        }

        /*
        if (!keepWeavedFiles) {
            // File weavedFolder = new File(WEAVED_FOLDER);
            SpecsIo.deleteFolderContents(outputFolder);
        
            // Recreate dummy
            SpecsIo.write(new File(outputFolder, "dummy"), "");
        }
        */

        return getWeaverLog();
    }

    public static File getWorkFolder() {
        return new File(WORK_FOLDER);
    }

    public static File getWeaverLog() {
        return new File(WORK_FOLDER, "test.log");
    }

    public static void clean() {
        if (DEBUG) {
            return;
        }

        // Delete CWeaver folder
        File workFolder = WeaverTester.getWorkFolder();
        if (workFolder.isDirectory()) {
            SpecsIo.deleteFolderContents(workFolder, true);
            SpecsIo.delete(workFolder);
        }

        // Delete weaver files
        // ClangAstParser.getTempFiles().stream()
        // .forEach(filename -> new File(filename).delete());
    }

}
