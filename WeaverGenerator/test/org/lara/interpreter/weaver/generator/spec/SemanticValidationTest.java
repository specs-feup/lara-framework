package org.lara.interpreter.weaver.generator.spec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.exception.LanguageSpecificationException;
import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Semantic Validation")
class SemanticValidationTest {

    private static InputStream is(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Valid minimal hierarchy builds and exposes root and global")
    void validHierarchyBuilds() {
        String jp = """
            <joinpoints root_class=\"root\"> 
                <joinpoint class=\"root\"/>
                <joinpoint class=\"child\" extends=\"root\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));
        assertThat(spec.getRoot().getName()).isEqualTo("root");
        assertThat(spec.getJoinPoint("child").getExtend().get().getName()).isEqualTo("root");

        // global joinpoint must be available
        assertThat(spec.getGlobal()).isNotNull();
        assertThat(spec.getGlobal().getName()).isEqualTo("joinpoint");
    }

    @Test
    @DisplayName("Duplicate joinpoint classes are rejected by parser or model")
    void duplicateJoinPointsRejected() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
                <joinpoint class=\"A\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions), false));

        assertThat(thrown.getSimpleMessage())
                .isEqualTo("Duplicate join point class 'A' while building language specification");
    }

    @Test
    @DisplayName("Hyphenated identifiers are rejected with a descriptive error")
    void hyphenatedNamesRejected() {
        String jp = """
            <joinpoints root_class=\"Root\"> 
                <joinpoint class=\"Root\"/>
                <joinpoint class=\"bad-name\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions), false));

        assertThat(thrown.getSimpleMessage())
                .isEqualTo("Identifier 'bad-name' for join point name must follow Java identifier rules");
    }

    @Test
    @DisplayName("Identifiers starting with digits are rejected")
    void digitPrefixedNamesRejected() {
        String jp = """
            <joinpoints root_class=\"Root\"> 
                <joinpoint class=\"Root\"/>
                <joinpoint class=\"1bad\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions), false));

        assertThat(thrown.getSimpleMessage())
                .isEqualTo("Identifier '1bad' for join point name must follow Java identifier rules");
    }

    @Test
    @DisplayName("Inheritance cycles are rejected with a descriptive error")
    void inheritanceCyclesRejected() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\" extends=\"A\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions), false));

        assertThat(thrown.getSimpleMessage())
                .isEqualTo("Inheritance cycle detected for join point 'A'");
    }

    @Test
    @DisplayName("Duplicate action signatures per joinpoint are rejected")
    void duplicateActionSignaturesRejected() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = """
            <actions>
                <action name=\"dup\" class=\"A\">
                    <parameter name=\"p\" type=\"String\"/>
                </action>
                <action name=\"dup\" class=\"A\">
                    <parameter name=\"p\" type=\"String\"/>
                </action>
            </actions>
            """;

        LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions)));

        assertThat(thrown.getSimpleMessage())
                .isEqualTo("Duplicate action signature 'dup(String)' for join point 'A'");
    }

    @Test
    @DisplayName("Attributes and actions are attached to correct joinpoints, and global ones propagate")
    void attributesAndActionsPropagate() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
                <joinpoint class=\"B\" extends=\"A\"/>
            </joinpoints>
            """;
        String attrs = """
            <artifacts>
                <global>
                    <attribute name=\"gAttr\" type=\"String\"/>
                </global>
                <artifact class=\"A\">
                    <attribute name=\"aAttr\" type=\"Integer\"/>
                </artifact>
            </artifacts>
            """;
        String actions = """
            <actions>
                <action name=\"gAct\" class=\"*\"/>
                <action name=\"aAct\" class=\"A\"/>
            </actions>
            """;

        var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));

        // Global
        assertThat(spec.getGlobal().hasAttributeSelf("gAttr")).isTrue();
        assertThat(spec.getGlobal().hasActionSelf("gAct")).isTrue();

        // A
        var A = spec.getJoinPoint("A");
        assertThat(A.hasAttribute("gAttr")).isTrue();
        assertThat(A.hasAttribute("aAttr")).isTrue();
        assertThat(A.hasAction("gAct")).isTrue();
        assertThat(A.hasAction("aAct")).isTrue();

        // B inherits
        var B = spec.getJoinPoint("B");
        assertThat(B.hasAttribute("gAttr")).isTrue();
        assertThat(B.hasAttribute("aAttr")).isTrue();
        assertThat(B.hasAction("gAct")).isTrue();
        assertThat(B.hasAction("aAct")).isTrue();
    }

    @Test
    @DisplayName("Type resolution errors (unknown typedef/enum/jp) are surfaced deterministically")
    void typeResolutionErrors() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
            </joinpoints>
            """;
        String attrsUnknownType = """
            <artifacts>
                <artifact class=\"A\">
                    <attribute name=\"bad\" type=\"UnknownType\"/>
                </artifact>
            </artifacts>
            """;
        String actions = "<actions/>";

        assertThrows(RuntimeException.class, () -> LangSpecsXmlParser.parse(is(jp), is(attrsUnknownType), is(actions)));
    }

    @Test
    @DisplayName("Global default attribute propagates down hierarchy when set in artifacts")
    void defaultAttributePropagation() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
                <joinpoint class=\"B\" extends=\"A\"/>
            </joinpoints>
            """;
        String attrs = """
            <artifacts>
                <artifact class=\"A\" default=\"aAttr\">
                    <attribute name=\"aAttr\" type=\"String\"/>
                </artifact>
            </artifacts>
            """;
        String actions = "<actions/>";

        var spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));
        assertThat(spec.getJoinPoint("A").getDefaultAttribute()).isPresent();
        assertThat(spec.getJoinPoint("B").getDefaultAttribute()).isPresent();
        assertThat(spec.getJoinPoint("B").getDefaultAttribute().get()).isEqualTo("aAttr");
    }
}
