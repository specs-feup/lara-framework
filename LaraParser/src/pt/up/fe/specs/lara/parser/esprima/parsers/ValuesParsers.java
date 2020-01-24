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

import com.google.gson.JsonObject;

import pt.up.fe.specs.lara.ast.utils.Position;
import pt.up.fe.specs.lara.ast.utils.SourceLocation;
import pt.up.fe.specs.util.SpecsCheck;

public class ValuesParsers {

    public static SourceLocation sourceLocation(JsonObject node) {
        var start = position(node.get("start").getAsJsonObject());
        var end = position(node.get("end").getAsJsonObject());

        var sourceLocation = SourceLocation.newInstance(start, end);

        if (node.has("source")) {
            String source = node.get("source").getAsString();
            SpecsCheck.checkNotNull(source, () -> "Was not supposed to be null");
            sourceLocation.set(SourceLocation.SOURCE, source);
        }

        return sourceLocation;
    }

    public static Position position(JsonObject node) {
        return Position.newInstance(node.get("line").getAsInt(), node.get("column").getAsInt());
    }

}
