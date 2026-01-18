/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.language.specification.ast.LangSpecNode;
import org.lara.language.specification.ast.NodeFactory;
import org.lara.language.specification.dsl.LanguageSpecification;
import pt.up.fe.specs.util.SpecsIo;

import java.io.File;

public class LaraJoinPointJsonGeneratorLauncher {

    private static final String OUTPUT_PATH = "../Lara-JS/LaraJoinPointSpecification.json";

    public static void main(String[] args) {
        String outputPath = OUTPUT_PATH;
        if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
            outputPath = args[0];
        }
        generateJsonOutputFile(outputPath);
    }

    private static void generateJsonOutputFile(String outputPath) {
        var jp = JoinPoint.getLaraJoinPoint();
        var langSpec = new LanguageSpecification(jp, null);
        langSpec.setGlobal(jp);

        LangSpecNode node = NodeFactory.toNode(langSpec);
        String json = node.toJson();

        File jsonOutFile = new File(outputPath);
        SpecsIo.write(jsonOutFile, json);
        System.out.println(json);
    }
}
