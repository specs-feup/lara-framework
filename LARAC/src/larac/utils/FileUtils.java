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
package larac.utils;

import java.awt.Panel;
import java.awt.TextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import larac.utils.output.NormalMsg;
import larac.utils.output.Output;
import pt.up.fe.specs.util.SpecsIo;

public class FileUtils {
    public enum Commands {
        ASPECT,
        LANGUAGE,
        RESOURCE,
        XML,
        OUTPUT,
        WORKSPACE,
        LOAD,
        SAVE,
        SAVEAS,
        CLEAR,
        GENERATE,
        OPEN
    }

    public static FileFilter aspectFilter = new FileFilter() {
        String extension = ".lara";

        @Override
        public String getDescription() {
            return "Aspect File (" + extension + ")";
        }

        @Override
        public boolean accept(File arg0) {
            if (arg0.isDirectory()) {
                return true;
            }
            if (arg0.getName().contains(extension)) {
                return true;
            }

            return false;
        }
    };
    public static FileFilter txtFilter = new FileFilter() {
        String extension = ".txt";

        @Override
        public String getDescription() {
            return "Resource File (" + extension + ")";
        }

        @Override
        public boolean accept(File arg0) {
            if (arg0.isDirectory()) {
                return true;
            }
            if (arg0.getName().contains(extension)) {
                return true;
            }

            return false;
        }
    };
    public static FileFilter dirFilter = new FileFilter() {
        @Override
        public String getDescription() {
            return "Directory";
        }

        @Override
        public boolean accept(File arg0) {
            if (arg0.isDirectory()) {
                return true;
            }
            return false;
        }
    };
    public static FileFilter dirXMLFilter = new FileFilter() {
        @Override
        public String getDescription() {
            return "XMLs Directory";
        }

        @Override
        public boolean accept(File arg0) {
            if (arg0.isDirectory()) {
                return true;
            }
            return false;
        }
    };

    /**
     * 
     * @param app
     * @param fc
     * @param ff
     * @param txtField
     * @param type
     */
    public static void processJFileChooser(Panel app, JFileChooser fc, FileFilter ff, TextField txtField, int type) {
        fc.setFileSelectionMode(type);
        fc.setFileFilter(ff);
        final int returnValue = fc.showDialog(app, "Open");
        switch (returnValue) {
        case JFileChooser.APPROVE_OPTION:
            txtField.setText(fc.getSelectedFile().getPath());
            break;
        default:
            break;
        }
    }

    public static String processSaveFileChooser(Panel app, JFileChooser fc, FileFilter ff, String title, int type) {
        fc.setFileSelectionMode(type);
        fc.setFileFilter(ff);

        final int returnValue = fc.showSaveDialog(app);
        switch (returnValue) {
        case JFileChooser.APPROVE_OPTION:
            return fc.getSelectedFile().getPath();
        default:
            break;
        }
        return "";
    }

    public static File processOpenFileChooser(Panel app, JFileChooser fc, FileFilter ff, String title, int type) {
        fc.setFileSelectionMode(type);
        fc.setFileFilter(ff);

        final int returnValue = fc.showOpenDialog(app);
        switch (returnValue) {
        case JFileChooser.APPROVE_OPTION:
            return fc.getSelectedFile();
        default:
            break;
        }
        return null;
    }

    public static void toFile(String filePath, String extension, String text, File outputDir) {
        final File outputFile = new File(outputDir, filePath + extension);
        System.out.println("Writing to file: " + outputFile.getAbsolutePath());
        SpecsIo.write(outputFile, text);
    }

    public static void toFile(Output printStream, String filePath, String extension, String text, File outputDir) {
        final File outputFile = new File(outputDir, filePath + extension);
        printStream.println("Writing to file: " + outputFile.getAbsolutePath());
        SpecsIo.write(outputFile, text);
    }

    public static String fromFile(File f) {
        new NormalMsg().println("Opening file: " + f.getPath());
        return SpecsIo.read(f);

    }

    public static boolean getExamplesFromDir(String dirName, HashMap<String, String> files) {
        final File dir = new File(dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            new NormalMsg().println("Directory not found: " + dirName);
            return false;
        }
        final String path = dir.getPath() + "\\";
        for (final String fileName : dir.list()) {
            final File f = new File(path + fileName);
            if (f.isDirectory()) {
                getExamplesFromDir(path + fileName, files);
            } else if (f.getPath().endsWith(".lara") || f.getPath().endsWith(".js")) {
                String key = f.getPath();
                if (key.contains(File.separator)) {
                    key = key.substring(key.lastIndexOf(File.separator) + 1);
                }
                try (FileReader fr = new FileReader(f.getPath()); BufferedReader br = new BufferedReader(fr);) {

                    String line;
                    String aspectCode = "";
                    while ((line = br.readLine()) != null) {
                        aspectCode += line + "\n";
                    }
                    files.put(key, aspectCode);
                } catch (final IOException e) {
                    System.err.println(e.getLocalizedMessage() + "\n");
                    return false;
                }

            }
        }
        return true;
    }

    public static boolean removeDirectory(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return false;
        }
        final String[] list = directory.list();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                final File entry = new File(directory, list[i]);
                if (entry.isDirectory() && !removeDirectory(entry)) {
                    return false;
                } else if (!entry.delete()) {
                    return false;
                }
            }
        }
        return directory.delete();
    }
}
