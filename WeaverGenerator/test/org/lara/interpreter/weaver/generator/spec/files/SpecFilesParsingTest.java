package org.lara.interpreter.weaver.generator.spec.files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.language.specification.dsl.LanguageSpecification;

@DisplayName("File-based Spec Parsing")
class SpecFilesParsingTest {

    @Test
    @DisplayName("Parse valid/medium spec triplet from classpath into temp dir")
    void parseValidMedium(@TempDir File temp) {
        copySpecFolder("spec/valid/medium", temp);
        var spec = LanguageSpecification.newInstance(temp);
        assertThat(spec).isNotNull();
        assertThat(spec.getRoot().getName()).isEqualTo("file");
        assertThat(spec.getJoinPoint("function")).isNotNull();
        assertThat(spec.getAttribute("language")).isNotEmpty();
        assertThat(spec.getAction("insert")).isNotEmpty();
    }

    @Test
    @DisplayName("Invalid schema (missing root_class) fails when parsing directory")
    void invalidSchemaMissingRootClass(@TempDir File temp) {
        // Mix-and-match valid artifacts/actions with invalid joinPointModel schema
        copySpecResource("spec/invalid/schema/joinPointModel_missing_root_class.xml",
                new File(temp, "joinPointModel.xml"));
        copySpecResource("spec/valid/minimal/artifacts.xml", new File(temp, "artifacts.xml"));
        copySpecResource("spec/valid/minimal/actionModel.xml", new File(temp, "actionModel.xml"));

        assertThrows(RuntimeException.class, () -> LanguageSpecification.newInstance(temp));
    }

    private static void copySpecFolder(String folder, File destDir) {
        copySpecResource(folder + "/joinPointModel.xml", new File(destDir, "joinPointModel.xml"));
        copySpecResource(folder + "/artifacts.xml", new File(destDir, "artifacts.xml"));
        copySpecResource(folder + "/actionModel.xml", new File(destDir, "actionModel.xml"));
    }

    private static void copySpecResource(String resourcePath, File target) {
        try (InputStream in = SpecFilesParsingTest.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Error copying resource: " + resourcePath, e);
        }
    }
}
