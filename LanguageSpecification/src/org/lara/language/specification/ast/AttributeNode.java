/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.ast;

import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import java.util.List;

public class AttributeNode extends LangSpecNode {

    /**
     * Create a new instance of an attribute with a child that defines its
     * declaration
     *
     */
    public AttributeNode(DeclarationNode declaration) {
        addChild(declaration);
    }

    public DeclarationNode getDeclaration() {
        return getChild(DeclarationNode.class, 0);
    }

    public List<DeclarationNode> getParameters() {
        return getChildren(DeclarationNode.class).subList(1, getNumChildren());
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
        builder.addLines("{");
        builder.increaseIndentation();

        builder.addLines("\"type\": \"attribute\",");

        builder.addLines(childrenToJson(builder.getCurrentIdentation() + 1));
        builder.decreaseIndentation();
        builder.add("}");
        return builder.toString();
    }
}
