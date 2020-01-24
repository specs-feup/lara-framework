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

import org.suikasoft.GsonPlus.SpecsGson;
import org.suikasoft.jOptions.Interfaces.DataStore;

import com.google.gson.JsonObject;

import pt.up.fe.specs.lara.ast.stmts.ExpressionStatement;
import pt.up.fe.specs.lara.parser.esprima.EsprimaConverterData;

public class StatementParsers {

    public static DataStore parseExpressionStatementData(JsonObject node, EsprimaConverterData data) {
        DataStore nodeData = GeneralParsers.parseNodeData(node, data);

        nodeData.add(ExpressionStatement.DIRECTIVE,
                SpecsGson.asOptional(node.get("directive"), elem -> elem.getAsString()));

        data.get(EsprimaConverterData.FOUND_CHILDREN).add(node.get("expression").getAsJsonObject());
        System.out.println("EXPR STMT: " + nodeData);
        return nodeData;
    }

}
