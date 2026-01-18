package integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.fixtures.TestGear;
import org.lara.interpreter.weaver.fixtures.TestWeaverEngine;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;

class WeaverInterfaceE2ETest {

    @AfterEach
    void cleanupThreadLocal() {
        if (WeaverEngine.isWeaverSet()) {
            WeaverEngine.removeWeaver();
        }
    }

    @Test
    @DisplayName("Event flow through insert/insertFar with TestGear and active toggle")
    void eventFlow_insert_and_insertFar() {
        var engine = new TestWeaverEngine();
        engine.setWeaver();

        var trigger = new EventTrigger();
        var gear = new TestGear();
        trigger.registerReceiver(gear);
        engine.setEventTrigger(trigger);

        var root = engine.getRootJp();

        // insert with String
        root.insert("before", "code-snippet");
        // insert with JP
        var other = new org.lara.interpreter.weaver.fixtures.TestJoinPoint("node");
        root.insert("after", other);
        // insertFar variants
        root.insertFar("replace", "far-code");
        root.insertFar("before", other);

        // Expect 8 action events: for insert(String) BEGIN+END, insert(JP) BEGIN+END,
        // insertFar(String) BEGIN+END, insertFar(JP) BEGIN+END
        assertThat(gear.getActionEvents()).hasSize(8);
        // Sanity check ordering and names
        var events = gear.getActionEvents();
        assertThat(events.get(0).getStage().getName()).isEqualTo("begin");
        assertThat(events.get(0).getActionName()).isEqualTo("insert");
        assertThat(events.get(1).getStage().getName()).isEqualTo("end");
        assertThat(events.get(1).getActionName()).isEqualTo("insert");
        assertThat(events.get(2).getActionName()).isEqualTo("insert");
        assertThat(events.get(4).getActionName()).isEqualTo("insertFar");
        assertThat(events.get(6).getActionName()).isEqualTo("insertFar");

        // Toggle gear off and ensure no more events are collected
        gear.setActive(false);
        root.insert("before", "no-capture");
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
        var jp = (JoinPoint) ast.toJavaJoinPoint(rootNode);
        assertThat(jp.getJoinPointType()).isEqualTo("root");
        var name = (String) ast.getJoinPointName(rootNode);
        assertThat(name).isEqualTo("root");
    }

    @Test
    @DisplayName("JSON spec generation (safe): build LanguageSpecification from JoinPoint.getLaraJoinPoint() and verify top-level fields")
    void jsonSpecGeneration_like_flow() {
        // Build a minimal language specification using the static LARA join point as a
        // base
        JoinPointClass base = JoinPoint.getLaraJoinPoint();

        // Declare a simple root JP and set a default attribute
        JoinPointClass root = new JoinPointClass("root");
        root.setDefaultAttribute("dump");

        LanguageSpecification spec = new LanguageSpecification(root, null);
        spec.add(root);
        spec.setRoot(root);
        spec.setGlobal(base);

        // The spec should report the root and global join points properly
        assertThat(spec.getRoot().getName()).isEqualTo("root");
        // Global JP name comes from JoinPoint.getLaraJoinPoint() => "LaraJoinPoint"
        assertThat(spec.getGlobal().getName()).isEqualTo("LaraJoinPoint");
        assertThat(spec.getRoot().getDefaultAttribute().orElse("<none>")).isEqualTo("dump");

        // A pseudo-JSON-like check via toString() DSL contents to ensure non-empty &
        // contains expected names
        String dsl = spec.toString();
        assertThat(dsl).isNotEmpty();
        assertThat(dsl).contains("root");
    }
}
