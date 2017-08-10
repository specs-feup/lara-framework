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

public class JsonWriter {

    // public static String toJson(RootNode node) {
    // return node.toJson();
    // }

    // private static String toJson(LangSpecNode node, int identationLevel) {
    // return toJson(node.getThis());
    // }
    //
    // private static String toJson(RootNode node, int identationLevel) {
    //
    // BuilderWithIndentation builder = new BuilderWithIndentation(identationLevel);
    //
    // builder.addLines("{");
    // builder.increaseIndentation();
    //
    // builder.addLines("\"root\": \"" + node.getRootName() + "\",");
    // builder.addLines("\"rootAlias\": \"" + node.getRootAlias() + "\",");
    //
    // // Add children
    // List<LangSpecNode> children = node.getChildren();
    // // if (children.size() == 0) --> always has at least the Global join point
    //
    // StringBuilder childrenBuilder = new StringBuilder();
    // childrenBuilder.append("\"children\": [\n");
    //
    // String childrenString = children.stream()
    // .map(child -> (LangSpecNode) child)
    // .map(child -> JsonWriter.toJson((LangSpecNode) child, builder.getCurrentIdentation() - 1))
    // .collect(Collectors.joining(",\n"));
    // // for (ClangNode child : children) {
    // // childrenBuilder.append(JsonWriter.toJson((GenericAstNode) child, builder.getCurrentIdentation() + 1));
    // // }
    // childrenBuilder.append(childrenString);
    // childrenBuilder.append("],");
    //
    // builder.addLines(childrenBuilder.toString());
    // builder.decreaseIndentation();
    // builder.add("}");
    //
    // return builder.toString();
    // }
}
