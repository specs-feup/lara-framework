package org.lara.interpreter.weaver.ast;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

class DummyAstMethodsTest {

    private DummyAstMethods dummyAstMethods;
    private TestWeaverEngine testEngine;

    @BeforeEach
    void setUp() {
        testEngine = new TestWeaverEngine();
        dummyAstMethods = new DummyAstMethods(testEngine);
    }

    @Test
    void getNodeClass_shouldThrowNotImplementedException() {
        assertThatThrownBy(() -> dummyAstMethods.getNodeClass())
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void toJavaJoinPoint_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        assertThatThrownBy(() -> dummyAstMethods.toJavaJoinPoint(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getJoinPointName_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        assertThatThrownBy(() -> dummyAstMethods.getJoinPointName(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getChildren_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        assertThatThrownBy(() -> dummyAstMethods.getChildren(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getNumChildren_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        assertThatThrownBy(() -> dummyAstMethods.getNumChildren(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getScopeChildren_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        assertThatThrownBy(() -> dummyAstMethods.getScopeChildren(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getParent_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        assertThatThrownBy(() -> dummyAstMethods.getParent(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getDescendants_shouldThrowNotImplementedException() {
        Object testNode = new Object();
        
        // getDescendants is implemented in AAstMethods but uses abstract methods
        // Since all abstract methods throw NotImplementedException, getDescendants should also fail
        assertThatThrownBy(() -> dummyAstMethods.getDescendants(testNode))
                .isInstanceOf(NotImplementedException.class)
                .hasMessageContaining("DummyAstMethods");
    }

    @Test
    void getRoot_shouldDelegateToEngine() {
        // getRoot is implemented in AAstMethods and delegates to engine's getRootNode()
        // This should work even with DummyAstMethods since it doesn't use the abstract methods
        Object root = dummyAstMethods.getRoot();
        
        // The root should be the same as the engine's root node
        assertThat(root).isSameAs(testEngine.getRootNode());
    }
}