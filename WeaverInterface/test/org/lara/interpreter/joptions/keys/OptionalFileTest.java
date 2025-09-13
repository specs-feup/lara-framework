package org.lara.interpreter.joptions.keys;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.up.fe.specs.util.parsing.StringCodec;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OptionalFile} - newInstance null/empty/non-empty; encode, getCodec round-trip, toString marks usage.
 */
class OptionalFileTest {

    @TempDir
    File tempDir;

    @Test
    void testNewInstanceNull() {
        OptionalFile optFile = OptionalFile.newInstance(null);
        
        assertThat(optFile.isUsed()).isFalse();
        assertThat(optFile.getFile()).isEqualTo(new File(""));
    }

    @Test
    void testNewInstanceEmpty() {
        OptionalFile optFile = OptionalFile.newInstance("");
        
        assertThat(optFile.isUsed()).isFalse();
        assertThat(optFile.getFile()).isEqualTo(new File(""));
    }

    @Test
    void testNewInstanceNonEmpty() {
        String fileName = "test.txt";
        OptionalFile optFile = OptionalFile.newInstance(fileName);
        
        assertThat(optFile.isUsed()).isTrue();
        assertThat(optFile.getFile()).isEqualTo(new File(fileName));
    }

    @Test
    void testConstructorWithFileAndUsed() {
        File file = new File(tempDir, "test.txt");
        
        OptionalFile optFileUsed = new OptionalFile(file, true);
        assertThat(optFileUsed.isUsed()).isTrue();
        assertThat(optFileUsed.getFile()).isEqualTo(file);
        
        OptionalFile optFileNotUsed = new OptionalFile(file, false);
        assertThat(optFileNotUsed.isUsed()).isFalse();
        assertThat(optFileNotUsed.getFile()).isEqualTo(file);
    }

    @Test
    void testSetUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile optFile = new OptionalFile(file, false);
        
        assertThat(optFile.isUsed()).isFalse();
        
        optFile.setUsed(true);
        assertThat(optFile.isUsed()).isTrue();
        
        optFile.setUsed(false);
        assertThat(optFile.isUsed()).isFalse();
    }

    @Test
    void testSetFile() {
        File file1 = new File(tempDir, "test1.txt");
        File file2 = new File(tempDir, "test2.txt");
        OptionalFile optFile = new OptionalFile(file1, true);
        
        assertThat(optFile.getFile()).isEqualTo(file1);
        
        optFile.setFile(file2);
        assertThat(optFile.getFile()).isEqualTo(file2);
    }

    @Test
    void testToStringUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile optFile = new OptionalFile(file, true);
        
        String result = optFile.toString();
        assertThat(result).contains("[X]"); // Marks usage with X
        assertThat(result).contains(file.toString());
    }

    @Test
    void testToStringNotUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile optFile = new OptionalFile(file, false);
        
        String result = optFile.toString();
        assertThat(result).contains("[ ]"); // Marks non-usage with space
        assertThat(result).contains(file.toString());
    }

    @Test
    void testEncodeUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile optFile = new OptionalFile(file, true);
        
        String encoded = OptionalFile.encode(optFile);
        assertThat(encoded).isEqualTo(file.toString());
    }

    @Test
    void testEncodeNotUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile optFile = new OptionalFile(file, false);
        
        String encoded = OptionalFile.encode(optFile);
        assertThat(encoded).isEmpty();
    }

    @Test
    void testCodecRoundTripUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile original = new OptionalFile(file, true);
        
        StringCodec<OptionalFile> codec = OptionalFile.getCodec();
        
        // Encode
        String encoded = codec.encode(original);
        assertThat(encoded).isEqualTo(file.toString());
        
        // Decode
        OptionalFile decoded = codec.decode(encoded);
        assertThat(decoded.isUsed()).isTrue();
        assertThat(decoded.getFile()).isEqualTo(file);
    }

    @Test
    void testCodecRoundTripNotUsed() {
        File file = new File(tempDir, "test.txt");
        OptionalFile original = new OptionalFile(file, false);
        
        StringCodec<OptionalFile> codec = OptionalFile.getCodec();
        
        // Encode (should return empty string)
        String encoded = codec.encode(original);
        assertThat(encoded).isEmpty();
        
        // Decode empty string
        OptionalFile decoded = codec.decode("");
        assertThat(decoded.isUsed()).isFalse();
        assertThat(decoded.getFile()).isEqualTo(new File(""));
    }

    @Test
    void testCodecRoundTripNull() {
        StringCodec<OptionalFile> codec = OptionalFile.getCodec();
        
        OptionalFile decoded = codec.decode(null);
        assertThat(decoded.isUsed()).isFalse();
        assertThat(decoded.getFile()).isEqualTo(new File(""));
    }

    @Test
    void testCodecConsistencyWithNewInstance() {
        StringCodec<OptionalFile> codec = OptionalFile.getCodec();
        
        // Test consistency with newInstance method
        String fileName = "test.txt";
        OptionalFile fromNewInstance = OptionalFile.newInstance(fileName);
        OptionalFile fromCodec = codec.decode(fileName);
        
        assertThat(fromCodec.isUsed()).isEqualTo(fromNewInstance.isUsed());
        assertThat(fromCodec.getFile()).isEqualTo(fromNewInstance.getFile());
    }
}