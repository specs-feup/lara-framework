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

package pt.up.fe.specs.lara.ast;

import java.util.Collection;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.utilities.StringLines;

public class UnimplementedNode extends LaraNode {

    public static final DataKey<String> TYPE = KeyFactory.string("type");

    public UnimplementedNode(DataStore data, Collection<? extends LaraNode> children) {
        super(data, children);
    }

    @Override
    public String getCode() {
        var code = new StringBuilder();

        code.append("// Unimplemented node: ").append(get(TYPE)).append("\n");

        // Add each children
        String tab = "   ";
        for (var child : getChildren()) {
            var childCode = child.getCode();
            for (var line : StringLines.getLines(childCode)) {
                code.append(tab).append(line).append("\n");
            }

        }

        return code.toString();
    }
}
