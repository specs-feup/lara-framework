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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.lazy.Lazy;

/**
 * Convert Lara to JS.
 * 
 * @author JoaoBispo
 *
 */
public class LaraToJs {

    private static final Set<String> FILES_TO_COPY = new HashSet<>(Arrays.asList("lara.resource", "lara.bundle"));

    private static final Pattern REGEX_WITH = Pattern.compile("with\\s*\\(.*\\)");
    private static final Pattern REGEX_FOR_EACH = Pattern.compile("for each \\(");
    private static final Pattern REGEX_QUOTES = Pattern.compile("(.*)\\['([a-zA-Z0-9_]+)'\\]");

    private File outputFolder;
    private final Lazy<AspectClassProcessor> aspectProcessor;
    private final LanguageSpecification languageSpecification;

    private final boolean ignoreUnderscoredFolders = true;

    // public LaraDoc(WeaverEngine weaverEngine, File inputPath, File outputFolder) {
    public LaraToJs(File outputFolder) {
        this.outputFolder = SpecsIo.mkdir(outputFolder);
        this.aspectProcessor = Lazy.newInstance(LaraToJs::newAspectProcessor);
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

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = SpecsIo.mkdir(outputFolder);
    }

    public void convertLara(File laraFile) {
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
        Document aspectIr = null;

        try {
            aspectIr = larac.compile();
        } catch (Exception e) {
            SpecsLogs.msgInfo("Could not compile file '" + laraFile + "'");
            return;
        }

        // String aspectXml = toXml(aspectIr);

        // LaraI.main(args);
        Aspects asps = null;
        try {
            asps = new Aspects(aspectIr, "");
            // System.out.println("--- IR BEFORE ---");
            // lara.printAspectIR();
            // System.out.println("--- IR AFTER ---");

        } catch (Exception e) {
            SpecsLogs.msgInfo("Could not create aspects: " + e.getMessage());
            return;
            // throw new RuntimeException("Could not create aspects", e);
        }

        // Pass through LaraI
        AspectClassProcessor aspectClassProcessor = aspectProcessor.get();
        StringBuilder jsCode = aspectClassProcessor.generateJavaScriptDoc(asps);

        String cleanedJsCode = cleanJsCode(jsCode.toString());

        // Save js to the same relative location as the original file
        File jsFile = new File(outputFolder, SpecsIo.removeExtension(laraFile) + ".js");

        SpecsIo.write(jsFile, cleanedJsCode.toString());
    }

    /**
     * Applies several cleaning passes to the generated JS code (removes for each, with, quotes...)
     * 
     * @param string
     * @return
     */
    private String cleanJsCode(String jsCode) {

        String currentCode = jsCode;

        // Replace 'with' with empty string
        currentCode = REGEX_WITH.matcher(currentCode).replaceAll("");

        // Replace 'for each' with 'for'
        currentCode = REGEX_FOR_EACH.matcher(currentCode).replaceAll("for (");

        // Remove quotes from JS properties accesses
        currentCode = replaceQuotes(currentCode);

        return currentCode;
    }

    private String replaceQuotes(String jsCode) {
        String currentCode = jsCode;

        try {

            while (true) {

                Matcher regexMatcher = REGEX_QUOTES.matcher(currentCode);

                boolean replace = regexMatcher.find();

                // If no replacement occurred, exit loop
                if (!replace) {
                    break;
                }

                String replacement = regexMatcher.group(1) + "." + regexMatcher.group(2);
                currentCode = regexMatcher.replaceFirst(replacement);
            }
        } catch (Exception e) {
            SpecsLogs.msgInfo("Problems while replacing quotes: " + e.getMessage());
        }
        return currentCode;

    }
}
