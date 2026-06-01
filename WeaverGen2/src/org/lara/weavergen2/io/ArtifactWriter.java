package org.lara.weavergen2.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.lara.weavergen2.api.GeneratedArtifact;

public final class ArtifactWriter {

    public void write(Path outputDir, List<GeneratedArtifact> artifacts) throws IOException {
        for (var artifact : artifacts) {
            var target = artifact.targetPath().isAbsolute()
                    ? artifact.targetPath()
                    : outputDir.resolve(artifact.targetPath());

            var parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(target, artifact.content());
        }
    }
}
