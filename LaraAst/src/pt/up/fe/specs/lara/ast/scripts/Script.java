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

package pt.up.fe.specs.lara.ast.scripts;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.lara.ast.LaraNode;
import pt.up.fe.specs.lara.ast.StatementListItem;

public class Script extends Program {

    public Script(DataStore data, Collection<? extends LaraNode> children) {
        super(data, children);
    }

    public List<StatementListItem> getBody() {
        return getChildren(StatementListItem.class);
    }

    @Override
    public String getCode() {
        // return getBody().stream()
        return getChildren().stream()
                .map(node -> node.getCode())
                .collect(Collectors.joining("\n"));
    }
}