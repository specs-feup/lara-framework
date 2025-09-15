package org.lara.interpreter.weaver.generator.fixtures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Helper to build minimal language specification XML files for tests.
 */
public final class SpecXmlBuilder {

    private SpecXmlBuilder() {}

    public static Path writeMinimalJoinPointModel(Path dir) throws IOException {
        String xml = """
            <model version=\"1.0\">
              <joinpoint class=\"root\"/>
            </model>
            """;
        Path out = dir.resolve("joinPointModel.xml");
        Files.writeString(out, normalize(xml), StandardCharsets.UTF_8);
        return out;
    }

    public static Path writeMinimalActionModel(Path dir) throws IOException {
        String xml = """
            <actions version=\"1.0\"/>
            """;
        Path out = dir.resolve("actionModel.xml");
        Files.writeString(out, normalize(xml), StandardCharsets.UTF_8);
        return out;
    }

    public static Path writeMinimalArtifacts(Path dir) throws IOException {
        String xml = """
            <artifacts version=\"1.0\"/>
            """;
        Path out = dir.resolve("artifacts.xml");
        Files.writeString(out, normalize(xml), StandardCharsets.UTF_8);
        return out;
    }

    public static String normalize(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }
}
