/*
 * Copyright 2013 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */
package larac.utils.output;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;

import pt.up.fe.specs.compress.ZipFormat;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.logging.MultiOutputStream;

public class Output {

    private Message msg = new NormalMsg();
    private Message warning = new WarningMsg();
    private final MessageDef def;
    private PrintStream stream;

    public Output() {
        this.def = new MessageDef(3);
        setStream(System.out);
    }

    public Output(PrintStream stream) {
        this.def = new MessageDef(3);
        setStream(stream);
    }

    public Output(PrintStream stream, int level) {
        this.def = new MessageDef(level);
        setStream(stream);
    }

    public Output(int level) {
        this.def = new MessageDef(level);
        setStream(System.out);
    }

    public void warn(String message) {
        if (this.def.warning) {
            this.warning.print(message);
        }
    }

    public void warnln(String message) {
        if (this.def.warning) {
            this.warning.println(message);
        }
    }

    public void println(String message) {
        if (this.def.normal) {
            this.msg.println(message);
        }
    }


    public void println(Object obj) {
        if (this.def.normal) {
            this.msg.println(obj.toString());
        }
    }

    public void setStream(PrintStream stream) {
        this.stream = stream;
        this.msg = new NormalMsg(stream);
        this.warning = new WarningMsg(stream);
    }

    public void addFileStream(File outFile) {

        PrintStream fileStream = buildFileStream(outFile);
        OutputStream multiStream = new MultiOutputStream(Arrays.asList(this.stream, fileStream));

        this.stream = new PrintStream(multiStream);
        this.msg = new NormalMsg(this.stream);
        this.warning = new WarningMsg(this.stream);
    }

    private PrintStream buildFileStream(File outFile) {
        Optional<ZipFormat> zipFormat = ZipFormat.fromExtension(SpecsIo.getExtension(outFile));

        // Compressed log file
        if (zipFormat.isPresent()) {
            // Name of the entry
            String logFilename = SpecsIo.removeExtension(outFile) + ".txt";

            // Streams must stay open after returning
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Could not use file '" + outFile + "' for zipped output: ", e);
            }

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            OutputStream zipStream = zipFormat.get().newFileCompressor(logFilename, bufferedOutputStream);
            PrintStream zipPrintStream = new PrintStream(zipStream);

            return zipPrintStream;

        }

        // Normal log file
        try {
            return new PrintStream(outFile);
        } catch (FileNotFoundException e) {

            throw new RuntimeException("Could not create output file: ", e);
        }

    }

    public void setLevel(int level) {
        this.def.setDef(level);
    }

    public void close() {

        // Don't close the stream if it is System.out or System.err, otherwise
        // no further output is possible
        if (this.stream != null && this.stream != System.out && this.stream != System.err) {
            this.stream.close();
        }
    }

    public PrintStream getOutStream() {
        return this.stream;
    }
}
