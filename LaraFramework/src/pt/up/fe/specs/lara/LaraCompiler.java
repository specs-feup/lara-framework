/**
 * Copyright 2021 SPeCS.
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

package pt.up.fe.specs.lara;

import java.io.File;
import java.util.ArrayList;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.w3c.dom.Document;

import larac.LaraC;
import larac.utils.output.MessageConstants;
import larac.utils.output.Output;
import larai.LaraI;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.StringUtils;

/**
 * Compiles LARA files to JS.
 * 
 * @author JBispo
 *
 */
public class LaraCompiler {

    private final WeaverEngine weaver;
    private final JsEngine jsEngine;
    private final AspectClassProcessor aspectProcessor;

    public LaraCompiler(WeaverEngine weaver) {
        this.weaver = weaver;
        this.jsEngine = JsEngineType.GRAALVM.newEngine();

        if (!weaver.hasScriptEngine()) {
            weaver.setScriptEngine(jsEngine);
        }

        aspectProcessor = buildAspectProcessor();
    }

    public String compile(String filename, String code) {
        var baseFolder = SpecsIo.getTempFolder("lara-compiler");
        var laraFile = new File(baseFolder, filename);
        SpecsIo.write(laraFile, code);

        // return compile(SpecsIo.read(laraFile), laraFile.getName());
        return compile(laraFile);
    }

    public String compile(File laraFile) {
        SpecsCheck.checkArgument(laraFile.isFile(), () -> "LARA file '" + laraFile + "' does not exist");

        // System.out.println("FILE:\n" + SpecsIo.read(laraFile));

        // System.out.println("CONTENT:\n" + code);
        // System.out.println("FILENAME: " + filename);

        var args = new ArrayList<>();
        args.add(laraFile.getAbsolutePath());
        // args.add("-o");
        // args.add(path);

        var lara = new LaraC(args.toArray(new String[0]), weaver.getLanguageSpecificationV2(), new Output(1));

        // Enable parsing directly to JS (e.g. transforms imports into scriptImports)
        lara.setToJsMode(true);
        // lara.compileAndSave();
        // Document compile = lara.getAspectIRXmlRepresentation();
        // return compile;
        Document aspectIr = lara.compile();
        String xml;
        try {
            xml = StringUtils.xmlToStringBuffer(aspectIr, MessageConstants.INDENT).toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new RuntimeException("Could not convert to XML", e);
        }
        // System.out.println("XML Done!");
        // System.out.println("XML:\n" + xml);

        try {
            return aspectProcessor.toSimpleJs(aspectIr);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate JavaScript from the AspectIR", e);
        }

    }

    private AspectClassProcessor buildAspectProcessor() {
        // Create LARA Interpreter
        DataStore laraiConfig = DataStore.newInstance("LaraCompiler");
        laraiConfig.set(LaraiKeys.LARA_FILE, new File(""));
        LaraI larai = LaraI.newInstance(laraiConfig, weaver);

        // Create MasterWeaver
        FileList folderApplication = FileList.newInstance();
        MasterWeaver masterWeaver = new MasterWeaver(larai, weaver, folderApplication, jsEngine);
        larai.setWeaver(masterWeaver);

        // Create interpreter
        Interpreter interpreter = new Interpreter(larai, jsEngine, false);
        larai.setInterpreter(interpreter);
        // larai.getInterpreter().getImportProcessor().importAndInitialize();

        masterWeaver.simpleBegin();

        var aspectProcessor = AspectClassProcessor.newInstance(interpreter);
        return aspectProcessor;
    }
}
