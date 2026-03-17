package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;

import java.util.*;

/**
 * Generates a DOT file representing the join point hierarchy.
 */
public final class DotGenerator {

    private final WeaverModel model;

    public DotGenerator(WeaverModel model) {
        this.model = model;
    }

    public String generate() {
        var sb = new StringBuilder();

        sb.append("digraph JoinPointHierarchy {\n");
        sb.append("    rankdir=TB;\n");
        sb.append("    node [shape=box, style=filled, fillcolor=lightyellow];\n");
        sb.append("\n");

        // Global node
        sb.append("    \"").append(model.getGlobal().getName()).append("\" [fillcolor=lightblue];\n");

        // Root node
        model.getRoot().ifPresent(root ->
                sb.append("    \"").append(root.getName()).append("\" [fillcolor=lightgreen];\n")
        );

        sb.append("\n");

        // Edges
        for (var jp : model.getJoinPoints().values()) {
            jp.getParent().ifPresent(parent ->
                    sb.append("    \"").append(parent.getName()).append("\" -> \"")
                            .append(jp.getName()).append("\";\n")
            );
        }

        sb.append("}\n");
        return sb.toString();
    }
}
