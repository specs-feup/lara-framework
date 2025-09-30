package org.lara.interpreter.joptions.keys;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OptionalFileTest {

    @Test
    @DisplayName("newInstance handles null/empty/non-empty correctly")
    void newInstanceVariants() {
        OptionalFile n1 = OptionalFile.newInstance(null);
        assertThat(n1.isUsed()).isFalse();
        assertThat(n1.getFile()).isNotNull();
        assertThat(n1.getFile().getPath()).isEmpty();

        OptionalFile n2 = OptionalFile.newInstance("");
        assertThat(n2.isUsed()).isFalse();
        assertThat(n2.getFile().getPath()).isEmpty();

        OptionalFile n3 = OptionalFile.newInstance("foo.txt");
        assertThat(n3.isUsed()).isTrue();
        assertThat(n3.getFile()).isEqualTo(new File("foo.txt"));
    }

    @Test
    @DisplayName("encode and codec round-trip")
    void encodeCodecRoundTrip() {
        OptionalFile used = OptionalFile.newInstance("bar.txt");
        String enc = OptionalFile.encode(used);
        OptionalFile dec = OptionalFile.getCodec().decode(enc);
        assertThat(dec.isUsed()).isTrue();
        assertThat(dec.getFile()).isEqualTo(new File("bar.txt"));

        OptionalFile notUsed = OptionalFile.newInstance("");
        String enc2 = OptionalFile.encode(notUsed);
        OptionalFile dec2 = OptionalFile.getCodec().decode(enc2);
        assertThat(dec2.isUsed()).isFalse();
        assertThat(dec2.getFile().getPath()).isEmpty();
    }

    @Test
    @DisplayName("toString marks usage with X or space")
    void toStringMarksUsage() {
        assertThat(OptionalFile.newInstance("a").toString()).startsWith("[X] ");
        assertThat(OptionalFile.newInstance("").toString()).startsWith("[ ] ");
    }
}
