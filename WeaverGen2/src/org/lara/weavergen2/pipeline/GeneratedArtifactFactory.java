package org.lara.weavergen2.pipeline;

import java.nio.file.Path;

import org.lara.weavergen2.api.ArtifactKind;
import org.lara.weavergen2.api.GeneratedArtifact;

public final class GeneratedArtifactFactory {

    public GeneratedArtifact javaArtifact(ArtifactKind kind, String packageName, String fileName, String content) {
        return new GeneratedArtifact(kind, packageName + "." + fileName.replaceFirst("\\.java$", ""),
                packagePath(packageName).resolve(fileName), content);
    }

    public GeneratedArtifact jsonArtifact(String packageName, String weaverName, Path path, String content) {
        return new GeneratedArtifact(ArtifactKind.JSON_SPEC, packageName + "." + weaverName, path, content);
    }

    public Path packagePath(String packageName) {
        return Path.of(packageName.replace('.', '/'));
    }
}
