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

package pt.up.fe.specs.lara.parser.esprima.parsers;

import org.suikasoft.jOptions.Interfaces.DataStore;

import com.google.gson.JsonObject;

import pt.up.fe.specs.lara.ast.LaraNode;
import pt.up.fe.specs.lara.parser.esprima.ConverterUtils;
import pt.up.fe.specs.lara.parser.esprima.EsprimaConverterData;

public class GeneralParsers {

    public static DataStore parseNodeData(JsonObject node, EsprimaConverterData data) {

        // Get LaraNode class
        var nodeClass = ConverterUtils.getLaraNodeClass(node);

        // Initialize DataStore
        DataStore nodeData = ConverterUtils.newLaraDataStore(nodeClass);

        // Populate node data
        nodeData.add(LaraNode.CONTEXT, data.get(EsprimaConverterData.LARA_CONTEXT));

        return nodeData;
    }

    public static DataStore parseProgramData(JsonObject node, EsprimaConverterData data) {
        DataStore nodeData = parseNodeData(node, data);

        // Extract program specific information

        return nodeData;
    }

}
