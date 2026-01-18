package org.lara.interpreter.weaver.generator.benchmarks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small helper to synthesize Language Specification XML files for benchmarks.
 */
final class SpecFactory {

    private SpecFactory() {
    }

    /**
     * Creates a spec folder with a root join point and N leaf join points extending
     * root.
     *
     * @param dir           output directory (created if missing)
     * @param numJoinPoints number of additional join points besides root (>=0)
     */
    static void writeSpec(Path dir, int numJoinPoints) throws IOException {
        Files.createDirectories(dir);

        // Join point model
        var jp = new StringBuilder();
        jp.append("<?xml version=\"1.0\"?>\n");
        jp.append("<joinpoints root_alias=\"root\" root_class=\"root\">\n");
        jp.append("  <joinpoint class=\"root\"/>\n");
        for (int i = 0; i < numJoinPoints; i++) {
            jp.append("  <joinpoint class=\"jp").append(i).append("\" extends=\"root\"/>\n");
        }
        jp.append("</joinpoints>\n");
        Files.writeString(dir.resolve("joinPointModel.xml"), normalize(jp.toString()), StandardCharsets.UTF_8);

        // Minimal artifacts (define one global attribute to satisfy typical
        // expectations)
        var artifacts = new StringBuilder();
        artifacts.append("<?xml version=\"1.0\"?>\n");
        artifacts.append("<artifacts>\n");
        artifacts.append("  <global>\n");
        artifacts.append("    <attribute name=\"id\" type=\"String\"/>\n");
        artifacts.append("  </global>\n");
        artifacts.append("</artifacts>\n");
        Files.writeString(dir.resolve("artifacts.xml"), normalize(artifacts.toString()), StandardCharsets.UTF_8);

        // Minimal actions (empty set is allowed)
        var actions = new StringBuilder();
        actions.append("<?xml version=\"1.0\"?>\n");
        actions.append("<actions/>\n");
        Files.writeString(dir.resolve("actionModel.xml"), normalize(actions.toString()), StandardCharsets.UTF_8);
    }

    static String normalize(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }
}
