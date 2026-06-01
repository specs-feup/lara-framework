package org.lara.weavergen2.api;

import java.nio.file.Path;

public record GeneratedArtifact(
        ArtifactKind kind,
        String qualifiedName,
        Path targetPath,
        String content) {
}
