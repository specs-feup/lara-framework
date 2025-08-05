package org.lara.interpreter.weaver.utils;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * From a list of input paths, creates a mapping between source files and the original source paths, ba
 */
public class SourcesGatherer {

    /**
     * Maps found source files to the original given source (e.g., input folder)
     */
    private final Map<File, File> sourceFiles;

    private final Collection<String> extensions;

    private SourcesGatherer(Collection<String> extensions) {
        this.sourceFiles = new LinkedHashMap<>();
        this.extensions = extensions;
    }

    public static SourcesGatherer build(List<File> sources, Collection<String> extensions) {
        var sourcesGatherer = new SourcesGatherer(extensions);
        sourcesGatherer.build(sources);
        return sourcesGatherer;
    }

    public Map<File, File> getSourceFiles() {
        return sourceFiles;
    }

    private void build(List<File> sources) {

        for (var source : sources) {
            addSource(source, source);
        }
    }

    private void addSource(File source, File originalSource) {
        //System.out.println("SOURCE: " + source);
        // Base case
        if (source.isFile()) {
            var extension = SpecsIo.getExtension(source);

            if (!extensions.contains(extension)) {
                // Ignoring non-supported extension
                //System.out.println("IGNORING");
                return;
            }
            //System.out.println("ADDING");
            sourceFiles.put(source, originalSource);
            return;
        }

        if (source.isDirectory()) {
            for (var childSource : SpecsIo.getFilesRecursive(source, extensions)) {
                addSource(childSource, originalSource);
            }

            return;
        }

        SpecsLogs.info("Ignoring source: " + source);
    }
}
