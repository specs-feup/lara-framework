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

import java.io.InputStream;

import pt.up.fe.specs.lara.ast.EcmaNode;
import pt.up.fe.specs.lara.parser.javacc.ast.ASTStart;
import pt.up.fe.specs.lara.parser.javacc.ast.LARAEcmaScript;
import pt.up.fe.specs.lara.parser.javacc.ast.ParseException;

public class JavaCCLaraParser implements LaraParser {

    public EcmaNode parse(InputStream code, String codeSource) {

        // Create parser
        LARAEcmaScript parser = new LARAEcmaScript(code);

        try {
            // Parse code
            ASTStart root = parser.parse();

            // Convert JJTree to EcmaAst
            System.out.println("AST:");
            root.dump("");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
