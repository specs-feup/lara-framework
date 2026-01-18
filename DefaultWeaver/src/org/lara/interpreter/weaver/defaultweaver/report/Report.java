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
 * specific language governing permissions and limitations under the License.
 */
package org.lara.interpreter.weaver.defaultweaver.report;

/*
 * Universidade do Porto, Faculdade de Engenharia Departamento de Engenharia Informática Porto, Portugal
 * 
 * Author: João M. P. Cardoso Email: jmpc@fe.up.pt
 * 
 * Date: July 2012 Version 0.1
 */

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import pt.up.fe.specs.util.SpecsLogs;

public class Report {

    // for now we only have one type of attribute: function
    public Vector<String> AttributeValues = null;

    /*
     * The regular expressions of the joinpoints
     */
    public Map<String, String> Attributes = new HashMap<String, String>() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            // look for functions
            put("function", "\\w+\\s+(\\D\\w+)\\([^\\)\\;]*\\)[\\s\t\r\n]*\\{");
        }
    };

    /*
     * The goupid in the regular expressions of the joinpoints.
     */
    public Map<String, Integer> AttributesGroupID = new HashMap<String, Integer>() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        {
            put("function", Integer.valueOf(1));
        }
    };

    public Report(HashMap<String, String> Attributes) {
        this.Attributes = Attributes;
    }

    public Report() {
    }

    public Vector<String> getRegexGroup(String contents, String regex, int capturingGroupIndex) {
        // ResultsKey[] keys = ResultsKey.values();
        // for (int i = 0; i < strings.length; i++) {
        // for (int j = 0; j < regexes.size(); j++) {
        // Pattern regex = regexes.get(j);
        // int backReferenceIndex = keyIndex.get(j);

        final Vector<String> matches = new Vector<>();

        try {
            final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);

            final Matcher regexMatcher = pattern.matcher(contents);

            while (regexMatcher.find()) {
                // tester = regexMatcher.group(1);
                final String m = regexMatcher.group(capturingGroupIndex);
                if (m != null) {
                    matches.add(m);
                }
            }
        } catch (final PatternSyntaxException ex) {
            // Syntax error in the regular expression
            System.err.println(ex.getMessage());
            // LoggingUtils.getLogger().warning(ex.getMessage());
        }

        return matches;
    }

    /**
     * Read whole file to a String. This should be improved. Possibly using Scanner.
     * 
     * @param filePath
     * @return
     * @throws java.io.IOException
     */
    private static String readFileAsString(String filePath) throws java.io.IOException {
        final byte[] buffer = new byte[(int) new File(filePath).length()];
        try (final FileInputStream f = new FileInputStream(filePath);) {
            f.read(buffer);
        }
        return new String(buffer);
    }

    /**
     * Extract attribute values from file.
     * 
     * @param filename
     */
    public final void extract(String path, String filename) {
        this.extract(path + System.getProperty("file.separator") + filename);
    }

    public final void extract(String filename) {
        AttributeValues = new Vector<>();

        try {
            final String fileContent = Report.readFileAsString(filename);

            for (final Map.Entry<String, String> entry : Attributes.entrySet()) {
                Vector<String> matches = new Vector<>();
                // System.out.println("key: " + entry.getKey() +
                // " name: "
                // + entry.getValue());
                matches = getRegexGroup(fileContent, entry.getValue(),
                        AttributesGroupID.get(entry.getKey()).intValue());
                // System.out.println("key: " + entry.getKey()
                // + " result: " + result);
                if (!matches.isEmpty()) {
                    final Iterator<String> myIter = matches.iterator();
                    while (myIter.hasNext()) {
                        AttributeValues.addElement(myIter.next());
                    }
                }
            }

        } catch (final Exception e) {// Catch exception if any
            SpecsLogs.warn("Error: " + e.getMessage(), e);
            // Throwing a RuntimeException so that we can detect if there was
            // any problem
            // It should be caught at a higher level
            // throw new RuntimeException("Error2: " + e.getMessage(), e);
            // System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Print the attributes and their values.
     */
    public final void report() {
        final Iterator<String> it = AttributeValues.iterator();

        while (it.hasNext()) {
            System.out.println("attribute: function value: " + it.next());
        }
    }

    /**
     * This method translates the attributes and their values to a JavaScript representation consistent to the their
     * representation in the JavaScript interpreter.
     * 
     * @return
     */
    public final StringBuffer getLARA() {
        final StringBuffer report = new StringBuffer();

        final String str = "Attributes.design[" + "function" + "].";

        final Iterator<String> it = AttributeValues.iterator();

        while (it.hasNext()) {
            report.append(str);
            report.append("name");
            report.append("=");
            report.append(it.next());
            report.append(";\n");
        }
        // System.out.println(report);

        return report;
    }

    /**
     * This method translates the attributes and their values to a JavaScript representation consistent to the their
     * representation in the JavaScript interpreter.
     * 
     * @return
     */
    public final StringBuffer getReportStr() {
        final StringBuffer report = new StringBuffer();

        report.append("attributes.set({\n");
        report.append("\tfunction:{\n");

        final Iterator<String> it = AttributeValues.iterator();

        while (it.hasNext()) {
            report.append("\t\t\t");
            report.append(it.next());
            // report.append(":");
            // report.append(entry.getValue());
            report.append(",\n");
        }
        report.append("\t}");
        report.append("\n});");
        // System.out.println(report);

        return report;
    }

    /**
     * Get the files in a folder identified by a given path.
     * 
     * @param path
     * @return
     */
    public static ArrayList<String> getFiles(String path) {
        final ArrayList<String> files = new ArrayList<>();
        for (final File f : new File(path).listFiles()) {
            files.add(f.getName());
            // System.out.println(f.getName());
        }
        return files;
    }

    /**
     * get all files and functions in files and return the results as a String representing the info as JavaScript code
     * as:
     * 
     * When selecting only files:
     * 
     * files = [ { name: "file1.c"}, { name: "file2.c"}, { name: "main.c"}, ]
     * 
     * When selecting files.functions:
     * 
     * files = [ { name: "file1.c", functions: [ {name: "f1"},{name: "max"}, {name: "min"}, ] }, { name: "file2.c",
     * functions: [ {name: "f2"}, {name: "xpto"}, ] }, { name: "main.c", functions: [ {name: "main"} ] } ]
     * 
     */
    public StringBuffer getFilesAndFunctions(String path, String files, String functions) {

        // get the files in the folder:
        final ArrayList<String> ListFiles = Report.getFiles(path);

        final StringBuffer joinpoint = new StringBuffer();
        joinpoint.append("\n{file : [\n");

        // int numFiles = 0;

        final Iterator<String> itr = ListFiles.iterator();
        while (itr.hasNext()) {
            final String element = itr.next();

            if (element.matches(files)) {
                // System.out.print("file: "+element + " ");
                // numFiles++;
                joinpoint.append("\t{ name: \"");
                joinpoint.append(element);
                joinpoint.append("\"");
                joinpoint.append(", filename: \"" + path + System.getProperty("file.separator") + element + "\"");
                joinpoint.append(", absolutePath: \""
                        + (new File(path + System.getProperty("file.separator") + element)).getAbsolutePath() + "\"");

                int numFunctions = 0;
                this.extract(path + System.getProperty("file.separator") + element);

                final Iterator<String> itr2 = AttributeValues.iterator();
                while (itr2.hasNext()) {
                    final String element2 = itr2.next();

                    // System.out.print("function: "+element2 + " ");
                    if (element2.matches(functions)) {
                        numFunctions++;
                        if (numFunctions == 1) {
                            joinpoint.append(",\n\t\tfunction: [\n");
                        }
                        joinpoint.append("\t\t\t{ name: \"");
                        joinpoint.append(element2);
                        joinpoint.append("\"");
                        joinpoint.append(
                                ", filename: \"" + path + System.getProperty("file.separator") + element2 + "\"");
                        joinpoint.append(", absolutePath: \""
                                + (new File(path + System.getProperty("file.separator") + element2)).getAbsolutePath()
                                + "\"");
                        joinpoint.append("},\n");
                    }
                }
                if (numFunctions > 0) {
                    joinpoint.append("\t\t]\n\t},\n");
                } else {
                    joinpoint.append("},\n");
                }
            }
        }

        // if (numFiles > 0) {
        // joinpoint.append("\t}\n");
        // }

        joinpoint.append("]\n}");
        return joinpoint;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        // Report myReport = new Report();
        // myReport.extract(args[0]);

        System.out.println(
                new Report().getFilesAndFunctions(".\\org\\reflect\\weaving\\examples\\cfiles", ".*\\.c", ".*"));

        System.out
                .println(new Report().getFilesAndFunctions(".\\org\\reflect\\weaving\\examples\\cfiles", ".*\\.c", ""));

        System.out.println(
                new Report().getFilesAndFunctions(".\\org\\reflect\\weaving\\examples\\cfiles", ".*\\.c", "main"));

    }

}
