package org.lara.interpreter.weaver.generator.spec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.generator.BaseGenerator;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.exception.LanguageSpecificationException;
import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("XML Schema Validation")
class SchemaValidationTest {

    private static InputStream is(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Valid minimal spec parses with schema validation enabled")
    void validMinimalSpecParses() {
        String jp = """
            <joinpoints root_class=\"root\"> 
                <joinpoint class=\"root\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        LanguageSpecification spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));
        assertThat(spec).isNotNull();
        assertThat(spec.getRoot()).isNotNull();
        assertThat(spec.getRoot().getName()).isEqualTo("root");
    }

    @Test
    @DisplayName("Artifacts schema accepts mixed enum/object/typedef ordering")
    void mixedArtifactsOrderingAccepted() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
            </joinpoints>
            """;
        String attrs = """
            <artifacts>
                <enum name=\"Color\">
                    <value name=\"RED\"/>
                    <value name=\"BLUE\"/>
                </enum>
                <typedef name=\"Alias\">
                    <attribute name=\"color\" type=\"Color\"/>
                </typedef>
                <object name=\"Vec2\">
                    <attribute name=\"x\" type=\"String\"/>
                    <attribute name=\"y\" type=\"String\"/>
                </object>
                <artifact class=\"A\">
                    <attribute name=\"position\" type=\"Vec2\"/>
                    <attribute name=\"alias\" type=\"Alias\"/>
                </artifact>
            </artifacts>
            """;
        String actions = "<actions/>";

        LanguageSpecification spec = LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions));

        assertThat(spec.hasEnumDef("Color")).isTrue();
        assertThat(spec.hasTypeDef("Vec2")).isTrue();
        assertThat(spec.hasTypeDef("Alias")).isTrue();
        assertThat(spec.getJoinPoint("A").hasAttribute("position")).isTrue();
        assertThat(spec.getJoinPoint("A").hasAttribute("alias")).isTrue();
    }

    @Test
    @DisplayName("Malformed XML fails fast with a RuntimeException (schema + XML well-formedness)")
    void malformedXmlFails() {
        String bad = "<not-closed>";
        assertThrows(RuntimeException.class, () -> LangSpecsXmlParser.parse(is(bad), is(bad), is(bad)));
    }

    @Test
    @DisplayName("Missing required joinpoints root_class fails schema validation")
    void missingRootClassFails() {
        // joinpoints element is missing required attributes per XSD
        String jp = "<joinpoints></joinpoints>";
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        assertThrows(RuntimeException.class, () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions)));
    }

    @Test
    @DisplayName("Unknown 'extends' target triggers failure during parsing/semantic linking")
    void unknownExtendsTargetFails() {
        String jp = """
            <joinpoints root_class=\"A\"> 
                <joinpoint class=\"A\"/>
                <joinpoint class=\"B\" extends=\"Nope\"/>
            </joinpoints>
            """;
        String attrs = "<artifacts/>";
        String actions = "<actions/>";

        // The parser links extends by name; unknown target should raise at some point when building model
        LanguageSpecificationException thrown = assertThrows(LanguageSpecificationException.class,
                () -> LangSpecsXmlParser.parse(is(jp), is(attrs), is(actions), false));

        assertThat(thrown.getSimpleMessage())
                .isEqualTo("Unknown extends target 'Nope' for join point 'B'");
    }

    @Test
    @DisplayName("WeaverGenerator.newInstance(File) rejects non-existing directory with a helpful message")
    void newInstanceRejectsBadDir(@TempDir File temp) {
        File notDir = new File(temp, "no-such-dir");
        // Use default constructor so we don't trigger parsing on a folder without the 3 spec files
        BaseGenerator gen = new JavaAbstractsGenerator();
        assertThrows(RuntimeException.class, () -> gen.setLanguageSpecification(notDir));
    }
}
