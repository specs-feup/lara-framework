/**
 * Copyright 2013 SPeCS Research Group.
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

package larac.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dojo.jsl.parser.ast.ASTAspectDef;
import org.dojo.jsl.parser.ast.ASTStart;
import org.dojo.jsl.parser.ast.SimpleNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import larac.LaraC;
import larac.exceptions.AspectIRException;
import larac.exceptions.LARACompilerException;
import larac.exceptions.LaraException;
import larac.utils.output.MessageConstants;

/**
 * Class for creating an Aspect-Intermediate Representation (Aspect-IR)
 * 
 * @author Tiago
 * 
 */
public class AspectIR {
    private int codeCount;
    // private ASTCompositeReference compositeReference;
    private ASTStart ast;
    // private Map<String, ASTCodeDef> codedefs;
    private List<ASTAspectDef> aspectdefs;
    private Map<String, ASTAspectDef> importedAspectDefs;
    private Map<String, SimpleNode> globalElements;
    private StringBuffer xml;
    private final LaraC lara;
    private static int globalUID = 0;

    public AspectIR(LaraC lara) {
        this.lara = lara;
        codeCount = 0;
        // codedefs = new HashMap<>();
        aspectdefs = new ArrayList<>();
        importedAspectDefs = new HashMap<>();
        setGlobalElements(new LinkedHashMap<>());
        xml = null;
    }

    /**
     * * Organize the AST containing the aspects, according to the target language specification, to agree with the
     * Aspect-IR structure specification
     * 
     */
    public void organize() {
        try {
            ast.organize(lara);
        } catch (Exception e) {
            throw new AspectIRException(e);
        }

    }

    /**
     * Generate Aspect-IR as an org.w3c.dom.Document instance
     * 
     * @return the Aspect-IR Document instance
     */
    public Document toXML() {
        final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = dbfac.newDocumentBuilder();
            final Document doc = docBuilder.newDocument();
            final Element root = doc.createElement("aspects");
            if (!aspectdefs.isEmpty()) {
                // return doc;
                root.setAttribute("main", aspectdefs.get(0).getName());
            } else {
                root.setAttribute("main", "");
            }
            doc.appendChild(root);

            for (final ASTAspectDef aspect : aspectdefs) {
                aspect.toXML(doc, root);
            }
            for (final ASTAspectDef aspect : importedAspectDefs.values()) {
                aspect.toXML(doc, root);
            }
            for (final SimpleNode node : globalElements.values()) {
                node.globalToXML(doc, root);
            }
            return doc;
        } catch (final ParserConfigurationException e) {
            throw new LARACompilerException("When generating Aspect-IR", e);
        }

    }

    /**
     * Add a new Codedef to the Codedefs' hashmap
     * 
     * @param name
     *            Codedef identifier
     * @param codedef
     *            the Codedef instance
     */
    // public void addCodeDef(String name, ASTCodeDef codedef) {
    // if (codedefs.containsKey(name)) {
    // throw new LARACompilerException("Duplicated name for codedef \"" + name + "\"");
    // }
    // codedefs.put(name, codedef);
    // }

    /**
     * Add a new AspectDef to the AspectDefs' List
     * 
     * @param name
     * @param aspectdef
     */
    public void addAspectDef(String name, ASTAspectDef aspectdef) {
        if (aspectdefs.contains(aspectdef)) {
            throw new LARACompilerException("Duplicated name for aspectdef \"" + name + "\"");
        }
        if (globalElements.containsKey(name)) {

            throw new LaraException(
                    "Found an aspect with the same name as a declaration: " + name + ".");
        }
        aspectdefs.add(aspectdef);
    }

    /**
     * Return the AspectDef according to its name, or null if the AspectDef does not exist in the List
     * 
     * @param name
     *            the name of the Aspect to be returned
     * @return the corresponding AspectDef, or null if the AspectDef does not exist
     */
    public ASTAspectDef getAspectDef(String name) {
        for (final ASTAspectDef asp : aspectdefs) {
            if (asp.getName().equals(name)) {
                return asp;
            }
        }
        return null;
    }

    /**
     * Get an imported AspectDef
     * 
     * @param name
     * @return
     */
    public ASTAspectDef getImportedAspectDef(String name) {
        ASTAspectDef asp = importedAspectDefs.get(name);
        if (asp != null) {
            return asp;
        }
        String lastName = "";
        final String endingName = MessageConstants.NAME_SEPARATOR + name;
        for (final String key : importedAspectDefs.keySet()) {
            if (key.endsWith(endingName)) {
                if (asp != null) {
                    // String error = "Found more than one aspect with the same
                    // name in different files for '" + name
                    final String error = "Found more than one aspect with name '" + name + "' in different files.\n"
                            + "\tTwo of them are \"" + lastName.replace(MessageConstants.NAME_SEPARATOR, ".")
                            + "\" and \"" + key.replace(MessageConstants.NAME_SEPARATOR, ".")
                            + "\".\n\tPlease refine which is the intended aspect.";
                    throw new LARACompilerException(error);
                }

                asp = importedAspectDefs.get(key);
                lastName = key;
            }

        }
        if (asp != null) {
            return asp;
        }
        for (LaraC laraC : lara.getPreviouslyImportedLARA()) {
            List<ASTAspectDef> aspectdefs2 = laraC.getAspectIR().getAspectdefs();
            for (ASTAspectDef astAspectDef : aspectdefs2) {
                String nameWPrefix = laraC.getPrefix() + astAspectDef.getName();
                if (nameWPrefix.endsWith(endingName)) {

                    return astAspectDef;
                }
            }
            asp = laraC.getAspectIR().getImportedAspectDef(name);
            if (asp != null) {
                return asp;
            }
        }
        return asp;
    }

    /**
     * Get a codedef based on its name
     * 
     * @param codeName
     * @return
     */
    // public ASTCodeDef getCodedef(String codeName) {
    //
    // return codedefs.get(codeName);
    // }

    /**
     * Prints the aspects intermediate representation
     */
    public void printIR() {
        // lara.println("CodeDefs");
        // for (final ASTCodeDef code : codedefs.values()) {
        // code.printIR();
        // }
        lara.println("AspectDefs");
        for (final ASTAspectDef aspect : aspectdefs) {
            aspect.printIR();
        }
        lara.println("Declarations (TODO)");
        // for (final SimpleNode node : globalElements.values()) {
        // node.printIR();
        // }
    }

    /**
     * @return the codeCount
     */
    public int getCodeCount() {
        return codeCount;
    }

    /**
     * @param codeCount
     *            the codeCount to set
     */
    public void setCodeCount(int codeCount) {
        this.codeCount = codeCount;
    }

    // /**
    // * @return the compositeReference
    // */
    // public ASTCompositeReference getCompositeReference() {
    // return compositeReference;
    // }
    //
    // /**
    // * @param compositeReference
    // * the compositeReference to set
    // */
    // public void setCompositeReference(ASTCompositeReference compositeReference) {
    // this.compositeReference = compositeReference;
    // }

    /**
     * @return the ast
     */
    public ASTStart getAst() {
        return ast;
    }

    /**
     * @param ast
     *            the ast to set
     */
    public void setAst(ASTStart ast) {
        this.ast = ast;
    }

    /**
     * @return the codedefs
     */
    // public Map<String, ASTCodeDef> getCodedefs() {
    // return codedefs;
    // }

    /**
     * @param codedefs
     *            the codedefs to set
     */
    // public void setCodedefs(Map<String, ASTCodeDef> codedefs) {
    // this.codedefs = codedefs;
    // }

    /**
     * @return the aspectdefs
     */
    public List<ASTAspectDef> getAspectdefs() {
        return aspectdefs;
    }

    /**
     * @param aspectdefs
     *            the aspectdefs to set
     */
    public void setAspectdefs(List<ASTAspectDef> aspectdefs) {
        this.aspectdefs = aspectdefs;
    }

    /**
     * @return the importedAspectDefs
     */
    public Map<String, ASTAspectDef> getImportedAspectDefs() {
        return importedAspectDefs;
    }

    /**
     * @param importedAspectDefs
     *            the importedAspectDefs to set
     */
    public void setImportedAspectDefs(Map<String, ASTAspectDef> importedAspectDefs) {
        this.importedAspectDefs = importedAspectDefs;
    }

    /**
     * @return the xml
     */
    public StringBuffer getXml() {
        return xml;
    }

    /**
     * @param xml
     *            the xml to set
     */
    public void setXml(StringBuffer xml) {
        this.xml = xml;
    }

    /**
     * Generate a new name for a code
     * 
     * @return
     */
    public String generateNewCodeName() {
        final String codeName = lara.getLaraName() + "_code_" + codeCount++;
        return codeName;
    }

    public Map<String, SimpleNode> getGlobalElements() {
        return globalElements;
    }

    public void addGlobalElement(String name, SimpleNode element) {
        addGlobalElement(name, element, lara.getLaraPath());
    }

    public void addGlobalStatement(SimpleNode element) {
        addGlobalStatement(element, lara.getLaraPath());
    }

    public void addGlobalStatement(SimpleNode element, String origin) {
        addGlobalElement("statement" + newGlobalUID(), element, origin);
    }

    private static int newGlobalUID() {

        return globalUID++;
    }

    public void addGlobalElement(String name, SimpleNode element, String origin) {
        if (globalElements.containsKey(name)) {
            String location;
            String laraPath = lara.getLaraPath();
            if (laraPath.equals(origin)) {
                location = "both in file " + origin;
            } else {
                location = "one in file " + lara.getLaraFile() + " other in file " + origin;
            }
            throw new LaraException("Two Declarations with the same name: " + name + ". " + location + ".");
        }
        boolean foundAspect = aspectdefs.stream().anyMatch(aspect -> aspect.getName().equals(name));
        if (foundAspect) {
            String location;
            String laraPath = lara.getLaraPath();
            if (laraPath.equals(origin)) {
                location = "both in file " + origin;
            } else {
                location = "aspect in file " + laraPath + ", declaration in file " + origin;
            }
            throw new LaraException(
                    "Found an aspect with the same name as a declaration: "
                            + name + ". " + location + ".");
        }
        globalElements.put(name, element);
    }

    public void setGlobalElements(Map<String, SimpleNode> globalElements) {
        this.globalElements = globalElements;
    }

    public boolean containsGlobal(String var) {
        return globalElements.containsKey(var);
    }

    public SimpleNode getGlobal(String var) {
        return globalElements.get(var);
    }

}