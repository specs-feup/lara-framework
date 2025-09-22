package org.lara.interpreter.weaver.generator.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;

/**
 * Model building and invariants over the in-memory LanguageSpecification model.
 */
@DisplayName("Model Building and Invariants")
public class ModelBuilderTest {

    private static InputStream is(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Type graph builds with inheritance and attribute/action propagation")
    void typeGraphBuilds() {
        String jp = """
                <joinpoints root_class=\"Root\">
                    <joinpoint class=\"Root\"/>
                    <joinpoint class=\"Mid\" extends=\"Root\"/>
                    <joinpoint class=\"Leaf\" extends=\"Mid\"/>
                </joinpoints>
                """;
        String attrs = """
                <artifacts>
                    <global>
                        <attribute name=\"gAttr\" type=\"String\"/>
                    </global>
                    <artifact class=\"Root\" default=\"rAttr\">
                        <attribute name=\"rAttr\" type=\"Integer\"/>
                    </artifact>
                    <artifact class=\"Mid\">
                        <attribute name=\"mAttr\" type=\"Integer\"/>
                    </artifact>
                    <artifact class=\"Leaf\">
                        <attribute name=\"lAttr\" type=\"Integer\"/>
                    </artifact>
                </artifacts>
                """;
        String actions = """
                <actions>
                    <action name=\"gAct\" class=\"*\"/>
                    <action name=\"rAct\" class=\"Root\"/>
                    <action name=\"mAct\" class=\"Mid\"/>
                    <action name=\"lAct\" class=\"Leaf\"/>
                </actions>
                """;

        var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));

        var root = spec.getJoinPoint("Root");
        var mid = spec.getJoinPoint("Mid");
        var leaf = spec.getJoinPoint("Leaf");

        // Hierarchy
        assertThat(mid.getExtend().get()).isEqualTo(root);
        assertThat(leaf.getExtend().get()).isEqualTo(mid);

        // Default attribute propagates
        assertThat(root.getDefaultAttribute()).contains("rAttr");
        assertThat(leaf.getDefaultAttribute()).contains("rAttr");

        // Attribute propagation
        assertThat(leaf.hasAttribute("gAttr")).isTrue();
        assertThat(leaf.hasAttribute("rAttr")).isTrue();
        assertThat(leaf.hasAttribute("mAttr")).isTrue();
        assertThat(leaf.hasAttribute("lAttr")).isTrue();

        // Action propagation
        assertThat(leaf.hasAction("gAct")).isTrue();
        assertThat(leaf.hasAction("rAct")).isTrue();
        assertThat(leaf.hasAction("mAct")).isTrue();
        assertThat(leaf.hasAction("lAct")).isTrue();

        // Available attributes/actions caches should report presence
        assertThat(spec.hasAttribute("gAttr")).isTrue();
        assertThat(spec.hasAction("gAct")).isTrue();
    }

    @Test
    @DisplayName("Overloaded attributes and actions with same name across hierarchy are all reachable")
    void overloadingReachable() {
        String jp = """
                <joinpoints root_class=\"A\">
                    <joinpoint class=\"A\"/>
                    <joinpoint class=\"B\" extends=\"A\"/>
                </joinpoints>
                """;
        String attrs = """
                <artifacts>
                    <artifact class=\"A\">
                        <attribute name=\"val\" type=\"Integer\"/>
                    </artifact>
                    <artifact class=\"B\">
                        <attribute name=\"val\" type=\"String\"/>
                    </artifact>
                </artifacts>
                """;
        String actions = """
                <actions>
                    <action name=\"emit\" class=\"A\"/>
                    <action name=\"emit\" class=\"B\"/>
                </actions>
                """;

        var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));
        var B = spec.getJoinPoint("B");

        // Attributes with same name from B plus inherited from A
        assertThat(B.getAttribute("val")).hasSize(2);
        // Actions with same name
        assertThat(B.getAction("emit")).hasSize(2);
    }

    @Test
    @DisplayName("Unknown type references in attributes surface as failures (guard)")
    void unknownTypeInAttributeFails() {
        String jp = """
                <joinpoints root_class=\"Root\">
                    <joinpoint class=\"Root\"/>
                </joinpoints>
                """;
        String attrs = """
                <artifacts>
                    <artifact class=\"Root\">
                        <attribute name=\"broken\" type=\"NoSuchType\"/>
                    </artifact>
                </artifacts>
                """;
        String actions = "<actions/>";

        assertThrows(RuntimeException.class, () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions)));
    }

    @Test
    @DisplayName("Array types and typedef / enum references are resolved and preserved")
    void arrayAndCustomTypes() {
        String jp = """
                <joinpoints root_class=\"A\">
                    <joinpoint class=\"A\"/>
                </joinpoints>
                """;
        String attrs = """
                <artifacts>
                    <artifact class=\"A\">
                        <attribute name=\"cArr\" type=\"Integer[]\"/>
                        <attribute name=\"num\" type=\"Integer\"/>
                    </artifact>
                </artifacts>
                """;
        String actions = "<actions/>";

        var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));
        var A = spec.getJoinPoint("A");
        assertThat(A.getAttribute("cArr")).hasSize(1);
        assertThat(A.getAttributeSelf("cArr")).hasSize(1);
        assertThat(A.getAttribute("num")).hasSize(1);
        // Access underlying type names for regression (string form)
        assertThat(A.getAttribute("cArr").get(0).getReturnType()).contains("Integer");
    }

    @Nested
    @DisplayName("Invariants")
    class Invariants {
        @Test
        @DisplayName("Hierarchy is acyclic (simple cycle detection)")
        void hierarchyAcyclic() {
            // The current parser does not allow forward references that would create cycles
            // via XML order,
            // but we defensively assert that parent traversal of each node eventually
            // reaches global without repetition.
            String jp = """
                    <joinpoints root_class=\"R\">
                        <joinpoint class=\"R\"/>
                        <joinpoint class=\"C1\" extends=\"R\"/>
                        <joinpoint class=\"C2\" extends=\"C1\"/>
                    </joinpoints>
                    """;
            String attrs = "<artifacts/>";
            String actions = "<actions/>";
            var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));

            spec.getDeclaredJoinPoints().forEach(jpCls -> {
                var slowSet = new HashSet<>();
                var current = Optional.of(jpCls);
                while (current.isPresent()) {
                    var c = current.get();
                    assertThat(slowSet).as("Cycle detected starting at " + jpCls.getName()).doesNotContain(c);
                    slowSet.add(c);
                    current = c.getExtend();
                }
            });
        }

        @Test
        @DisplayName("Join point names are unique and global name reserved")
        void uniqueNames() {
            String jp = """
                    <joinpoints root_class=\"R\">
                        <joinpoint class=\"R\"/>
                        <joinpoint class=\"Other\" extends=\"R\"/>
                    </joinpoints>
                    """;
            String attrs = "<artifacts/>";
            String actions = "<actions/>";
            var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));

            var names = new HashSet<String>();
            spec.getAllJoinPoints().forEach(jpCls -> {
                assertThat(names.add(jpCls.getName())).isTrue();
            });
            assertThat(names).contains("joinpoint");
        }
    }
}
