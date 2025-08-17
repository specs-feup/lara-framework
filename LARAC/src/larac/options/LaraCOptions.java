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
package larac.options;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.lara.interpreter.weaver.utils.GenericLaraResourceProvider;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;

import larac.LaraC;
import larac.exceptions.LARACompilerException;
import larac.imports.FileLaraImport;
import larac.imports.LaraImport;
import larac.imports.ResourceLaraImport;
import larac.options.optionprovider.OptionUtils;
import larac.utils.output.MessageConstants;
import larac.utils.output.Output;
import pt.up.fe.specs.util.SpecsFactory;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.JarPath;

public class LaraCOptions {
    public static final String CONFIG_FILE_NAME = "larac.properties";

    private static final String SKIP_ARGS = "#lara_c_options_skip";

    // Display the Aspect-IR
    private boolean showAspectIR;
    // Display all available information in the console
    private boolean debug;
    // Target Language
    private String language;
    // Specification Directory
    private File xmlSpecDir;
    // Output directory for the created files
    private File outputDir;
    // include aspects in the given directories (separated by the file separator)
    private List<File> includeFolders;
    private List<LaraResourceProvider> includeResources;
    private Lazy<MultiMap<String, LaraResourceProvider>> includeResourcesMap;
    private boolean documentationMode;

    private Map<String, List<LaraImport>> importsCache;

    private Options options;
    private CommandLine command;

    public LaraCOptions() {
        setDefaultOptions();
    }

    public static String getSkipArgs() {
        return SKIP_ARGS;
    }

    /**
     * Define the default values for LaraC options
     */
    private void setDefaultOptions() {
        showAspectIR = debug = false;
        language = MessageConstants.LANGUAGE_DEFAULT;
        xmlSpecDir = outputDir = new File(MessageConstants.HOME_DIR);
        includeFolders = new ArrayList<>();
        includeResources = new ArrayList<>();
        includeResourcesMap = Lazy.newInstance(() -> buildIncludeResourcesMap());
        importsCache = new HashMap<>();

        // Add working dir to the included paths
        final File workingDir = SpecsIo.getWorkingDir();
        includeFolders.add(workingDir);
        documentationMode = false;
    }

    /**
     * Set {@link LaraCOptions} instance with the given input arguments
     * 
     * @param args
     * @return
     */
    public boolean setOptions(LaraC larac, String args[]) {
        // SpecsLogs.debug(() -> "LARAC args: " + Arrays.asList(args));
        options = OptionUtils.optionsBuilder(LaraOptionProvider.class);
        // LaraC requires at least one input: the aspect file
        if ((args.length < 1)) {

            OptionUtils.help(MessageConstants.LARAC_HELP_EXEC, options);
            return false;
        }

        String laraFileName = args[0];

        if (laraFileName.equals(SKIP_ARGS)) {
            return true;
        }

        setLaraFile(larac, laraFileName);

        command = OptionUtils.parseOptions(options, args, MessageConstants.LARAC_HELP_EXEC);
        if (command == null) {
            return false;
        }

        final Option[] processedOptions = command.getOptions();
        for (final Option option : processedOptions) {

            setOption(option, larac);
        }

        larac.setJarPath(new JarPath(LaraC.class, LaraC.getPropertyJarPath()).buildJarPath());
        getIncludeFolders().add(larac.getLaraFile().getParentFile());
        return true;
    }

    /**
     * @param args
     */
    public void setLaraFile(LaraC larac, String laraFileName) {
        final File laraFile = new File(laraFileName);

        setLaraFile(larac, laraFile);
    }

    /**
     * @param args
     */
    public void setLaraFile(LaraC larac, File laraFile) {

        if (!laraFile.exists()) {
            throw new LARACompilerException("when loading file",
                    new FileNotFoundException("The file does not exist: " + laraFile.getPath()));
        }

        final File absoluteLaraFile = SpecsIo.getCanonicalFile(laraFile);

        // try {
        // laraFile = laraFile.getCanonicalFile();
        // } catch (final IOException e) {
        // throw new LARACompilerException("when processing file", e);
        // }
        larac.setLaraName(absoluteLaraFile.getName());
        // larac.setLaraPath(absoluteLaraFile.getName());
        larac.setLaraPath(SpecsIo.getCanonicalPath(absoluteLaraFile));

        larac.setLaraStreamProvider(() -> {
            try {
                return new FileInputStream(absoluteLaraFile);
            } catch (Exception e) {
                throw new LARACompilerException(
                        "Could not create InputStream from file: " + absoluteLaraFile.getAbsolutePath(),
                        e);
            }
        });

        larac.setLaraFile(absoluteLaraFile);

    }

    public void setLaraResource(LaraC larac, ResourceProvider laraResource) {

        String resourcePath = laraResource.getResource();

        // laraFile = IoUtils.getCanonicalFile(laraFile);
        // try {
        // laraFile = laraFile.getCanonicalFile();
        // } catch (final IOException e) {
        // throw new LARACompilerException("when processing file", e);
        // }
        larac.setLaraName(laraResource.getResourceName());
        larac.setLaraPath(resourcePath);

        larac.setLaraStreamProvider(() -> {
            InputStream stream = LaraCOptions.class.getClassLoader().getResourceAsStream(resourcePath);
            if (stream == null) {
                throw new LARACompilerException("when loading resource",
                        new FileNotFoundException("Could not read resource file: " + resourcePath));
            }
            return stream;
        });
        // larac.setLaraFile(laraFile);
    }

    /**
     * Set an option on larac according to the value given, if the option exists on the enum {@link LaraOptionProvider}
     * 
     * @param optionName
     * @return
     */
    public boolean setOption(Option option, LaraC larac) {

        final String optionName = option.getLongOpt();
        final String value = option.getValue();

        LaraOptionProvider arg = OptionUtils.getOptionByName(LaraOptionProvider.class, optionName);
        if (arg == null) {
            arg = OptionUtils.getOptionByShortName(LaraOptionProvider.class, optionName);
            if (arg == null) {
                larac.warnln("Option " + optionName + " does not exist. Ignoring option.");
                return true;
            }
        }
        switch (arg) {
        case help:
            OptionUtils.help(MessageConstants.LARAC_HELP_EXEC, options);
            return false;
        case version:
            larac.println(MessageConstants.getLaraVersion() + "\n" + LaraC.getFrontEndVersion());
            return false;
        case debug:
            setDebug(true);
            break;
        case documentation:
            setDocumentationMode(true);
            break;
        case aspectir:
            showAspectIR = true;
            break;
        case language:
            setLanguage(value);
            break;
        case xmlspec:
            setXmlSpecDir(new File(value));
            break;
        case output:
            setOutputDir(new File(value));
            break;
        case verbose:
            try {

                final int verboseLevel = Integer.parseInt(value);
                if (verboseLevel < 0 || verboseLevel > 3) {
                    throw new NumberFormatException("Number out of bounds");
                }
                larac.getPrint().setLevel(verboseLevel);
            } catch (final NumberFormatException e) {

                larac.errorln("Wrong value for verbose level: " + value + ". Will use default level");
            }
            break;
        case stream:
            try (final PrintStream streamFile = new PrintStream(new File(value));) {

                larac.getPrint().setStream(streamFile);
            } catch (final FileNotFoundException e) {

                larac.errorln("Could not create stream file: " + value + ". Will use default output stream.");
            }
            break;
        case include:
            includeFolders.addAll(getIncludeFolders(value, larac));
            break;
        case resource:
            includeResources.addAll(getResourceProviders(value, larac));
            break;
        default:
            break;
        }
        return true;
    }
    //
    // @Override
    // public String toString() {
    // final StringBuilder toString = new StringBuilder("LARAC options:");
    // toString.append("\n\tTODO");
    // toString.append("\n");
    // return toString.toString();
    // }

    public Collection<LaraResourceProvider> getResourceProviders(String includeDir, LaraC larac) {
        final String[] paths = SpecsIo.splitPaths(includeDir);

        final Collection<LaraResourceProvider> importPaths = SpecsFactory.newArrayList();
        for (final String path : paths) {

            // importPaths.add(() -> path);
            importPaths.add(new GenericLaraResourceProvider(path));
        }
        return importPaths;
    }

    public List<File> getIncludeFolders(String includeDir, LaraC larac) {
        final String[] paths = SpecsIo.splitPaths(includeDir);

        final List<File> importPaths = SpecsFactory.newArrayList();
        for (final String path : paths) {

            final File includeFile = new File(path);
            if (!includeFile.isDirectory()) {
                larac.warnln("Tried to add folder '" + includeFile + "' to the includes, but the path was not found.");
            }

            importPaths.add(includeFile);
        }
        return importPaths;
    }

    /**
     * @return the showAspectIR
     */
    public boolean isShowAspectIR() {
        return showAspectIR;
    }

    /**
     * @param showAspectIR
     *            the showAspectIR to set
     */
    public void setShowAspectIR(boolean showAspectIR) {
        this.showAspectIR = showAspectIR;
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug
     *            the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug) {
            showAspectIR = true;
        }
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language
     *            the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the xmlSpecDir
     */
    public File getXmlSpecDir() {
        return xmlSpecDir;
    }

    /**
     * @param xmlSpecDir
     *            the xmlSpecDir to set
     */
    public void setXmlSpecDir(File xmlSpecDir) {
        this.xmlSpecDir = xmlSpecDir;
    }

    /**
     * @return the outputDir
     */
    public File getOutputDir() {
        return outputDir;
    }

    /**
     * @param outputDir
     *            the outputDir to set
     */
    public void setOutputDir(File outputDir) {
        this.outputDir = SpecsIo.mkdir(outputDir);
    }

    /**
     * @return the includeFolders
     */
    public List<File> getIncludeFolders() {
        return includeFolders;
    }

    /**
     * @param includeFolders
     *            the includeFolders to set
     */
    public void setIncludeFolders(List<File> includeFolders) {
        this.includeFolders = includeFolders;

        // Reset
        importsCache = new HashMap<>();
    }

    /**
     * @return the options
     */
    public Options getOptions() {
        return options;
    }

    /**
     * @param options
     *            the options to set
     */
    public void setOptions(Options options) {
        this.options = options;
    }

    public List<LaraResourceProvider> getIncludeResources() {
        return includeResources;
    }

    public MultiMap<String, LaraResourceProvider> getIncludeResourcesMap() {
        return includeResourcesMap.get();
        // Optional<LaraResourceProvider> findFirst = lara.getOptions().getIncludeResources().stream()
        // .filter(r -> r.getFileLocation().replace("/", File.separator).equals(importPath))
        // .findFirst();
    }

    private MultiMap<String, LaraResourceProvider> buildIncludeResourcesMap() {
        var resourcesMap = new MultiMap<String, LaraResourceProvider>();

        for (var resource : getIncludeResources()) {
            resourcesMap.put(resource.getFileLocation().replace("/", File.separator), resource);
        }

        return resourcesMap;
    }

    public void setIncludeResources(List<LaraResourceProvider> includeResources) {
        this.includeResources = includeResources;

        // Reset in case it is initialized
        if (includeResourcesMap.isInitialized()) {
            includeResourcesMap = Lazy.newInstance(() -> buildIncludeResourcesMap());
        }

        // Reset
        importsCache = new HashMap<>();
    }

    /**
     * Print information regarding the LaraC Execution
     * 
     * @param print
     *            Print streamer
     */
    public void printInformation(Output print) {
        // print.println("Concerning language: " + language); <-- Activate this when we start to use it
        print.println("Language Specification folder: " + xmlSpecDir);
        print.println("Output directory: " + outputDir);
        if (!includeFolders.isEmpty()) {
            print.println("Path included for import: " + includeFolders);
        }
        if (!includeResources.isEmpty()) {
            print.println("Resources included for import: "
                    + includeResources.stream().map(ResourceProvider::getResource).collect(Collectors.toList()));
        }
        print.println("Running on: " + System.getProperty("os.name"));
    }

    public boolean isDocumentationMode() {
        return documentationMode;
    }

    public void setDocumentationMode(boolean documentationMode) {
        this.documentationMode = documentationMode;
    }

    public List<LaraImport> getLaraImports(String filename, String filePath) {
        // Using forward slash as separator, since it is an illegal charater for a filename, both in Windows and Linux
        var key = filename + "////" + filePath;

        var laraImports = importsCache.get(key);

        if (laraImports == null) {
            laraImports = buildLaraImports(filename, filePath);
            importsCache.put(key, laraImports);
        }

        return laraImports;
    }

    private List<LaraImport> buildLaraImports(String filename, String filePath) {
        var laraImports = new ArrayList<LaraImport>();

        String relativePath = filePath + filename;

        // 1.
        // Check include folders
        for (final File path : getIncludeFolders()) {
            for (var ext : LaraC.getSupportedExtensions()) {
                var importPath = relativePath + "." + ext;

                final File importingFile = new File(path, importPath);
                if (importingFile.exists()) {
                    laraImports.add(new FileLaraImport(importPath, importingFile));
                    // System.out.println("FILE: " + importingFile);
                }
            }
        }

        // 2.
        // Check resource by filename, instead of resource name
        for (var ext : LaraC.getSupportedExtensions()) {
            var importPath = relativePath + "." + ext;

            var resource = getIncludeResourcesMap().get(importPath);
            if (!resource.isEmpty()) {
                laraImports.add(new ResourceLaraImport(importPath, resource.get(0)));
                // System.out.println("RESOURCE: " + resource.get(0));
            }
        }

        return laraImports;
    }

}
