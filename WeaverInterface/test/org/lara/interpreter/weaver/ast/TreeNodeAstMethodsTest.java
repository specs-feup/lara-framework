package org.lara.interpreter.weaver.ast;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

import pt.up.fe.specs.util.treenode.ATreeNode;

class TreeNodeAstMethodsTest {

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
    @DisplayName("children array, numChildren, parent and scopeChildren mapping")
    void childrenAndParents() {
        var engine = new TestWeaverEngine();
        var root = new N("root");
        var body = new N("body");
        var c1 = new N("c1");
        var c2 = new N("c2");
        root.addChild(body);
        body.addChild(c1);
        body.addChild(c2);

        var ast = new TreeNodeAstMethods<>(
                engine,
                N.class,
                node -> new TestJoinPoint("node", node),
                node -> node.name,
                node -> node == root ? List.of(body) : node.getChildren());

        // children of body
        var bodyChildren = (Object[]) ast.getChildren(body);
        assertThat(bodyChildren).containsExactly(c1, c2);

        // num children
        var num = (Integer) ast.getNumChildren(body);
        assertThat(num).isEqualTo(2);

        // parent
        var parent = ast.getParent(c1);
        assertThat(parent).isSameAs(body);

        // scope children of root: only body
        var scope = (Object[]) ast.getScopeChildren(root);
        assertThat(scope).containsExactly(body);
    }

    @Test
    @DisplayName("toJavaJoinPointImpl and getJoinPointNameImpl delegation")
    void joinPointMapping() {
        var engine = new TestWeaverEngine();
        var node = new N("x");

        var ast = new TreeNodeAstMethods<>(
                engine,
                N.class,
                n -> new TestJoinPoint("jp-" + n.name, n),
                n -> "name-" + n.name,
                N::getChildren);

        var jp = ast.toJavaJoinPoint(node);
        assertThat(jp).isInstanceOf(TestJoinPoint.class);
        assertThat(((TestJoinPoint) jp).get_class()).isEqualTo("jp-x");

        var name = ast.getJoinPointName(node);
        assertThat(name).isEqualTo("name-x");
    }
}
