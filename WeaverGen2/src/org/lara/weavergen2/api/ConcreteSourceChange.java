package org.lara.weavergen2.api;

import java.nio.file.Path;

public record ConcreteSourceChange(ConcreteSourceChangeKind kind, Path path, String message) {
}
