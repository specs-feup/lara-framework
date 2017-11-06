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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import larac.exceptions.LARACompilerException;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.logging.MultiOutputStream;

public class Output {

    private Message msg = new NormalMsg();
    private Message error = new ErrorMsg();
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

    public void error(String message) {
        if (this.def.error) {
            this.error.print(message);
        }
    }

    public void errorln(String message) {
        if (this.def.error) {
            this.error.println(message);
        }
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

    public void print(String message) {
        if (this.def.normal) {
            this.msg.print(message);
        }
    }

    public void println(String message) {
        if (this.def.normal) {
            this.msg.println(message);
        }
    }

    public void error(Object obj) {
        if (this.def.error) {
            this.error.print(obj.toString());
        }
    }

    public void errorln(Object obj) {
        if (this.def.error) {
            this.error.println(obj.toString());
        }
    }

    public void warn(Object obj) {
        if (this.def.warning) {
            this.warning.print(obj.toString());
        }
    }

    public void warnln(Object obj) {
        if (this.def.warning) {
            this.warning.println(obj.toString());
        }
    }

    public void print(Object obj) {
        if (this.def.normal) {
            this.msg.print(obj.toString());
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
        this.error = new ErrorMsg(stream);
        this.warning = new WarningMsg(stream);
    }

    public void addFileStream(File outFile) {

        PrintStream fileStream = buildFileStream(outFile);
        OutputStream multiStream = new MultiOutputStream(Arrays.asList(this.stream, fileStream));

        this.stream = new PrintStream(multiStream);
        this.msg = new NormalMsg(this.stream);
        this.error = new ErrorMsg(this.stream);
        this.warning = new WarningMsg(this.stream);
    }

    private PrintStream buildFileStream(File outFile) {
        // Check extension, use ZipStream is ends with .zip
        boolean isZip = SpecsIo.getExtension(outFile).equals("zip");

        if (isZip) {
            // Name of the entry
            String logFilename = SpecsIo.removeExtension(outFile) + ".txt";

            // Streams must stay open after returning
            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            ZipOutputStream out = null;
            PrintStream zipPrintStream = null;
            try {
                fileOutputStream = new FileOutputStream(outFile);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                out = new ZipOutputStream(bufferedOutputStream);
                zipPrintStream = new PrintStream(out);

                // Create zip entry
                out.putNextEntry(new ZipEntry(logFilename));
                // System.out.println("ZIP STREAM to " + outFile);
                return zipPrintStream;

            } catch (IOException e) {
                // Close streams. At this point we do not know which ones are open
                // and which were not initialized, try to close all
                closeStreamAfterError(zipPrintStream);
                closeStreamAfterError(out);
                closeStreamAfterError(bufferedOutputStream);
                closeStreamAfterError(fileOutputStream);

                throw new LARACompilerException("Could not create zipped output file: ", e);
            }
        }

        // Normal log file
        try {
            return new PrintStream(outFile);
        } catch (FileNotFoundException e) {

            throw new LARACompilerException("Could not create output file: ", e);
        }

    }

    private void closeStreamAfterError(OutputStream stream) {
        // Do nothing if no stream
        if (stream == null) {
            return;
        }

        // Close the stream
        try {
            stream.close();
        } catch (IOException e) {
            SpecsLogs.msgWarn("Exception while closing a stream", e);
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
