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
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngineManager;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.aspectir.Aspects;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.w3c.dom.Document;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import larac.LaraC;
import larac.utils.output.Output;
import larai.LaraI;
import pt.up.fe.specs.util.Preconditions;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.lazy.Lazy;

public class LaraDoc {

    private final File inputPath;
    private final File outputFolder;
    private final Lazy<AspectClassProcessor> aspectProcessor;
    private final LanguageSpecification languageSpecification;

    // public LaraDoc(WeaverEngine weaverEngine, File inputPath, File outputFolder) {
    public LaraDoc(File inputPath, File outputFolder) {
        Preconditions.checkArgument(inputPath.exists(), "Given input path '" + inputPath + "' does not exist");
        this.inputPath = inputPath;
        this.outputFolder = SpecsIo.mkdir(outputFolder);
        // this.aspectProcessor = Lazy.newInstance(() -> LaraDoc.newAspectProcessor(weaverEngine));
        this.aspectProcessor = Lazy.newInstance(LaraDoc::newAspectProcessor);
        this.languageSpecification = new DefaultWeaver().getLanguageSpecification();
    }

    // private static AspectClassProcessor newAspectProcessor(WeaverEngine weaverEngine) {
    private static AspectClassProcessor newAspectProcessor() {
        DataStore data = DataStore.newInstance("LaraDoc");
        data.add(LaraiKeys.LARA_FILE, new File(""));
        // data.add(LaraiKeys.VERBOSE, VerboseLevel.errors);
        WeaverEngine weaverEngine = new DefaultWeaver();
        LaraI larai = LaraI.newInstance(data, weaverEngine);
        NashornScriptEngine jsEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
        FileList folderApplication = FileList.newInstance();
        MasterWeaver masterWeaver = new MasterWeaver(larai, weaverEngine, folderApplication, jsEngine);
        larai.setWeaver(masterWeaver);

        Interpreter interpreter = new Interpreter(larai, jsEngine);
        larai.setInterpreter(interpreter);
        larai.getInterpreter().getImportProcessor().importAndInitialize();
        masterWeaver.begin();

        return AspectClassProcessor.newInstance(interpreter);
    }

    public void convertFiles() {

        // Collect LARA files
        List<File> laraFiles = SpecsIo.getFilesRecursive(inputPath, "lara");
        for (File laraFile : laraFiles) {
            convertLara(laraFile);
        }

        // Collect JS files

        // Collect bundle files
    }

    private void convertLara(File laraFile) {
        // Pass through LaraC
        System.out.println("COMPILING FILE " + laraFile);
        List<String> args = new ArrayList<>();

        args.add(laraFile.getAbsolutePath());
        args.add("--doc");
        args.add("--verbose");
        args.add("0");
        // args.add("-d");
        // preprocess.add("-o");
        // preprocess.add(path);
        // if (!encodedIncludes.trim().isEmpty()) {
        // preprocess.add("-i");
        // preprocess.add(encodedIncludes);
        // }

        // lara files as resources
        // List<ResourceProvider> laraAPIs = new ArrayList<>(ResourceProvider.getResources(LaraApiResource.class));
        // System.out.println("LARA APIS :" + IoUtils.getResource(laraAPIs2.get(0)));
        // laraAPIs.addAll(options.getLaraAPIs());
        /*
        List<ResourceProvider> laraAPIs = options.getLaraAPIs();
        if (!laraAPIs.isEmpty()) {
            preprocess.add("-r");
            String resources = laraAPIs.stream().map(LaraI::getOriginalResource)
                    .collect(Collectors.joining(File.pathSeparator));
            preprocess.add(resources);
        }
        */
        /*
        if (options.isDebug()) {
            preprocess.add("-d");
        }
        */
        LaraC larac = new LaraC(args.toArray(new String[0]), languageSpecification, new Output());
        Document aspectIr = larac.compile();
        // String aspectXml = toXml(aspectIr);

        // LaraI.main(args);
        Aspects asps = null;
        try {
            asps = new Aspects(aspectIr, "");
            // System.out.println("--- IR BEFORE ---");
            // lara.printAspectIR();
            // System.out.println("--- IR AFTER ---");

        } catch (Exception e) {
            throw new RuntimeException("Could not create aspects", e);
        }

        // Pass through LaraI
        AspectClassProcessor aspectClassProcessor = aspectProcessor.get();
        StringBuilder jsCode = aspectClassProcessor.generateJavaScriptDoc(asps);

        // Save js to the same relative location as the original file
        String relativePath = SpecsIo.getRelativePath(laraFile, inputPath);
        File jsFile = new File(outputFolder, SpecsIo.removeExtension(relativePath) + ".js");

        SpecsIo.write(jsFile, jsCode.toString());
    }

    // private String toXml(Document aspectIr) {
    // try {
    // return StringUtils.xmlToStringBuffer(aspectIr, MessageConstants.INDENT).toString();
    // } catch (TransformerFactoryConfigurationError | TransformerException e) {
    // throw new RuntimeException("Could not generate XML from Aspect IR", e);
    // }
    //
    // }

}
