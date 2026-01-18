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

import pt.up.fe.specs.util.treenode.ATreeNode;
import pt.up.fe.specs.util.utilities.BuilderWithIndentation;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class LangSpecNode extends ATreeNode<LangSpecNode> {
    private String toolTip = null;

    public LangSpecNode() {
        super(Collections.emptyList());
    }

    @Override
    protected LangSpecNode copyPrivate() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public LangSpecNode getThis() {
        return this;
    }

    @Override
    public String toContentString() {
        return "";
    }

    public String toJson() {
        return toJson(new BuilderWithIndentation());
    }

    public abstract String toJson(BuilderWithIndentation builder);

    protected String childrenToJson(int indentation) {
        StringBuilder childrenBuilder = new StringBuilder();
        if (toolTip != null) {
            childrenBuilder.append("\"tooltip\": \"").append(toolTip).append("\",\n"); // TODO - stringify
        }
        childrenBuilder.append("\"children\": [\n");

        String childrenString = getChildren().stream()
                .map(child -> child.toJson(new BuilderWithIndentation(indentation - 1)))
                .collect(Collectors.joining(",\n"));
        childrenBuilder.append(childrenString);
        childrenBuilder.append("]");
        return childrenBuilder.toString();
    }

    public Optional<String> getToolTip() {
        return Optional.ofNullable(toolTip);
    }

    public void setToolTip(String tooltip) {
        toolTip = tooltip;
    }

    public String toHtml() {
        throw new RuntimeException("Not yet implemented");
    }
}
