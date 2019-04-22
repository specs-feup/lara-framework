/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.lara.loc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lara.interpreter.weaver.interf.WeaverEngine;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsStrings;
import pt.up.fe.specs.util.csv.CsvWriter;
import pt.up.fe.specs.util.utilities.ProgressCounter;

public class LaraLoc {

    private static final String VERSION = "2.0";
    private static final String TOTALS_KEY = "%totals%";

    public static String getTotalsKey() {
        return TOTALS_KEY;
    }

    // private static final Pattern REGEX_ASPECTDEF = Pattern.compile("(\\baspectdef\\b)");

    // // Just for tests
    // public static void main(String[] args) {
    // SpecsSystem.programStandardInit();
    // }

    private final WeaverEngine weaverEngine;

    public LaraLoc(WeaverEngine weaverEngine) {
        this.weaverEngine = weaverEngine;
    }

    public void execute(List<String> args) {
        SpecsLogs.info("LARA Lines-of-Code Counter " + VERSION + " for " + weaverEngine.getName() + "\n");

        // Parse first arg as a path
        if (args.size() < 1) {
            SpecsLogs.msgInfo(
                    "Needs at least one path as parameter. Can be a single LARA file, or a folder with .lara files inside");
            return;
        }

        List<File> existingPaths = new ArrayList<>();
        for (String arg : args) {
            File path = new File(arg);
            if (!path.exists()) {
                SpecsLogs.info("Ignoring path '" + arg + "', it does not exist");
                continue;
            }

            existingPaths.add(path);
        }

        long startCollectFiles = System.nanoTime();
        List<File> laraFiles = new ArrayList<>();
        for (File existingPath : existingPaths) {
            if (existingPath.isDirectory()) {
                laraFiles.addAll(SpecsIo.getFilesRecursive(existingPath, "lara"));
            } else {
                laraFiles.add(existingPath);
            }
        }

        // File srcFolder = new File("C:\\Users\\JoaoBispo\\Desktop\\shared\\specs-lara\\ANTAREX");
        // List<File> laraFiles = IoUtils.getFilesRecursive(srcFolder, "lara");

        long tic = System.nanoTime();
        // Create CSV for stats
        // Header: filename | LoC | Aspects | Comment Lines
        CsvWriter writer = new CsvWriter("File", "LoC", "Aspects", "Comments", "Functions");

        ProgressCounter progress = new ProgressCounter(laraFiles.size());
        LaraStats totals = new LaraStats(weaverEngine.getLanguageSpecification());
        // long totalLoc = 0;
        // long totalAspects = 0;
        // long totalComments = 0;
        //
        // File textFolder = saveText ? SpecsIo.mkdir(srcFolder, "laraloc-text") : null;

        File srcFolder = SpecsIo.getWorkingDir();

        for (File laraFile : laraFiles) {
            SpecsLogs.msgInfo("Processing '" + laraFile + "' " + progress.next());

            LaraStats fileStats = new LaraStats(weaverEngine.getLanguageSpecification());
            fileStats.addFileStats(laraFile);

            // LaraLoc laraLoc = new LaraLoc(saveText);
            // laraLoc.processLaraFile(laraFile);

            // Save line
            String filename = SpecsIo.getRelativePath(laraFile, srcFolder);

            // writer.addLine(filename, laraLoc.getLoc(), laraLoc.getNumAspects(), laraLoc.getCommentLines());
            writer.addLine(filename, fileStats.get(LaraStats.LARA_STMTS), fileStats.get(LaraStats.ASPECTS),
                    fileStats.get(LaraStats.COMMENTS), fileStats.get(LaraStats.FUNCTIONS));

            // Update totals
            totals.addFileStats(fileStats);

            // totalLoc += laraLoc.getLoc();
            // totalAspects += laraLoc.getNumAspects();
            // totalComments += laraLoc.getCommentLines();

            // if (saveText) {
            // String baseFolder = SpecsIo.getRelativePath(laraFile.getParentFile(), srcFolder);
            // File cleanTxtFolder = new File(textFolder, baseFolder);
            // SpecsIo.write(new File(cleanTxtFolder, laraFile.getName() + ".clean"), laraLoc.getText());
            // }
        }

        // writer.addLine("Total", totalLoc, totalAspects, totalComments);
        writer.addLine("Total", totals.get(LaraStats.LARA_STMTS), totals.get(LaraStats.ASPECTS),
                totals.get(LaraStats.COMMENTS), totals.get(LaraStats.FUNCTIONS));

        // File outputFile = new File(srcFolder, "lara-loc.csv");
        File outputFile = new File(SpecsIo.getWorkingDir(), "lara-loc.csv");
        SpecsIo.write(outputFile, writer.buildCsv());

        SpecsLogs.msgInfo("Written '" + outputFile.getAbsolutePath() + "'");
        SpecsLogs.msgInfo(SpecsStrings.takeTime("Collecting files time", startCollectFiles));
        SpecsLogs.msgInfo(SpecsStrings.takeTime("Processing time", tic));
    }

    public Map<String, LaraStats> getStats(List<File> laraFiles) {

        Map<String, LaraStats> stats = new HashMap<>();

        ProgressCounter progress = new ProgressCounter(laraFiles.size());
        LaraStats totals = new LaraStats(weaverEngine.getLanguageSpecification());
        stats.put(TOTALS_KEY, totals);

        File srcFolder = SpecsIo.getWorkingDir();

        for (File laraFile : laraFiles) {
            SpecsLogs.msgInfo("LaraLoc: processing '" + laraFile + "' " + progress.next());

            LaraStats fileStats = new LaraStats(weaverEngine.getLanguageSpecification());
            fileStats.addFileStats(laraFile);

            // Save line
            String filename = SpecsIo.getRelativePath(laraFile, srcFolder);

            stats.put(filename, fileStats);

            // Update totals
            totals.addFileStats(fileStats);
        }

        return stats;
    }

}
