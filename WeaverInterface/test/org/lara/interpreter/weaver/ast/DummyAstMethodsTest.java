package org.lara.interpreter.weaver.ast;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

class DummyAstMethodsTest {

    @Test
    @DisplayName("All DummyAstMethods operations throw NotImplementedException")
    void dummyMethodsThrow() {
        var engine = new TestWeaverEngine();
        var dummy = new DummyAstMethods(engine);
        var node = new Object();

        assertThatThrownBy(() -> dummy.getNodeClass())
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> dummy.toJavaJoinPoint(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> dummy.getChildren(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> dummy.getJoinPointName(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> dummy.getScopeChildren(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> dummy.getParent(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> dummy.getNumChildren(node))
                .isInstanceOf(NotImplementedException.class);
    }

    @Test
    @DisplayName("Directly hitting protected impl methods also throws NotImplementedException")
    void protectedImplsThrow() {
        var engine = new TestWeaverEngine();

        // Subclass to bypass getNodeClass() and reach the protected impls in
        // DummyAstMethods
        class Probe extends DummyAstMethods {
            Probe() {
                super(engine);
            }

            @Override
            public Class<Object> getNodeClass() {
                // Make casts succeed
                return Object.class;
            }

            // Expose protected impls
            public void callToJavaJoinPointImpl(Object n) {
                super.toJavaJoinPointImpl(n);
            }

            public void callGetChildrenImpl(Object n) {
                super.getChildrenImpl(n);
            }

            public void callGetJoinPointNameImpl(Object n) {
                super.getJoinPointNameImpl(n);
            }

            public void callGetScopeChildrenImpl(Object n) {
                super.getScopeChildrenImpl(n);
            }

            public void callGetParentImpl(Object n) {
                super.getParentImpl(n);
            }
        }

        var probe = new Probe();
        var node = new Object();

        assertThatThrownBy(() -> probe.callToJavaJoinPointImpl(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> probe.callGetChildrenImpl(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> probe.callGetJoinPointNameImpl(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> probe.callGetScopeChildrenImpl(node))
                .isInstanceOf(NotImplementedException.class);
        assertThatThrownBy(() -> probe.callGetParentImpl(node))
                .isInstanceOf(NotImplementedException.class);
    }
}
