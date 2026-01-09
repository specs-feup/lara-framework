/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.ast;

import java.util.List;

import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

public class ActionNode extends LangSpecNode {

    /**
     * Create a new instance of an action with a declaration child that defines its
     * name and return type
     *
     */
    public ActionNode(DeclarationNode declaration) {
        addChild(declaration);
    }

    public DeclarationNode getDeclaration() {
        return getChild(DeclarationNode.class, 0);
    }

    public List<ParameterNode> getParameters() {
        // Get all children except the first one (which is DeclarationNode), and filter for ParameterNode
        return getChildren().subList(1, getNumChildren()).stream()
                .map(ParameterNode.class::cast)
                .toList();
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
        builder.addLines("{");
        builder.increaseIndentation();

        builder.addLines("\"type\": \"action\",");

        builder.addLines(childrenToJson(builder.getCurrentIdentation() + 1));
        builder.decreaseIndentation();
        builder.add("}");
        return builder.toString();
    }

}
