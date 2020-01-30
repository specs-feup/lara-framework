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

import pt.up.fe.specs.lara.ast.exprs.Literal;
import pt.up.fe.specs.lara.ast.exprs.StringLiteral;
import pt.up.fe.specs.lara.parser.esprima.EsprimaConverterData;

public class ExpressionParsers {

    public static DataStore parseLiteralData(JsonObject node, EsprimaConverterData data) {
        DataStore nodeData = GeneralParsers.parseNodeData(node, data);

        nodeData.set(Literal.RAW, node.get("raw").getAsString());

        return nodeData;
    }

    public static DataStore parseStringLiteralData(JsonObject node, EsprimaConverterData data) {
        DataStore nodeData = parseLiteralData(node, data);

        nodeData.set(StringLiteral.VALUE, node.get("value").getAsString());

        return nodeData;
    }

}
