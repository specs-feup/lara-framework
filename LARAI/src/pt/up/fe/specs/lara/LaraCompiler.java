/**
 * Copyright 2021 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.lara;

import larac.LaraC;
import larac.options.LaraCOptions;
import larac.utils.output.Output;
import larai.LaraI;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.w3c.dom.Document;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;

import java.io.File;
import java.util.ArrayList;

/**
 * Compiles LARA files to JS.
 *
 * @author JBispo
 *
 */
public class LaraCompiler {

    private final LanguageSpecification langSpec;
    private final WeaverEngine weaver;
    private final JsEngine jsEngine;
    private AspectClassProcessor aspectProcessor;
    private String lastCompilation = null;
    private boolean addMain;

    public LaraCompiler(LanguageSpecification langSpec) {
        this.langSpec = langSpec;
        this.weaver = new DefaultWeaver();
        this.jsEngine = JsEngineType.GRAALVM.newEngine();

        if (!weaver.hasScriptEngine()) {
            weaver.setScriptEngine(jsEngine);
        }

        // Delay initialization, so that we can build the object and make it run on another thread
        aspectProcessor = null;
        lastCompilation = null;
        this.addMain = false;
    }

    public LaraCompiler setAddMain(boolean addMain) {
        this.addMain = addMain;
        return this;
    }

    public String getLastCompilation() {
        return lastCompilation;
    }

    public String compile(File laraFile) {
        SpecsCheck.checkArgument(laraFile.isFile(), () -> "LARA file '" + laraFile + "' does not exist");

        return compile(laraFile.getName(), SpecsIo.read(laraFile));
    }

    public String compile(String laraFilename, String laraCode) {

        var args = new ArrayList<>();
        args.add(LaraCOptions.getSkipArgs());

        var lara = new LaraC(args.toArray(new String[0]), langSpec, new Output(1));

        // Enable parsing directly to JS (e.g. transforms imports into scriptImports)
        lara.setToJsMode(true, laraFilename, laraCode);

        Document aspectIr = lara.compile();

        try {
            return toSimpleJs(aspectIr);
        } catch (Exception e) {
            throw new RuntimeException("Could not generate JavaScript from the AspectIR", e);
        }

    }

    private String toSimpleJs(Document aspectIr) {
        if (aspectProcessor == null) {
            aspectProcessor = buildAspectProcessor();
        }

        try {
            lastCompilation = aspectProcessor.toSimpleJs(aspectIr, addMain);
        } catch (Exception e) {
            throw new RuntimeException("Could not compile LARA file to JS", e);
        }

        return lastCompilation;
    }

    private AspectClassProcessor buildAspectProcessor() {
        return LaraI.buildAspectProcessor(weaver, jsEngine);
    }

}
