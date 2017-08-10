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

public class SelectNode extends LangSpecNode {
    private final String clazz;
    private final String alias;

    public SelectNode(String clazz, String alias) {
        this.clazz = clazz;
        if (alias == null) {
            alias = "";
        }
        this.alias = alias;
    }

    public String getClazz() {
        return clazz;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toContentString() {
        return "class: " + clazz + (alias.isEmpty() ? "" : (", alias: " + alias));
    }

    @Override
    public String toJson(BuilderWithIndentation builder) {
        builder.addLines("{");
        builder.increaseIndentation();

        builder.addLines("\"type\": \"select\",");
        builder.addLines("\"clazz\": \"" + clazz + "\",");
        if (getToolTip().isPresent()) {
            builder.addLines("\"alias\": \"" + alias + "\",");
            builder.addLines("\"tooltip\": \"" + getToolTip().get() + "\""); // TODO - stringify
        } else {
            builder.addLines("\"alias\": \"" + alias + "\"");
        }
        builder.decreaseIndentation();
        builder.add("}");
        return builder.toString();
    }
}
