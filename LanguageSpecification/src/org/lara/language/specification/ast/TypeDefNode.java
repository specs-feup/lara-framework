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

import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

public class TypeDefNode extends LangSpecNode {

    private final String name;
    private String nodeType;

    public TypeDefNode(String name) {
        this(name, "typedef");

    }

    public TypeDefNode(String name, String nodeType) {
        this.name = name;
        this.nodeType = nodeType;

    }

    @Override
    public String toContentString() {
        return "name: " + name;
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
        builder.addLines("{");
        builder.increaseIndentation();
        builder.addLines("\"type\": \"" + getNodeType() + "\"");
        builder.addLines("\"name\": \"" + name + "\"");
        if (this.hasChildren()) {
            builder.addLine(",");
            builder.addLines(childrenToJson(builder.getCurrentIdentation() + 1));
        }
        builder.decreaseIndentation();
        builder.add("}");
        return builder.toString();
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
