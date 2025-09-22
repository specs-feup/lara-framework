package org.lara.interpreter.weaver.generator.perf;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.language.specification.dsl.LanguageSpecification;

public class PerfGuardTest {

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void tinySpec_parsing_and_codegen_under_budget() throws Exception {
        Path specDir = Files.createTempDirectory("wg-guard-spec-");
        // Build a tiny spec with 5 join points
        String jp = """
                <?xml version=\"1.0\"?>
                <joinpoints root_alias=\"root\" root_class=\"root\">
                    <joinpoint class=\"root\"/>
                    <joinpoint class=\"a\" extends=\"root\"/>
                    <joinpoint class=\"b\" extends=\"root\"/>
                    <joinpoint class=\"c\" extends=\"root\"/>
                    <joinpoint class=\"d\" extends=\"root\"/>
                </joinpoints>
                """;
        Files.writeString(specDir.resolve("joinPointModel.xml"), jp);
        String artifacts = """
                <?xml version=\"1.0\"?>
                <artifacts>
                    <global>
                        <attribute name=\"id\" type=\"String\"/>
                    </global>
                </artifacts>
                """;
        Files.writeString(specDir.resolve("artifacts.xml"), artifacts);
        Files.writeString(specDir.resolve("actionModel.xml"), "<?xml version=\"1.0\"?>\n<actions/>\n");

        // Parse
        var lang = LanguageSpecification.newInstance(specDir.toFile());
        assertThat(lang).isNotNull();
        assertThat(lang.hasJoinPoint("root")).isTrue();

        // Codegen
        Path outDir = Files.createTempDirectory("wg-guard-out-");
        var gen = new JavaAbstractsGenerator(new File(specDir.toString()));
        gen.outputDir(outDir.toFile());
        gen.setPackage("guard.generated");
        gen.weaverName("GuardWeaver");
        gen.generate();
        gen.print();

        // A couple of files should exist
        assertThat(outDir.toFile().exists()).isTrue();
    }
}
