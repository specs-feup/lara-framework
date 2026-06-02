package org.lara.weavergen2.source;

import java.nio.file.Path;
import java.util.List;

public record ConcreteSourceSync(
        List<Path> createdFiles,
        List<NonConformingConcreteSource> nonConformingFiles) {

    public ConcreteSourceSync {
        createdFiles = List.copyOf(createdFiles);
        nonConformingFiles = List.copyOf(nonConformingFiles);
    }
}
