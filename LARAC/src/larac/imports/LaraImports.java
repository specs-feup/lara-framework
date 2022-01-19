/**
 * Copyright 2021 SPeCS.
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

package larac.imports;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.dojo.jsl.parser.ast.ASTAspectDef;
import org.dojo.jsl.parser.ast.SimpleNode;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;

import larac.LaraC;
import larac.exceptions.LARACompilerException;
import larac.utils.output.MessageConstants;
import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.StringUtils;

public class LaraImports {

    public static void importLaraFile(final LaraC lara, final String importPath, final File importingFile) {

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
        LaraImports.rearrangeImportedLaraAndImportAspects(lara, importPath, importingLara);
        lara.setImportedLARA(importingLara.getImportedLARA());
        lara.addImportedLARA(canonicalPath, importingLara);
    }

    public static void importLaraResource(final LaraC lara, String filePath,
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
        LaraImports.rearrangeImportedLaraAndImportAspects(lara, resource.replace("/", File.separator), importingLara);
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
        LaraImports.importAspects(lara, filePath, importingLara);
    }

    private static void importAspects(final LaraC lara, String filePath, final LaraC importingLara) {
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

}
