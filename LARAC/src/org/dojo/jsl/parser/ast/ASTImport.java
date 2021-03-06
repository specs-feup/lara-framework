/* Generated By:JJTree: Do not edit this line. ASTImport.java Version 4.3 */
/*
 * JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,
 * NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true
 */
package org.dojo.jsl.parser.ast;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import larac.exceptions.LARACompilerException;
import larac.utils.output.MessageConstants;
import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.StringUtils;

public class ASTImport extends SimpleNode {
    public ASTImport(int id) {
        super(id);
    }

    public ASTImport(LARAEcmaScript p, int id) {
        super(p, id);
    }

    @Override
    public Object organize(Object obj) {
        declareGlobal(getLara());
        return null;
    }

    @Override
    public void declareGlobal(LaraC lara) {
        // Ignore imports if in documentation mode
        if (lara.getOptions().isDocumentationMode()) {
            return;
        }

        String fileName = value + ".lara";
        String filePath = "";
        if (getChildren() != null) {
            for (final Node n : getChildren()) {
                filePath += ((ASTIdentifier) n).value + File.separator;
            }
        }

        MessageConstants.addSpace();
        if (fileName.equals("*.lara")) {

            importAllLara(lara, filePath);

        } else {

            importSingleLara(lara, fileName, filePath);
        }
        MessageConstants.removeSpace();
        lara.println("");

    }

    /**
     * Imports all lara files/resources containing the input file path prefix
     * 
     * @param lara
     * @param filePath
     */
    private void importAllLara(final LaraC lara, String filePath) {

        boolean anyImported = false;

        // 1.
        // Search all included folders that contains the path as child
        for (final File path : lara.getOptions().getIncludeFolders()) {
            if ((new File(path, filePath).exists())) {

                final File importingDir = new File(path, filePath);

                importLaraFiles(importingDir, filePath, lara);
                anyImported = true;
            }
        }

        // 2.
        // Search the resources defined in the given package;
        List<LaraResourceProvider> resources = lara.getOptions().getIncludeResources().stream()
                // .filter(r -> r.getResourceLocation().replace("/", File.separator).equals(filePath))
                .filter(r -> r.getFileLocation().replace("/", File.separator).equals(filePath))
                .collect(Collectors.toList());
        if (!resources.isEmpty()) {
            resources.forEach(r -> importLaraResource(lara, filePath, r));
            anyImported = true;
        }

        if (!anyImported) {
            throw newException("No aspect was found in the included folder for the aspect file: "
                    + filePath.replace("*.lara", ""));
        }
    }

    private void importLaraFiles(final File importingDir, String filePath, final LaraC lara) {
        lara.printSubTopic("Importing all lara files from " + importingDir.getAbsolutePath());
        if (!importingDir.isDirectory()) {
            throw newException("Import: The following path is not a directory: " + importingDir);
        }

        MessageConstants.addSpace();
        final File[] files = importingDir.listFiles();
        if (files.length != 0) {
            for (final File importingLaraFile : files) {
                if (importingLaraFile.isFile() && SpecsIo.getExtension(importingLaraFile).equals("lara")) {

                    importLaraFile(lara, filePath, importingLaraFile);
                }
            }
        } else {
            lara.warnln("No LARA files to import.");
        }
        MessageConstants.removeSpace();
    }

    /**
     * Import a single lara file with a given name, in the file path
     * 
     * @param lara
     * @param fileName
     * @param filePath
     */
    private void importSingleLara(final LaraC lara, String fileName, String filePath) {
        String relativePath = filePath + fileName;

        // 1.
        // Check include folders
        for (final File path : lara.getOptions().getIncludeFolders()) {
            final File importingFile = new File(path, relativePath);
            if (importingFile.exists()) {
                lara.printSubTopic("Importing " + importingFile);
                if (!importingFile.exists()) {
                    throw newException(
                            "Import: Could not find file '" + fileName + "' on file path: " + path + relativePath);
                }

                importLaraFile(lara, relativePath, importingFile);
                return;
            }
        }

        // 2.
        // Check resource by filename, instead of resource name
        Optional<LaraResourceProvider> findFirst = lara.getOptions().getIncludeResources().stream()
                // .filter(r -> r.getResource().replace("/", File.separator).equals(relativePath))
                .filter(r -> r.getFileLocation().replace("/", File.separator).equals(relativePath))
                .findFirst();

        if (findFirst.isPresent()) {
            importLaraResource(lara, relativePath, findFirst.get());
            return;
        }

        throw newException(
                "No aspect was found in the included folders/resources for the aspect file: " + relativePath);

    }
    /*
    private boolean importFromResource(final LaraC lara, String relativePath) {
    
        // Check resource by filename, instead of resource name
        Optional<LaraResourceProvider> findFirst = lara.getOptions().getIncludeResources().stream()
                // .filter(r -> r.getResource().replace("/", File.separator).equals(relativePath))
                .filter(r -> r.getFileLocation().replace("/", File.separator).equals(relativePath))
                .findFirst();
    
        if (findFirst.isPresent()) {
            importLaraResource(lara, relativePath, findFirst.get());
            return true;
        }
    
        return false;
    }
    */
    /*
    private boolean importFromInclude(final LaraC lara, String fileName, String filePath) {
        String relativePath = filePath + fileName;
    
        for (final File path : lara.getOptions().getIncludeFolders()) {
            final File importingFile = new File(path, relativePath);
            if (importingFile.exists()) {
                lara.printSubTopic("Importing " + importingFile);
                if (!importingFile.exists()) {
                    throw newException(
                            "Import: Could not find file '" + fileName + "' on file path: " + path + relativePath);
                }
    
                importLaraFile(lara, relativePath, importingFile);
                return true;
            }
        }
    
        return false;
    }
    */

    private static void importLaraFile(final LaraC lara, final String importPath, final File importingFile) {
        String canonicalPath = SpecsIo.getCanonicalPath(importingFile);
        if (lara.wasImported(canonicalPath)) {
            LaraC importedLARA = lara.getImportedLARA(canonicalPath);
            if (importedLARA == null) {
                throw new LARACompilerException("Problem with recursive import with file: " + canonicalPath
                        + " one occurrence is: " + lara.getLaraPath());
            }
            // LaraLog.debug("ALREADY IMPORTED:" + importedLARA);
            lara.addPreviouslyImportedLARA(importedLARA);
            lara.println(" Aspects from file " + importPath + " were already imported. Will ignore this import.");
            return;
        }
        lara.printSubTopic(" Importing aspects from file " + importPath);
        lara.addImportedLARA(canonicalPath, null);
        final LaraC importingLara = LaraC.newImporter(importingFile, lara.getOptions(), lara.languageSpec(),
                lara.getPrint(), lara.getImportedLARA());
        rearrangeImportedLaraAndImportAspects(lara, importPath, importingLara);
        lara.setImportedLARA(importingLara.getImportedLARA());
        lara.addImportedLARA(canonicalPath, importingLara);
    }

    private static void importLaraResource(final LaraC lara, String filePath,
            final LaraResourceProvider importingResource) {
        String importName = importingResource.getFileLocation();
        String resource = importingResource.getResource();
        // if (lara.wasImported(resource)) {

        if (lara.wasImported(importName)) {
            LaraC importedLARA = lara.getImportedLARA(importName);
            if (importedLARA == null) {
                throw new LARACompilerException("Problem with recursive import with resource: " + resource
                        + " one occurrence is: " + lara.getLaraPath());
            }
            lara.addPreviouslyImportedLARA(importedLARA);
            lara.println(" Aspects from import " + importName + " were already imported. Will ignore this import.");
            return;
        }

        if (importName.equals(resource)) {
            lara.printSubTopic(" Importing aspects from resource " + resource);
        } else {
            lara.printSubTopic(" Importing aspects from resource " + resource + " (import '" + importName + "')");
        }

        // lara.addImportedLARA(resource);
        lara.addImportedLARA(importName, null);
        final LaraC importingLara = LaraC.newImporter(importingResource, lara.getOptions(), lara.languageSpec(),
                lara.getPrint(), lara.getImportedLARA());
        rearrangeImportedLaraAndImportAspects(lara, resource.replace("/", File.separator), importingLara);
        lara.setImportedLARA(importingLara.getImportedLARA());
        lara.addImportedLARA(importName, importingLara);

    }

    private static void rearrangeImportedLaraAndImportAspects(final LaraC lara, String filePath,
            final LaraC importingLara) {
        String prefix = filePath.replace(".lara", MessageConstants.NAME_SEPARATOR);
        prefix = prefix.replace(File.separator, MessageConstants.NAME_SEPARATOR);

        importingLara.setPrefix(prefix);
        lara.println("Organizing imported aspects from " + filePath);
        importingLara.toAspectIR();
        lara.println("Finished organizing imported aspects!");
        importAspects(lara, filePath, importingLara);
    }

    public static void importAspects(final LaraC lara, String filePath, final LaraC importingLara) {
        List<ASTAspectDef> importingAspects = importingLara.aspectIR().getAspectdefs();

        if (!importingAspects.isEmpty()) {
            for (final ASTAspectDef importingAsp : importingAspects) {
                final String key = importingLara.getPrefix() + importingAsp.getName();

                lara.aspectIR().getImportedAspectDefs().put(key, importingAsp);
            }
            String importingAspectsStr = StringUtils.join(importingAspects, ASTAspectDef::getName, ", ");
            lara.println(" Imported the following aspects: " + importingAspectsStr);
        }

        Map<String, SimpleNode> globalElements = importingLara.aspectIR().getGlobalElements();
        if (!globalElements.isEmpty()) {
            globalElements.entrySet()
                    .forEach(e -> lara.aspectIR().addGlobalElement(e.getKey(), e.getValue(), filePath));
            String importingAspectsStr = StringUtils.join(globalElements.keySet(), ", ");
            lara.println(" Imported the following aspects: " + importingAspectsStr);
        }
        // Recursive import
        Map<String, ASTAspectDef> recursiveImport = importingLara.aspectIR().getImportedAspectDefs();
        if (!recursiveImport.isEmpty()) {
            lara.aspectIR().getImportedAspectDefs().putAll(recursiveImport);
            String importedFromImporting = StringUtils.join(recursiveImport.keySet(),
                    s -> s.replace(MessageConstants.NAME_SEPARATOR, "."), ",");
            lara.println(" Recursive import: " + importedFromImporting);
        }
    }

    @Override
    public void toXML(Document doc, Element parent) {
    }
}
/*
 * JavaCC - OriginalChecksum=70d3639852523f4c77e45a34861ed251 (do not edit this
 * line)
 */
