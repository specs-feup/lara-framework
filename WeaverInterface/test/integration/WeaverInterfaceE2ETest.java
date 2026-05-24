package integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.fixtures.TestGear;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;
import org.lara.interpreter.weaver.interf.enums.InsertPosition;

class WeaverInterfaceE2ETest {

    @Test
    @DisplayName("Event flow through insert/insertFar with TestGear and active toggle")
    void eventFlow_insert_and_insertFar() {
        var engine = new TestWeaverEngine();

        var trigger = new EventTrigger();
        var gear = new TestGear();
        trigger.registerReceiver(gear);
        engine.setEventTrigger(trigger);

        var root = engine.getRootJp();

        // insert with String
        root.insertImpl(InsertPosition.BEFORE, "code-snippet");
        // insert with JP
        var other = new TestJoinPoint(engine, "node");
        root.insertImpl(InsertPosition.AFTER, other);
        // insertFar variants
        root.insertImpl(InsertPosition.REPLACE, "far-code");
        root.insertImpl(InsertPosition.BEFORE, other);

        // Expect 8 action events: for insert(String) BEGIN+END, insert(JP) BEGIN+END,
        // insert(String) BEGIN+END, insert(JP) BEGIN+END
        assertThat(gear.getActionEvents()).hasSize(8);
        // Sanity check ordering and names
        var events = gear.getActionEvents();
        assertThat(events.get(0).getStage().getName()).isEqualTo("begin");
        assertThat(events.get(0).getActionName()).isEqualTo("insert");
        assertThat(events.get(1).getStage().getName()).isEqualTo("end");
        assertThat(events.get(1).getActionName()).isEqualTo("insert");
        assertThat(events.get(2).getActionName()).isEqualTo("insert");
        assertThat(events.get(4).getActionName()).isEqualTo("insert");
        assertThat(events.get(6).getActionName()).isEqualTo("insert");

        // Toggle gear off and ensure no more events are collected
        gear.setActive(false);
        root.insertImpl(InsertPosition.BEFORE, "no-capture");
        assertThat(gear.getActionEvents()).hasSize(8);
    }

    @Test
    @DisplayName("Options wiring: getOptions -> getStoreDefinition contains keys; DataStore round-trip not performed here")
    void optionsWiring_storeDefinitionContainsKeys() {
        var engine = new TestWeaverEngine();
        var def = engine.getStoreDefinition();
        assertThat(def.hasKey("verbose")).isTrue();
        assertThat(def.hasKey("target")).isTrue();
    }

    @Test
    @DisplayName("AST bridge: TreeNodeAstMethods from TestWeaverEngine; verify root node, children, descendants and name mapping")
    void astBridge_end_to_end() {
        var engine = new TestWeaverEngine();
        var ast = engine.getAstMethods();

        var rootNode = ast.getRoot();
        // children of root
        Object[] children = (Object[]) ast.getChildren(rootNode);
        assertThat(children).hasSize(1);

        // descendants should include the single child
        @SuppressWarnings("unchecked")
        var desc = (java.util.List<Object>) ast.getDescendants(rootNode);
        assertThat(desc).hasSize(1).containsExactly(children[0]);

        // Mapping to join point and name
        var jp = (TestJoinPoint) ast.toJavaJoinPoint(rootNode);
        assertThat(jp.getJoinPointTypeImpl()).isEqualTo("root");
        var name = (String) ast.getJoinPointName(rootNode);
        assertThat(name).isEqualTo("root");
    }
}
