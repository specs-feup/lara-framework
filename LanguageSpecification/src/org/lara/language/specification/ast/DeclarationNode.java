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

import org.lara.language.specification.dsl.Declaration;

import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

public class DeclarationNode extends LangSpecNode {

    private final String name;
    private final String type;

    public DeclarationNode(Declaration declaration) {
	this(declaration.getName(), declaration.getType().toString());
    }

    public DeclarationNode(String name, String type) {
	super();
	this.name = name;
	this.type = type;
    }

    @Override
    public String toContentString() {
	return "name: " + name + ", type: " + type;
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
	builder.addLines("{");
	builder.increaseIndentation();

	builder.addLines("\"type\": \"" + type + "\",");
	builder.addLines("\"name\": \"" + name + "\"");
	builder.decreaseIndentation();
	builder.add("}");
	return builder.toString();
    }
}
