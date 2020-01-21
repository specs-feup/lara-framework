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

package pt.up.fe.specs.lara.parser.esprima;

import java.util.Collections;

import com.google.gson.JsonObject;

import pt.up.fe.specs.lara.ast.LaraContext;
import pt.up.fe.specs.lara.ast.LaraNode;
import pt.up.fe.specs.util.SpecsSystem;

public class EsprimaConverter {

    private final EsprimaConverterData parserData;

    public EsprimaConverter(LaraContext laraContext) {
        this.parserData = new EsprimaConverterData();
        parserData.set(EsprimaConverterData.LARA_CONTEXT, laraContext);
    }

    public LaraNode parse(JsonObject node) {
        // Get DataStore

        // Get children

        // Invoke constructor to build node and return it
        var nodeClass = ConverterUtils.getLaraNodeClass(node);
        return SpecsSystem.newInstance(nodeClass, null, Collections.emptyList());

    }

}
