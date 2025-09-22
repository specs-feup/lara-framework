package org.lara.interpreter.weaver.generator.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.generator.commandline.WeaverGenerator;
import org.lara.interpreter.weaver.generator.fixtures.SpecXmlBuilder;

import com.github.stefanbirkner.systemlambda.SystemLambda;

/**
 * CLI integration tests.
 */
public class GeneratorCliTest {

    @TempDir
    Path temp;

    @Test
    @DisplayName("Help option prints usage and does not throw")
    void helpOption() throws Exception {
        AtomicInteger exitCode = new AtomicInteger(-1);
        String output = SystemLambda.tapSystemOutNormalized(
                () -> exitCode.set(WeaverGenerator.run(new String[] { "-h" })));

        assertThat(exitCode.get()).isZero();
        assertThat(output)
                .contains("-x", "--XMLspec", "-o", "--output", "-p", "--package", "-w", "--weaver");
    }

    @Test
    @DisplayName("Missing spec directory triggers failure")
    void missingSpecDirFails() throws Exception {
        String bogus = temp.resolve("no-such").toString();
        AtomicInteger exitCode = new AtomicInteger(-1);
        String errorOutput = SystemLambda.tapSystemErrNormalized(
                () -> exitCode.set(WeaverGenerator.run(new String[] { "-x", bogus })));

        assertThat(exitCode.get()).isEqualTo(1);
        assertThat(errorOutput).containsIgnoringCase("Language Specification directory is invalid");
    }

    @Test
    @DisplayName("Successful run prints success message")
    void successfulRun() throws Exception {
        Path specDir = Files.createDirectory(temp.resolve("spec"));
        SpecXmlBuilder.writeMinimalJoinPointModel(specDir);
        SpecXmlBuilder.writeMinimalActionModel(specDir);
        SpecXmlBuilder.writeMinimalArtifacts(specDir);

        Path outputDir = temp.resolve("out");
        AtomicInteger exitCode = new AtomicInteger(-1);
        String stdOut = SystemLambda.tapSystemOutNormalized(() -> exitCode.set(WeaverGenerator.run(new String[] {
                "-x", specDir.toString(),
                "-o", outputDir.toString(),
                "-w", "CliTestWeaver" })));

        assertThat(exitCode.get()).isZero();
        assertThat(stdOut).contains("Weaver successfully created!");
    }
}
