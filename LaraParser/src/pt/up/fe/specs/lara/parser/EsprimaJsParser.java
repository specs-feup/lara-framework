/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsEngineType;
import pt.up.fe.specs.util.lazy.Lazy;

public class EsprimaJsParser {

    private static final Lazy<JsEngine> ESPRIMA_PARSER = Lazy.newInstance(EsprimaJsParser::initEsprima);

    private static final String ARG_ESPRIMA_CODE = "esprimaCode";
    private static final String RETURN_ESPRIMA_AST = "esprimaAstAsString";

    private final JsEngine jsEngine;
    private final JsonParser parser;

    public EsprimaJsParser() {
        this.jsEngine = ESPRIMA_PARSER.get();
        this.parser = new JsonParser();
    }

    private static JsEngine initEsprima() {
        var javascriptEngine = JsEngineType.GRAALVM.newEngine();

        javascriptEngine.eval(LaraParserResource.ESPRIMA.read());
        javascriptEngine.eval(LaraParserResource.ESPRIMA_LARA.read());
        javascriptEngine.eval(LaraParserResource.ESCODEGEN.read());

        return javascriptEngine;
    }

    public JsonObject parseScript(String code, String scriptSource) {

        // Add arguments for parsing
        jsEngine.put(ARG_ESPRIMA_CODE, code);

        // Parse JS code
        jsEngine.eval(LaraParserResource.PARSE_JS.read());

        // Obtain AST in String format
        String stringAst = jsEngine.get(RETURN_ESPRIMA_AST, String.class);

        // Parse JSON to Java objects
        JsonElement jsonTree = parser.parse(stringAst);
        JsonObject program = jsonTree.getAsJsonObject();

        // Add script source as property
        program.addProperty("path", scriptSource);

        // System.out.println("RESULT: " + jsEngine.eval("FnExprTokens;"));

        return program;
    }

}
