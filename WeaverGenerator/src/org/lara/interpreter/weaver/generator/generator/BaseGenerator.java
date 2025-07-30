/**
 * Copyright 2015 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver.generator.generator;

import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.LanguageSpecification;
import pt.up.fe.specs.util.SpecsCheck;

import java.io.File;
import java.util.Arrays;

public abstract class BaseGenerator {

    private String outPackage;
    private LanguageSpecification languageSpecification;
    private File languageSpecificationDir;
    private File outDir;
    private String weaverName;
    private boolean abstractGetters;
    private boolean events;
    private boolean implMode;
    // If nodeGenerics is null, the code is not generated with generics
    // If nodeGenerics is Object, the code is generated with <T>
    // Else, the code is generated with <T extends «nodeGenerics» >
    private String nodeType;
    private boolean showGraph;
    private boolean generated;
    private boolean json;
    private boolean defs;
    private String concreteClassesPrefix;

    public BaseGenerator() {
        init();
    }

    public BaseGenerator(BaseGenerator baseGenerator) {
        init(baseGenerator);
    }

    /**
     * Generate the code for the given language specification
     *
     * @return true if generation was successful; false otherwise
     * @throws RuntimeException if the language specification was not defined
     */
    public void generate() {
        if (languageSpecification == null) {
            throw new RuntimeException("The language specification must be defined prior to the generation");
        }
        generateCode();
        generated = true;
    }

    /**
     * Print the generated code in files
     *
     * @return true if generation was successful; false otherwise
     * @throws RuntimeException if the language specification was not defined
     */
    public void print() {
        if (!generated) {
            throw new RuntimeException("The generate method must be called prior to this method");
        }
        printCode();
    }

    /**
     * Initialize the Base Generator with the default values
     */
    private void init() {

        languageSpecification = null;
        setOutPackage(GenConstants.getDefaultPackage());
        setOutDir(GenConstants.getDefaultOutputDir());
        setWeaverName(GenConstants.getDefaultWeaverName());
        setAbstractGetters(GenConstants.getDefaultAbstractGetters());
        setEvents(GenConstants.getDefaultEvents());
        setImplMode(GenConstants.getDefaultImplMode());
        setNodeType(GenConstants.getDefaultNodeType());
        setShowGraph(GenConstants.getDefaultShowGraph());
        setGenerated(false);
        setJson(false);
        setDefs(false);
        setConcreteClassesPrefix(null);
    }

    /**
     * Initialize the generator based on another generator
     *
     * @param baseGenerator
     */
    private void init(BaseGenerator baseGenerator) {
        if (baseGenerator == null) {
            init();
            return;
        }

        setOutPackage(baseGenerator.getOutPackage());
        setOutDir(baseGenerator.getOutDir());
        setWeaverName(baseGenerator.getWeaverName());
        setAbstractGetters(baseGenerator.isAbstractGetters());
        setEvents(baseGenerator.hasEvents());
        setImplMode(baseGenerator.hasImplMode());
        setNodeType(baseGenerator.getNodeType());
        setShowGraph(baseGenerator.isShowGraph());
        setGenerated(false);
    }

    /**
     * Generate the code for the given language specification
     *
     * @return
     */
    protected abstract void generateCode();

    protected abstract void printCode();

    public BaseGenerator weaverName(String name) {
        setWeaverName(name);
        return this;
    }

    public BaseGenerator abstractGetters(boolean abstractGetters) {
        setAbstractGetters(abstractGetters);
        return this;
    }

    public BaseGenerator events(boolean events) {
        setEvents(events);
        return this;
    }

    public BaseGenerator implMode(boolean implMode) {
        setImplMode(implMode);
        return this;
    }

    public BaseGenerator setPackage(String outPackage) {
        setOutPackage(outPackage);
        return this;
    }

    public BaseGenerator outputDir(File outputDir) {
        setOutDir(outputDir);
        return this;
    }

    public BaseGenerator outputDir(String outputDir) {
        return outputDir(new File(outputDir));
    }

    public BaseGenerator languageSpec(String langSpec) {
        setLanguageSpecification(langSpec);
        return this;
    }

    public BaseGenerator languageSpec(File langSpec) {
        setLanguageSpecification(langSpec);
        return this;
    }

    public BaseGenerator nodeType(Class<?> className) {
        setNodeType(className);
        return this;
    }

    public BaseGenerator nodeType(String className) {
        setNodeType(className);
        return this;
    }

    /**
     * Get the output package for the weaver and join points
     *
     * @return
     */
    public String getOutPackage() {
        return outPackage;
    }

    /**
     * Set the output package for the weaver and join points
     *
     * @param outPackage
     */
    public void setOutPackage(String outPackage) {
        this.outPackage = outPackage;
    }

    /**
     * Get the output dir for the generated files
     *
     * @return
     */
    public File getOutDir() {
        return outDir;
    }

    /**
     * Set the output dir for the generated files
     *
     * @return
     */
    public void setOutDir(File outDir) {
        this.outDir = outDir;
    }

    /**
     * Get the name of the weaver
     *
     * @return
     */
    public String getWeaverName() {
        return weaverName;
    }

    /**
     * Set the name of the weaver
     *
     * @return
     */
    public void setWeaverName(String weaverName) {
        this.weaverName = weaverName;
    }

    /**
     * See if the getters should be generated as abstract, and thus no field is
     * generated
     *
     * @return
     */
    public boolean isAbstractGetters() {
        return abstractGetters;
    }

    /**
     * Should the generated code have events or not
     *
     * @return
     */
    public boolean hasEvents() {
        return events;
    }

    /**
     * Set if the getters should be generated as abstract, and thus no field is
     * generated
     *
     * @return
     */
    public void setAbstractGetters(boolean abstractGetters) {
        this.abstractGetters = abstractGetters;
    }

    /**
     * Get the base AST node.
     *
     * @return
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Get the base AST node name.
     *
     * @return
     */
    public String getNodeName() {
        var nodeNames = nodeType.split("\\.");

        SpecsCheck.checkArgument(nodeNames.length > 0, () -> "Expected node type '" + nodeType
                + "' to result in one or more names: " + Arrays.asList(nodeNames));

        return nodeNames[nodeNames.length - 1];
    }

    /**
     * Set the base AST node.
     *
     * @param nodeType
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Set the generic type of the join points
     *
     * @param nodeType
     */
    public void setNodeType(Class<?> nodeType) {
        this.nodeType = nodeType.getCanonicalName().toString();
    }

    /**
     * Should the generator show a graph in the end of the generation
     *
     * @return
     */
    public boolean isShowGraph() {
        return showGraph;
    }

    /**
     * Set if the generator should show a graph in the end of the generation
     */
    public void setShowGraph(boolean showGraph) {
        this.showGraph = showGraph;
    }

    /**
     * The language specification for this generation
     *
     * @return
     */
    public LanguageSpecification getLanguageSpecification() {
        return languageSpecification;
    }

    public File getLanguageSpecificationDir() {
        return languageSpecificationDir;
    }

    /**
     * Create a language specification using the models defined in the given
     * directory
     *
     * @param langSpecDirStr the input folder
     */
    public void setLanguageSpecification(File langSpecDir) {
        languageSpecification = LanguageSpecification.newInstance(langSpecDir);
        this.languageSpecificationDir = langSpecDir;
    }

    /**
     * Create a language specification using the models defined in the given
     * directory name
     *
     * @param langSpecDirStr name of the input folder
     */
    public void setLanguageSpecification(String langSpecDirStr) {
        final File langSpecDir = new File(langSpecDirStr);
        setLanguageSpecification(langSpecDir);
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

    public boolean isJson() {
        return json;
    }

    public void setEvents(boolean events) {
        this.events = events;
    }

    public void setImplMode(boolean implMode) {
        this.implMode = implMode;
    }

    public boolean hasImplMode() {
        return implMode;
    }

    public void setDefs(boolean b) {
        this.defs = b;
    }

    public boolean hasDefs() {
        return defs;
    }

    /**
     * @return the concreteClassesPrefix
     */
    public String getConcreteClassesPrefix() {
        return concreteClassesPrefix;
    }

    public boolean isTemplatedGenerator() {
        return concreteClassesPrefix != null;
    }

    /**
     * @param concreteClassesPrefix the concreteClassesPrefix to set
     */
    public void setConcreteClassesPrefix(String concreteClassesPrefix) {
        this.concreteClassesPrefix = concreteClassesPrefix;
    }

}
