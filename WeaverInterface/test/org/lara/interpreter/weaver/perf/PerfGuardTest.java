package org.lara.interpreter.weaver.perf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.fixtures.TestJoinPoint;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.utils.SourcesGatherer;

class PerfGuardTest {

    @Test
    void eventTrigger_tiny_shouldFinishQuickly() {
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            var trigger = new EventTrigger();
            trigger.registerReceiver(new AGear() {
            }); // no-op gear

            var jp = new TestJoinPoint("test");
            for (int i = 0; i < 1_000; i++) {
                trigger.triggerAction(Stage.BEGIN, "guard", jp, List.of(), Optional.empty());
                trigger.triggerAction(Stage.END, "guard", jp, List.of(), Optional.of(i));
            }
        });
    }

    @Test
    void sourcesGatherer_tiny_shouldFinishQuickly(@TempDir Path root) throws IOException {
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            Files.writeString(root.resolve("a.c"), "x");
            Files.writeString(root.resolve("b.h"), "x");
            Files.writeString(root.resolve("c.txt"), "x");

            var map = SourcesGatherer.build(List.of(root.toFile()), List.of("c", "h")).getSourceFiles();
            assertThat(map).hasSize(2);
        });
    }

    @Test
    void descendants_tiny_shouldFinishQuickly() {
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            var root = buildTree(3, 4);
            var list = root.getJpDescendants();
            var count = root.getJpDescendantsStream().count();
            assertThat(list).hasSize((int) count);
        });
    }

    private static TestJoinPoint buildTree(int branching, int depth) {
        var root = new TestJoinPoint("root");
        build(root, branching, depth - 1);
        return root;
    }

    private static void build(TestJoinPoint parent, int branching, int depth) {
        if (depth < 0)
            return;
        for (int i = 0; i < branching; i++) {
            var child = new TestJoinPoint("n");
            parent.addChild(child);
            build(child, branching, depth - 1);
        }
    }
}
