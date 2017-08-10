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

public class RootNode extends LangSpecNode {

    private final String rootName;
    private final String rootAlias;

    public RootNode(String rootName, String rootAlias) {
	this.rootName = rootName;
	if (rootAlias == null) {
	    rootAlias = "";
	}
	this.rootAlias = rootAlias;
    }

    public String getRootName() {
	return rootName;
    }

    public String getRootAlias() {
	return rootAlias;
    }

    @Override
    public String toContentString() {
	return "root: " + rootName + (rootAlias.isEmpty() ? "" : (", alias: " + rootAlias));
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
	builder.addLines("{");
	builder.increaseIndentation();

	builder.addLines("\"root\": \"" + getRootName() + "\",");
	builder.addLines("\"rootAlias\": \"" + getRootAlias() + "\",");

	builder.addLines(childrenToJson(builder.getCurrentIdentation() + 1));
	builder.decreaseIndentation();
	builder.add("}");
	return builder.toString();
    }
}
