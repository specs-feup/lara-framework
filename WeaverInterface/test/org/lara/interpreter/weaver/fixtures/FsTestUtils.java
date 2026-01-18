package org.lara.interpreter.weaver.fixtures;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class FsTestUtils {

    private FsTestUtils() {}

    public static File writeFile(File dir, String name, String content) throws IOException {
        File f = new File(dir, name);
        Files.writeString(f.toPath(), content, StandardCharsets.UTF_8);
        return f;
    }

    public static File mkdir(File dir, String name) {
        File f = new File(dir, name);
        //noinspection ResultOfMethodCallIgnored
        f.mkdirs();
        return f;
    }
}
