package org.lara.interpreter.weaver.ast;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

import pt.up.fe.specs.util.treenode.ATreeNode;

class AAstMethodsTest {

    // Minimal tree node for tests
    static class N extends ATreeNode<N> {
        private final String name;

        N(String name) {
            super(null);
            this.name = name;
        }

        @Override
        protected N copyPrivate() {
            return new N(name);
        }

        @Override
        public String toContentString() {
            return name;
        }
    }

    @Test
    @DisplayName("getDescendants() gathers children in pre-order (AAstMethods)")
    void descendantsPreOrder() {
        var engine = new TestWeaverEngine();
        var root = new N("root");
        var c1 = new N("c1");
        var c2 = new N("c2");
        var gc1 = new N("gc1");
        c1.addChild(gc1);
        root.addChild(c1);
        root.addChild(c2);

        var ast = new TreeNodeAstMethods<>(
                engine,
                N.class,
                node -> new TestJoinPoint("node", node),
                node -> node.name,
                node -> node.getChildren());

        @SuppressWarnings("unchecked")
        var desc = (List<Object>) ast.getDescendants(root);

        assertThat(desc).containsExactly(c1, gc1, c2);
    }

    @Test
    @DisplayName("getRoot() delegates to engine root node")
    void getRootDelegatesToEngine() {
        var engine = new TestWeaverEngine();
        var ast = new TreeNodeAstMethods<>(
                engine,
                N.class,
                node -> new TestJoinPoint("node", node),
                node -> node.name,
                node -> node.getChildren());

        var rootFromAst = ast.getRoot();
        var rootFromEngine = engine.getRootNode();

        assertThat(rootFromAst).isSameAs(rootFromEngine);
    }
}
