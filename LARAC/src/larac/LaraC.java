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
package larac;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.modelmbean.XMLParseException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.input.BOMInputStream;
import org.dojo.jsl.parser.ast.ASTStart;
import org.dojo.jsl.parser.ast.LARAEcmaScript;
import org.dojo.jsl.parser.ast.LARAEcmaScriptTreeConstants;
import org.dojo.jsl.parser.ast.ParseException;
import org.dojo.jsl.parser.ast.Token;
import org.dojo.jsl.parser.ast.TokenMgrError;
import org.dojo.jsl.parser.ast.utils.ASTScriptImport;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import larac.exceptions.LARACompilerException;
import larac.exceptions.StopParseException;
import larac.exceptions.SyntaxException;
import larac.options.LaraCOptions;
import larac.options.resources.InputStreamProvider;
import larac.structure.AspectIR;
import larac.utils.FileUtils;
import larac.utils.Organizer;
import larac.utils.output.MessageConstants;
import larac.utils.output.Output;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.tools.lara.exception.BaseException;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.providers.ResourceProvider;
import tdrc.utils.StringUtils;

/**
 * Main class of the LARA compiler. The process involves parsing the input file given by the user, organize the AST,
 * according to the target language specification, and the create the Aspect Intermediate Representation (Aspect-IR)
 * 
 * @author Tiago
 * 
 */
public class LaraC {
    /**
     * The current version of the Lara compiler
     */
    public static final String FRONT_END_VERSION = "Lara compiler version: 2.51";

    public static final String PROPERTY_JAR_PATH = "lara.jarpath";

    private static final Collection<String> SUPPORTED_LARA_EXT = new LinkedHashSet<>(Arrays.asList("lara"));
    private static final Collection<String> SUPPORTED_SCRIPT_EXT = new LinkedHashSet<>(Arrays.asList("js", "mjs"));
    private static final Collection<String> SUPPORTED_EXT = new LinkedHashSet<>();
    static {
        SUPPORTED_EXT.addAll(SUPPORTED_LARA_EXT);
        SUPPORTED_EXT.addAll(SUPPORTED_SCRIPT_EXT);
    }

    private Output print;
    private String jarPath;
    private String laraName; // simple name of the lara file/resource
    private String laraPath; // complete path to the lara file/resource
    private File laraFile;
    private InputStreamProvider laraStreamProvider;
    private String prefix = "";

    private LanguageSpecificationV2 languageSpec;

    private AspectIR aspectIR;

    private boolean parsed;
    private LaraCOptions options;

    private Document aspectIRXmlRepresentation;
    private Map<String, LaraC> importedLARA;
    private List<LaraC> previouslyImportedLARA = new ArrayList<>();

    private boolean readyToParse = false;
    private boolean toJsMode = false;

    /**
     * Create a LaraC instance with the input arguments and the default output stream
     * 
     * @param args
     *            input arguments
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLParseException
     * @throws JAXBException
     */
    public LaraC(String args[])
            // throws IOException, ParserConfigurationException, SAXException, JAXBException, XMLParseException {
            throws IOException, ParserConfigurationException, SAXException, JAXBException {
        this(args, new Output());
    }

    /**
     * Create a LaraC instance with the input arguments and a user-defined output stream
     * 
     * @param args
     *            input arguments
     * @param out
     *            user-defined output, containing a user-defined verbose level
     */
    public LaraC(String args[], Output out) {

        setReadyToParse(laraConstructor(args, out));
    }

    /**
     * Create a LaraC instance with the input arguments and a user-defined output stream
     * 
     * @param args
     *            input arguments
     * @param out
     *            user-defined output, containing a user-defined verbose level
     */
    public LaraC(String args[], LanguageSpecificationV2 langSpec, Output out) {
        setReadyToParse(laraConstructor(args, langSpec, out));
    }

    /**
     * Generate a LaraC instance with a previous information of a (parent) LaraC instance and the target language
     * specification. <br>
     * NOTE: This constructor should only be called when an import is made in a LARA file
     * 
     * @param laraFile
     *            input lara file
     * @param options
     *            input arguments previously parsed by other LaraC instance
     * @param langSpec
     *            language specification previously parsed by other LaraC instance
     * @param output
     *            the output stream used on the parent LaraC instance
     */
    public static LaraC newImporter(File laraFile, LaraCOptions options, LanguageSpecificationV2 langSpec,
            Output output, Map<String, LaraC> importedFiles) {

        LaraC laraC = new LaraC(options, langSpec, output);
        laraC.getOptions().setLaraFile(laraC, laraFile);
        laraC.parseForImport(importedFiles);
        return laraC;
    }

    /**
     * Generate a LaraC instance with a previous information of a (parent) LaraC instance and the target language
     * specification. <br>
     * NOTE: This constructor should only be called when an import is made in a LARA file
     * 
     * * @param laraResource input lara resource
     * 
     * @param options
     *            input arguments previously parsed by other LaraC instance
     * @param langSpec
     *            language specification previously parsed by other LaraC instance
     * @param output
     *            the output stream used on the parent LaraC instance
     */
    public static LaraC newImporter(ResourceProvider laraResource, LaraCOptions options,
            LanguageSpecificationV2 langSpec,
            Output output, Map<String, LaraC> importedFiles) {
        LaraC laraC = new LaraC(options, langSpec, output);
        laraC.getOptions().setLaraResource(laraC, laraResource);
        laraC.parseForImport(importedFiles);

        return laraC;
    }

    private LaraC(LaraCOptions options, LanguageSpecificationV2 langSpec, Output output) {
        setParsed(false);
        this.options = options;
        print = output;
        languageSpec = langSpec;
        // languageSpecV2 = JoinPointFactory.fromOld(langSpec);
    }

    public void setToJsMode(boolean toJsMode, String laraFilename, String laraCode) {
        this.toJsMode = toJsMode;
        setLaraPath(laraFilename);
        setLaraStreamProvider(() -> SpecsIo.toInputStream(laraCode));
    }

    public boolean isToJsMode() {
        return toJsMode;
    }

    public static Collection<String> getSupportedScriptExtensions() {
        return SUPPORTED_SCRIPT_EXT;
    }

    public static Collection<String> getSupportedExtensions() {
        return SUPPORTED_EXT;
    }

    private void parseForImport(Map<String, LaraC> importedFiles) {
        importedLARA = importedFiles;
        aspectIR = new AspectIR(this);

        final boolean isParsed = execParse();
        if (!isParsed) {
            return;
        }
        setParsed(true);
        // parse();
        println("Importing: Parse Successful!");
    }

    /**
     * Constructs the LaraC instance with the given arguments and an output stream
     * 
     * @param args
     */
    private boolean laraConstructor(String[] args, Output out) {

        // initialize lara
        boolean ready = initialize(args, out);

        // Parse the language specification
        if (ready) {
            final File xmlSourceDir = options.getXmlSpecDir();
            setLanguageSpec(LanguageSpecificationV2.newInstance(xmlSourceDir, true));
        }
        return ready;
        // parse();
    }

    /**
     * Constructs the LaraC instance with the given arguments, the output stream and a {@link LanguageSpecificationV2}
     * instance
     * 
     * @param args
     * @param langSpec
     * @param out
     */
    private boolean laraConstructor(String[] args, LanguageSpecificationV2 langSpec, Output out) {

        // initialize lara
        boolean ready = initialize(args, out);

        // Parsing the language specification
        setLanguageSpec(langSpec);
        return ready;
        // parse();
    }

    /**
     * This is the last part of the constructor, after the language specification, in which we parse the lara file with
     * the given input and the language specification
     */
    private void parse() {
        final boolean isFileParsed = execParse();
        if (!isFileParsed) {
            return;
        }
        setParsed(true);
    }

    /**
     * Initialize LaraC with the input arguments and the output stream
     * 
     * @param args
     * @return true if all options were successfully managed, false otherwise
     */
    private boolean initialize(String[] args, Output out) {

        setPrint(out);
        printTopic("Setting up LARA");
        setImportedLARA(SpecsCollections.newHashMap());
        setAspectIR(new AspectIR(this));
        setParsed(false);
        setOptions(new LaraCOptions());

        final boolean optionsSetted = options.setOptions(this, args);
        if (optionsSetted) {
            options.printInformation(print);
        }
        return optionsSetted;
    }

    /**
     * Parse the input LARA file
     * 
     * @return true if parsed successful, false otherwise
     */
    private boolean execParse() {

        // Create a new Parser
        // final LARAEcmaScript parser = new LARAEcmaScript();
        // laraFile = new File(laraPath + laraSimpleName);

        // And then parse the input LARA file, if exists
        println(MessageConstants.FILE_READ + laraPath);
        // if (laraFile == null) {
        // LoggingUtils.msgInfo("!Terminating program");
        // return false;
        // }

        var extension = SpecsIo.getExtension(laraPath).toLowerCase();

        // System.out.println("EXTENSION: " + extension);

        if (extension.equals("lara")) {
            parseLara();
        } else if (SUPPORTED_SCRIPT_EXT.contains(extension)) {
            parseScript();
        } else {
            throw new RuntimeException("Tried to import unsupported file type: " + extension
                    + ". Supported types: lara, " + SUPPORTED_SCRIPT_EXT.stream().collect(Collectors.joining(", ")));
        }

        println("Parse Successful!");
        aspectIR.getAst().setLara(this);
        return true;
    }

    private void parseLara() {
        // try (BOMInputStream bis = new BOMInputStream(new FileInputStream(laraFile));
        try (BOMInputStream bis = new BOMInputStream(getLaraStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(bis));) {
            ASTStart ast = javaCCParser(br);
            aspectIR.setAst(ast);
        } catch (Exception e) {
            throw new LARACompilerException("when parsing: " + laraPath, e);
        }
    }

    private void parseScript() {
        var ast = new ASTStart(LARAEcmaScriptTreeConstants.JJTSTART);
        ast.jjtAddChild(new ASTScriptImport(getLaraStream(), getLaraPath()), 0);
        aspectIR.setAst(ast);
    }

    public static ASTStart javaCCParser(BufferedReader br) {
        LARAEcmaScript parser = new LARAEcmaScript(br);
        Throwable possibleException = null;
        ASTStart abstractTree = null;
        try {
            abstractTree = parser.parse();
            if (!parser.exceptions.isEmpty()) {
                throw new StopParseException();
                // System.out.println("Exceptions:");
                // parser.exceptions.forEach(System.out::println);
            }
            return abstractTree;
        } catch (StopParseException e) {
            // Just go to the finally statement and throw syntax exception with exceptions
        } catch (ParseException e) {
            parser.exceptions.add(e);
        } catch (TokenMgrError e) {
            parser.exceptions.add(e);
        } catch (Throwable e) {
            possibleException = e;
        } finally {

            if (!parser.exceptions.isEmpty()) {
                if (parser.exceptions.size() == 1) {
                    throw new RuntimeException("Problems while parsing LARA", parser.exceptions.get(0));
                } else {
                    throw new SyntaxException(parser.exceptions, possibleException);
                }

            } else if (possibleException != null) {
                parser.exceptions.add(possibleException);
                possibleException.printStackTrace();
                throw new SyntaxException(parser.exceptions);
            }
        }
        return abstractTree;

    }

    public static int getNumTokens(ASTStart abstractTree) {
        Token token = abstractTree.jjtGetFirstToken();
        int count = 0;
        while (token != null) {
            count++;
            token = token.next;
        }
        return count;
    }

    public int getNumTokens() {
        return getNumTokens(this.aspectIR.getAst());
    }

    /**
     * Organize the AST containing the aspects, according to the target language specification, to agree with the
     * Aspect-IR structure specification
     */
    public void toAspectIR() {
        aspectIR.organize();
    }

    /**
     * Generate the Aspect-IR and save it into an xml file with the same name as the input lara file
     */
    public void createXML() {

        aspectIRXmlRepresentation = aspectIR.toXML();

    }

    private void saveXML() {
        try {
            String fileName = laraName;

            fileName = fileName.replace(".lara", "");
            fileName = fileName.replace(".js", "");

            if (fileName.lastIndexOf(File.separator) > -1) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }

            saveXML(aspectIRXmlRepresentation, fileName);
        } catch (final TransformerException e) {
            throw new LARACompilerException("When saving Aspect-IR's XML", e);
        }
    }

    /**
     * Write an XML (org.w3c.dom.Document instance) into a file
     * 
     * @param doc
     *            the Document to be written
     * @param fileName
     *            output file name
     * @throws TransformerException
     *             if doc is not correctly transformed to XML
     */
    private void saveXML(Document doc, String fileName) throws TransformerException {
        final StringBuffer xmlStringBuffer = StringUtils.xmlToStringBuffer(doc, MessageConstants.INDENT);
        aspectIR.setXml(xmlStringBuffer);
        FileUtils.toFile(print, fileName, ".xml", xmlStringBuffer.toString(), options.getOutputDir());
    }

    /**
     * Kill the execution. The method is deprecated, please use {@link LaraC} .kill(error).
     * 
     * @param error
     */
    @Deprecated
    public void die(String error) {
        // errorln(error);
        throw new RuntimeException(error);// System.exit(-1);
    }

    /**
     * Kill the execution, throwing a RuntimeException
     * 
     * @param error
     *            an error message
     */
    /*
     * public static void kill(String error) { // ErrorMsg.say(error); throw new
     * RuntimeException(error);// System.exit(-1); }
     */

    // //////////////////////////////////////////
    // Methods to ease the use of the printer //
    // //////////////////////////////////////////

    /**
     * Method used to print the section where the front-end currently is
     * 
     * @param topic
     *            the current topic
     * @param larac
     *            the current instance of LaraC
     */
    public void printTopic(String topic) {
        final StringBuffer buf = new StringBuffer(MessageConstants.UNDERLINE);
        buf.append("\n  ");
        buf.append(MessageConstants.order++);
        buf.append(". ");
        buf.append(topic);
        buf.append("\n");
        buf.append(MessageConstants.OVERLINE);
        println(buf.toString());
    }

    /**
     * Method used to print the section where the front-end currently is
     * 
     * @param topic
     *            the current topic
     * @param larac
     *            the current instance of LaraC
     */
    public void printSubTopic(String topic) {
        final StringBuffer buf = new StringBuffer(MessageConstants.UNDERLINE);
        buf.append("\n");
        buf.append(topic);
        buf.append("\n");
        buf.append(MessageConstants.OVERLINE);
        println(buf.toString());
    }

    public void error(Object message) {
        print.error(message);
    }

    public void errorln(Object message) {
        print.errorln(message);
    }

    public void warn(Object message) {
        print.warn(message);
    }

    public void warnln(Object message) {
        print.warnln(message);
    }

    public void print(Object message) {
        print.print(message);
    }

    public void println(Object message) {
        print.println(message);
    }

    // //////////////////////////////////////////
    // ///////// Getters & Setters //////////////
    // //////////////////////////////////////////

    /**
     * @param printer
     *            the printer to set
     */
    public void setPrint(Output printer) {
        print = printer;
    }

    /**
     * @param the
     *            printer
     */
    public Output getPrint() {
        return print;
    }

    /**
     * @return the options
     */
    public LaraCOptions getOptions() {
        return options;
    }

    /**
     * @param options
     *            the options to set
     */
    public void setOptions(LaraCOptions options) {
        this.options = options;
    }

    /**
     * @return the laraName
     */
    public String getLaraName() {
        return laraName;
    }

    /**
     * @param laraName
     *            the laraName to set
     */
    public void setLaraName(String laraName) {
        this.laraName = laraName;
    }

    /**
     * @return the laraFile
     */
    public File getLaraFile() {
        return laraFile;
    }

    // public String getLaraLocation() {
    // if (laraFile != null) {
    // return IoUtils.getCanonicalPath(laraFile);
    // }
    //
    // return getLaraPath()
    // }

    /**
     * @param laraFile
     *            the laraFile to set
     */
    public void setLaraFile(File laraFile) {
        this.laraFile = laraFile;
    }

    /**
     * @return the jarPath
     */
    public String getJarPath() {
        return jarPath;
    }

    /**
     * @param jarPath
     *            the jarPath to set
     */
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix
     *            the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the languageSpec
     */
    public LanguageSpecificationV2 languageSpec() {
        return languageSpec;
    }

    /**
     * @param languageSpec
     *            the languageSpec to set
     */
    public void setLanguageSpec(LanguageSpecificationV2 languageSpec) {
        this.languageSpec = languageSpec;
    }

    /**
     * @return the aspectIR
     */
    public AspectIR aspectIR() {
        return aspectIR;
    }

    /**
     * @param aspectIR
     *            the aspectIR to set
     */
    public void setAspectIR(AspectIR aspectIR) {
        this.aspectIR = aspectIR;
    }

    /**
     * @return the initialized
     */
    public boolean isParsed() {
        return parsed;
    }

    /**
     * @param initialized
     *            the initialized to set
     */
    public void setParsed(boolean initialized) {
        parsed = initialized;
    }

    public LanguageSpecificationV2 getLanguageSpec() {
        return languageSpec;
    }

    // public LanguageSpecificationV2 getLanguageSpecV2() {
    // // return languageSpecV2;
    // return JoinPointFactory.fromOld(getLanguageSpec());
    // }

    public Organizer getOrganizer() {
        return new Organizer(getLanguageSpec());
    }

    public AspectIR getAspectIR() {
        return aspectIR;
    }

    /**
     * Compiling a LARA file consist on four phases:<br>
     * <ol>
     * <li>Parse - parse the input file based on the LARA grammar to obtain the AST</li>
     * <li>AST to Aspect-IR - validation and organization of the AST based on the Aspect-IR Specification</li>
     * <li>Generate XML - generate the XML representation of the Aspect-IR</li>
     * <li>Save the XML in a file, within the output folder: <output_dir>/<lara_file_name>.xml
     * </ol>
     * 
     * @return
     */
    public int compileAndSave() {

        compile();

        printTopic("Saving Aspect-IR");
        saveXML();
        println("Saved!");

        return 0;
    }

    /**
     * Compiling a LARA file consist on four phases:<br>
     * <ol>
     * <li>Parse - parse the input file based on the LARA grammar to obtain the AST</li>
     * <li>AST to Aspect-IR - validation and organization of the AST based on the Aspect-IR Specification</li>
     * <li>Generate XML - generate the XML representation of the Aspect-IR</li>
     * </ol>
     * <br>
     * <b>NOTE:</b>Use this method if you intend to access immediately the Aspect-IR xml representation and do not want
     * to create the xml file
     * 
     * @return an xml representation of the Aspect-IR
     */
    public Document compile() {
        parse();
        /*if (!isParsed()) {
        return n;
        }*/
        // On debug mode, dump the AST
        if (getOptions().isDebug()) {
            printTopic("Dumping AST");
            getAspectIR().getAst().dump("  ");
        }

        if (laraFile != null) {
            importedLARA.put(SpecsIo.getCanonicalPath(laraFile), this);
        }

        // Organize Aspects in AST according to the language specification
        printTopic("Organizing Aspects");
        toAspectIR();
        println("Organized!");
        // lara.println("\nFinal Structure");
        // lara.printIR();

        // Generate the Aspect-Intermediate Representation as an XML
        printTopic("Creating Aspect-IR");
        createXML();
        println("Created!");
        // On debug or When the user wants to see the Aspect-IR
        if (getOptions().isShowAspectIR() || getOptions().isDebug()) {
            printTopic("Dumping Aspect-IR");
            printAspectIR();
        }
        return getAspectIRXmlRepresentation();
    }

    public void printAspectIR() {
        try {
            println(StringUtils.xmlToStringBuffer(aspectIRXmlRepresentation, MessageConstants.INDENT).toString());
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new LARACompilerException("When dumping Aspect-IR", e);
        }
    }

    public Document getAspectIRXmlRepresentation() {
        return aspectIRXmlRepresentation;
    }

    public String getLaraPath() {

        return laraPath;
    }

    public void setLaraPath(String laraPath) {
        this.laraPath = laraPath;
    }

    public InputStream getLaraStream() {
        return laraStreamProvider.getInputStream();
    }

    public void setLaraStreamProvider(InputStreamProvider laraStream) {
        laraStreamProvider = laraStream;
    }

    public Map<String, LaraC> getImportedLARA() {
        return importedLARA;
    }

    public void setImportedLARA(Map<String, LaraC> importedLARA) {
        this.importedLARA = importedLARA;
    }

    public void addImportedLARA(String name, LaraC laraC) {

        importedLARA.put(name, laraC);
    }

    public void addImportedLARA(Map<String, LaraC> laras) {
        importedLARA.putAll(laras);
    }

    public boolean wasImported(String name) {
        return importedLARA.containsKey(name);
    }

    public LaraC getImportedLARA(String name) {
        return importedLARA.get(name);
    }

    public boolean isReadyToParse() {
        return readyToParse;
    }

    public void setReadyToParse(boolean readyToParse) {
        this.readyToParse = readyToParse;
    }

    public List<LaraC> getPreviouslyImportedLARA() {
        return previouslyImportedLARA;
    }

    public void setPreviouslyImportedLARA(List<LaraC> previouslyImportedLARA) {
        this.previouslyImportedLARA = previouslyImportedLARA;
    }

    public void addPreviouslyImportedLARA(LaraC previouslyImportedLARA) {
        // LaraLog.debug("PREVIOUSLY IMPORTED: " + previouslyImportedLARA.getLaraPath() + " @ " + getLaraPath());
        this.previouslyImportedLARA.add(previouslyImportedLARA);
    }

    public static Optional<Aspects> parseLara(File laraFile, LanguageSpecificationV2 languageSpecification) {
        // Pass through LaraC
        List<String> args = new ArrayList<>();

        args.add(laraFile.getAbsolutePath());
        args.add("--doc");
        args.add("--verbose");
        args.add("0");

        LaraC larac = new LaraC(args.toArray(new String[0]), languageSpecification,
                new Output(0));
        Document aspectIr = null;

        try {
            aspectIr = larac.compile();
        } catch (BaseException e) {
            // If LARA exception, generate exception
            SpecsLogs.msgInfo("Could not compile file '" + laraFile + "': "
                    + e.generateExceptionBuilder().getRuntimeException());
            return Optional.empty();
        } catch (Exception e) {
            SpecsLogs.warn("Could not compile file '" + laraFile + "'", e);
            return Optional.empty();
        }

        try {
            return Optional.of(new Aspects(aspectIr, ""));
        } catch (Exception e) {
            SpecsLogs.msgInfo("Could not create aspects: " + e.getMessage());
            return Optional.empty();
        }

    }

    public static boolean isSupportedExtension(String filenameExtension) {
        return SUPPORTED_EXT.contains(filenameExtension.toLowerCase());
    }

    /**
     * 
     * Generates a XML document with the AspectIR corresponding to the JS code that needs to be executed in order to
     * import the given name, using the same format as the imports in LARA files (e.g. weaver.Query).
     * 
     * @param importName
     */
    public Document importLara(String importName) {
        // Create LaraC based on current LaraC, to keep imports

        LaraC laraC = new LaraC(options, languageSpec, print);
        // laraC.getOptions().setLaraResource(laraC, laraResource);
        laraC.setLaraPath("dummy.lara");
        laraC.setLaraStreamProvider(() -> SpecsIo.toInputStream("import " + importName + ";"));
        laraC.parseForImport(getImportedLARA());

        // laraC.compile();
        // laraC.parse();

        // var previouslyImported = new HashSet<>(getImportedLARA().keySet());

        // System.out.println("IMPORTED LARA BEFORE: " + previouslyImported);

        if (importName.endsWith(".")) {
            throw new RuntimeException("Invalid import, cannot end with '.': " + importName);
        }

        // Split into fileName and filePath
        int dotIndex = importName.lastIndexOf('.');

        var fileName = dotIndex == -1 ? importName : importName.substring(dotIndex + 1);
        var filePath = dotIndex == -1 ? "" : importName.substring(0, dotIndex + 1);
        filePath = filePath.replace('.', '\\');

        // Get LARA imports
        var laraImports = getOptions().getLaraImports(fileName, filePath);

        laraImports.stream().forEach(laraImport -> laraImport.resolveImport(laraC));

        // var currentlyImported = getImportedLARA();
        // System.out.println("IMPORTED LARA AFTER: " + currentlyImported);

        // var newKeys = new HashSet<>(currentlyImported.keySet());
        // System.out.println("NEW KEYS BEFORE: " + newKeys);
        // newKeys.removeAll(previouslyImported);

        // System.out.println("NEW KEYS AFTER: " + newKeys);

        var aspectIr = laraC.getAspectIR();
        var doc = aspectIr.toXML();

        return doc;

        // for (var key : newKeys) {
        // System.out.println("KEY: " + key);
        // var larac = currentlyImported.get(key);
        // var aspectIr = larac.getAspectIR();
        //
        // System.out.println("IS PARSED? " + larac.isParsed());
        // // Alternatively, we could call larac.createXML();, but this is more explicit
        // var doc = aspectIr.toXML();
        //
        // System.out.println("DOC: " + doc);
        // }

    }
}
