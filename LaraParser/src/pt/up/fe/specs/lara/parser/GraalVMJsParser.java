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

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.js.lang.JavaScriptLanguage;
import com.oracle.truffle.js.parser.GraalJSParserHelper;
import com.oracle.truffle.js.runtime.JSContext;
import com.oracle.truffle.js.runtime.JSEngine;
import com.oracle.truffle.js.runtime.JSParserOptions;

public class GraalVMJsParser {

    private final JSContext context;
    private final JSParserOptions parserOptions;

    public GraalVMJsParser(JSContext context, JSParserOptions parserOptions) {
        this.context = context;
        this.parserOptions = parserOptions;
    }

    public GraalVMJsParser() {
        // this.context = null;
        this.context = JSEngine.createJSContext(JavaScriptLanguage.getCurrentLanguage(),
                JavaScriptLanguage.getCurrentEnv());
        this.parserOptions = new JSParserOptions();

        // var js = new JavaScriptLanguage().parse(new Parsing)
    }

    public void parse(String code, String codeSource) {
        // TODO Auto-generated method stub
        var source = Source.newBuilder(JavaScriptLanguage.ID, code, codeSource).build();
        var rootNode = GraalJSParserHelper.parseScript(context, source, parserOptions);

        System.out.println("NODE:\n" + rootNode);
    }

}
