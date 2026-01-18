package org.lara.interpreter.joptions.keys;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pt.up.fe.specs.util.SpecsIo;

class FileListTest {

    @TempDir
    File tmp;

    @Test
    @DisplayName("Construction variants and isEmpty")
    void constructionAndEmpty() {
        assertThat(FileList.newInstance().isEmpty()).isTrue();
        assertThat(new FileList(new ArrayList<>()).isEmpty()).isTrue();
        assertThat(FileList.newInstance((String) null).isEmpty()).isTrue();

        File a = new File("a");
        File b = new File("b");
        assertThat(FileList.newInstance(a, b).isEmpty()).isFalse();
        assertThat(FileList.newInstance(List.of(a, b)).getFiles()).containsExactly(a, b);
    }

    @Test
    @DisplayName("encode/decode round-trip using universal path separator")
    void encodeDecodeRoundTrip() throws IOException {
        File f1 = new File(tmp, "one.txt");
        File f2 = new File(tmp, "two.txt");
        SpecsIo.write(f1, "1");
        SpecsIo.write(f2, "2");

        FileList fl = FileList.newInstance(f1, f2);
        String encoded = fl.encode();
        // decode via string constructor
        FileList decoded = FileList.newInstance(encoded);

        assertThat(decoded.getFiles()).containsExactlyElementsOf(fl.getFiles());
        assertThat(decoded).isEqualTo(fl);
        assertThat(decoded.hashCode()).isEqualTo(fl.hashCode());
    }

    @Test
    @DisplayName("Iterator and toString join with universal separator")
    void iteratorAndToString() {
        File a = new File("a");
        File b = new File("b");
        FileList fl = FileList.newInstance(a, b);

        Iterator<File> it = fl.iterator();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(a);
        assertThat(it.next()).isEqualTo(b);
        assertThat(it.hasNext()).isFalse();

        String sep = SpecsIo.getUniversalPathSeparator();
        assertThat(fl.toString()).isEqualTo("a" + sep + "b");
    }
}
