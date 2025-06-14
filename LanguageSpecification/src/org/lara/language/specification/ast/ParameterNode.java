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

import org.apache.commons.lang3.StringEscapeUtils;

import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

public class ParameterNode extends LangSpecNode {

    private String defaultValue;
    private final String name;
    private final String type;

    /**
     * Create a new instance of an action with a declaration child that defines its
     * name and return type
     * 
     * @param declaration
     */
    public ParameterNode(String type, String name, String defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public DeclarationNode getDeclaration() {
        return getChild(DeclarationNode.class, 0);
    }

    public List<DeclarationNode> getParameters() {
        return getChildren(DeclarationNode.class).subList(1, getNumChildren());
    }

    @Override
    public String toContentString() {
        return "defaultValue: " + defaultValue;
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
        builder.addLines("{");
        builder.increaseIndentation();

        builder.addLines("\"type\": \"" + type + "\",");
        builder.addLines("\"name\": \"" + name + "\",");
        builder.addLines("\"defaultValue\": \"" + StringEscapeUtils.escapeJava(defaultValue) + "\"");
        builder.decreaseIndentation();
        builder.add("}");
        return builder.toString();
    }
}
