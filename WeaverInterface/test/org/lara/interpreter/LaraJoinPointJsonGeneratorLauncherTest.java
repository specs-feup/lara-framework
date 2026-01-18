package org.lara.interpreter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LaraJoinPointJsonGeneratorLauncherTest {

    @Test
    @DisplayName("Launcher generates LaraJoinPoint JSON without throwing")
    void generatesJson(@TempDir Path tempDir) throws IOException {
        // Use a custom output path inside the temporary directory
        File out = tempDir.resolve("LaraJoinPointSpecification.json").toFile();
        LaraJoinPointJsonGeneratorLauncher.main(new String[] { out.getAbsolutePath() });

        assertThat(out).exists();
        assertThat(out.length()).isGreaterThan(100);
    }
}
