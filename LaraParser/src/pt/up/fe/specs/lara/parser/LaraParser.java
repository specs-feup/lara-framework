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

import java.io.File;
import java.io.InputStream;

import pt.up.fe.specs.lara.ast.EcmaNode;
import pt.up.fe.specs.util.SpecsIo;

public interface LaraParser {

    EcmaNode parse(InputStream code, String codeSource);

    default EcmaNode parse(File sourceFile) {
        return parse(SpecsIo.toInputStream(sourceFile), SpecsIo.getCanonicalPath(sourceFile));
    }

    default EcmaNode parse(String code, String codeSource) {
        return parse(SpecsIo.toInputStream(code), codeSource);
    }

}
