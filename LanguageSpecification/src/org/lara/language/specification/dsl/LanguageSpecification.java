/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.dsl;

import org.lara.language.specification.dsl.types.*;
import org.lara.language.specification.exception.LanguageSpecificationException;
import pt.up.fe.specs.lara.langspec.LangSpecsXmlParser;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.ResourceProvider;
import tdrc.utils.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * New version of the LanguageSpecification.
 *
 * @author jbispo
 */
public class LanguageSpecification {

    private static final String JOIN_POINTS_FILENAME = "joinPointModel.xml";
    private static final String ATTRIBUTES_FILENAME = "artifacts.xml";
    private static final String ACTIONS_FILENAME = "actionModel.xml";

    public static String getJoinPointsFilename() {
        return JOIN_POINTS_FILENAME;
    }

    public static String getAttributesFilename() {
        return ATTRIBUTES_FILENAME;
    }

    public static String getActionsFilename() {
        return ACTIONS_FILENAME;
    }

    private static final String BASE_JOINPOINT_CLASS = "joinpoint";

    public static String getBaseJoinpointClass() {
        return BASE_JOINPOINT_CLASS;
    }

    private JoinPointClass root;
    private String rootAlias;
    private Map<String, JoinPointClass> joinPoints;
    private JoinPointClass global;
    private Map<String, TypeDef> typeDefs;
    private Map<String, EnumDef> enumDefs;

    private final Lazy<Set<String>> availableAttributes;
    private final Lazy<Set<String>> availableActions;

    public LanguageSpecification(JoinPointClass root, String rootAlias) {
        super();
        this.root = root;
        this.rootAlias = rootAlias == null ? "" : rootAlias;
        // this.rootAlias = rootAlias;
        joinPoints = new LinkedHashMap<>();
        typeDefs = new LinkedHashMap<>();
        setEnumDefs(new LinkedHashMap<>());

        availableAttributes = Lazy.newInstance(this::buildAvailableAttributes);
        availableActions = Lazy.newInstance(this::buildAvailableActions);
    }

    public LanguageSpecification() {
        this(null, null);
    }

    /**
     * Creates a language specification instance with the files contained in the
     * folder 'specDir'
     *
     * @param specDir the source folder of the language specification, should
     *                include 3 files:
     *                {@value #JOIN_POINTS_FILENAME}, {@value #ATTRIBUTES_FILENAME}
     *                and {@value #ACTIONS_FILENAME}
     */
    public static LanguageSpecification newInstance(File specDir) {

        if (!specDir.exists() || !specDir.isDirectory()) {
            throw new RuntimeException("Language Specification directory is invalid: " + specDir.getAbsolutePath());
        }

        try {
            // Language specification files
            final File jpModelFile = SpecsIo.existingFile(specDir, JOIN_POINTS_FILENAME);
            final File artifactsFile = SpecsIo.existingFile(specDir, ATTRIBUTES_FILENAME);
            final File actionModelFile = SpecsIo.existingFile(specDir, ACTIONS_FILENAME);

            return LangSpecsXmlParser.parse(SpecsIo.toInputStream(jpModelFile), SpecsIo.toInputStream(artifactsFile),
                    SpecsIo.toInputStream(actionModelFile), true);
        } catch (final Exception e) {
            throw new LanguageSpecificationException(
                    "Could not create a Language Specification from folder '" + specDir + "'", e);
        }
    }

    public static LanguageSpecification newInstance(InputStream joinPointModel, InputStream attributeModel,
            InputStream actionModel) {

        return LangSpecsXmlParser.parse(joinPointModel, attributeModel, actionModel, true);
    }

    public static LanguageSpecification newInstance(ResourceProvider joinPointModel, ResourceProvider attributeModel,
            ResourceProvider actionModel) {

        return LangSpecsXmlParser.parse(SpecsIo.resourceToStream(joinPointModel),
                SpecsIo.resourceToStream(attributeModel),
                SpecsIo.resourceToStream(actionModel), true);
    }

    private Set<String> buildAvailableAttributes() {
        Set<String> availableAttributes = new HashSet<>();

        for (var jp : getJoinPoints().values()) {
            jp.getAttributesSelf().stream()
                    .map(Attribute::getName)
                    .forEach(availableAttributes::add);
        }

        // Add global attributes
        global.getAttributesSelf().stream()
                .map(Attribute::getName)
                .forEach(availableAttributes::add);

        return availableAttributes;
    }

    private Set<String> buildAvailableActions() {
        Set<String> availableActions = new HashSet<>();

        for (var jp : getJoinPoints().values()) {
            jp.getActionsSelf().stream()
                    .map(Action::getName)
                    .forEach(availableActions::add);
        }

        // Add global attributes
        global.getActionsSelf().stream()
                .map(Action::getName)
                .forEach(availableActions::add);

        return availableActions;
    }

    public void add(JoinPointClass node) {
        joinPoints.put(node.getName(), node);
    }

    public void add(TypeDef type) {
        typeDefs.put(type.getName(), type);
    }

    public void add(EnumDef type) {
        enumDefs.put(type.getName(), type);
    }

    public JoinPointClass getJoinPoint(String name) {
        // if (name.equals("joinpoint")) {
        if (getBaseJoinpointClass().equals(name)) {
            return global;
        }
        return joinPoints.get(name);
    }

    /**
     * @return true if the given name corresponds to an existing join point (not
     *         considering alias)
     */
    public boolean hasJoinPoint(String name) {
        // Join Points
        if (joinPoints.containsKey(name)) {
            return true;
        }

        // Global
        return getBaseJoinpointClass().equals(name);
    }

    /**
     * @return true if the given name is a valid join point (considering alias)
     */
    public boolean hasJoinPointName(String name) {
        return hasJoinPoint(name);
    }

    public IType getType(String type) {

        if (type.startsWith("{")) {// An enum
            return new LiteralEnum(type, type);
        }

        if (type.endsWith("[]")) {
            int arrayDimPos = type.indexOf("[");
            String arrayDimString = type.substring(arrayDimPos);
            type = type.substring(0, arrayDimPos).trim();
            int arrayDimension = 0;
            do {
                arrayDimString = arrayDimString.replaceFirst("\\[\\]", "");
                arrayDimension++;
            } while (arrayDimString.contains("[]"));

            if (!arrayDimString.trim().isEmpty()) {
                throw new RuntimeException("Bad format for array definition. Bad characters: " + arrayDimString);
            }
            IType baseType = getType(type);
            return new ArrayType(baseType, arrayDimension);
        }

        if (type.equalsIgnoreCase("template")) {
            return PrimitiveClasses.STRING;
        }

        if (type.equalsIgnoreCase("joinpoint")) {
            return new JPType(global);
        }

        if (Primitive.contains(type)) {
            return Primitive.get(type);
        }
        if (PrimitiveClasses.contains(StringUtils.firstCharToUpper(type))) {
            return PrimitiveClasses.get(type);
        }
        if (typeDefs.containsKey(type)) {
            return typeDefs.get(type);
        }
        if (enumDefs.containsKey(type)) {
            return enumDefs.get(type);
        }

        if (joinPoints.containsKey(type)) {
            return new JPType(joinPoints.get(type));
        }

        throw new RuntimeException("Type given does not exist: " + type);

        // return null;
    }

    public JoinPointClass getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = joinPoints.get(root);
    }

    public void setRoot(JoinPointClass root) {
        this.root = root;
    }

    public String getRootAlias() {
        if (rootAlias.isEmpty()) {
            return root.getName();
        }

        return rootAlias;
    }

    public void setRootAlias(String rootAlias) {
        this.rootAlias = rootAlias;
    }

    public Map<String, JoinPointClass> getJoinPoints() {
        return joinPoints;
    }

    /**
     * @return a list of all join points, including the global join point
     */
    public List<JoinPointClass> getAllJoinPoints() {
        return SpecsCollections.concat(global, joinPoints.values());
    }

    /**
     * @return a list of all declared join points, excluding the global join point
     */
    public List<JoinPointClass> getDeclaredJoinPoints() {
        return new ArrayList<>(joinPoints.values());
    }

    public void setJoinPoints(Map<String, JoinPointClass> joinpoints) {
        joinPoints = joinpoints;
    }

    public JoinPointClass getGlobal() {
        return global;
    }

    public void setGlobal(JoinPointClass global) {
        this.global = global;
    }

    public Map<String, TypeDef> getTypeDefs() {
        return Collections.unmodifiableMap(typeDefs);
    }

    public boolean hasTypeDef(String name) {
        return typeDefs.containsKey(name);
    }

    public boolean hasEnumDef(String name) {
        return enumDefs.containsKey(name);
    }

    public void setTypeDefs(Map<String, TypeDef> typeDefs) {
        this.typeDefs = typeDefs;
    }

    @Override
    public String toString() {
        String alias = rootAlias.isEmpty() ? "" : (" as " + rootAlias);
        StringBuilder string = new StringBuilder("root " + root.getName() + alias + "\n");

        string.append(global.toDSLString());

        for (JoinPointClass joinPoint : joinPoints.values()) {
            string.append("\n").append(joinPoint.toDSLString());
        }
        string.append("\n");
        for (TypeDef type : typeDefs.values()) {
            string.append("\n").append(type.toDSLString());
        }
        return string.toString();
    }

    public Map<String, EnumDef> getEnumDefs() {
        return Collections.unmodifiableMap(enumDefs);
    }

    public void setEnumDefs(Map<String, EnumDef> enumDefs) {
        this.enumDefs = enumDefs;
    }

    public boolean hasAttribute(String name) {
        return availableAttributes.get().contains(name);
    }

    public boolean hasAction(String name) {
        return availableActions.get().contains(name);
    }

    /**
     * @return the actions with the given name. Since overloading is supported,
     *         several actions can have the same name
     */
    public List<Action> getAction(String name) {
        return getAllJoinPoints().stream()
                .map(jp -> jp.getActionSelf(name))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * @return all the actions in the join point model
     */
    public List<Action> getAllActions() {
        return getAllJoinPoints().stream()
                .map(JoinPointClass::getActions)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * @return the attributes with the given name. Since overloading is supported,
     *         several attributes can have the same
     *         name
     */
    public List<Attribute> getAttribute(String name) {
        return getAllJoinPoints().stream()
                // .map(jp -> jp.getAttribute(name))
                .map(jp -> jp.getAttributeSelf(name))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Verify if the given Join point is a super type of any other Join Point
     * <p>
     * TODO: Could be more efficient (e.g., using a tree to represent the hierarchy)
     *
     */
    public boolean isSuper(JoinPointClass joinPoint) {
        for (var jp : getJoinPoints().values()) {

            if (jp.getExtend().map(extend -> extend.equals(joinPoint)).orElse(false)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Builds a hierarchy diagram in DOT format.
     *
     * @return a string with the Language Specification hierarchy diagram in DOT
     *         format
     */
    public String toHierarchyDiagram() {
        return toHierarchyDiagram("");
    }

    /**
     * Builds a hierarchy diagram in DOT format.
     *
     * @param langSpecName the name of the language specification.
     * @return a string with the Language Specification hierarchy diagram in DOT
     *         format
     */
    public String toHierarchyDiagram(String langSpecName) {

        langSpecName = langSpecName == null ? "" : langSpecName;
        langSpecName = langSpecName.isBlank() ? langSpecName : langSpecName + "_";

        var dot = new StringBuilder();

        dot.append("digraph ").append(langSpecName).append("join_point_hierarchy {\n")
                .append("node [color=lightblue2, style=filled];\n")
                .append("rankdir=\"RL\"\n")
                .append("node [fontsize=10, shape=box, height=0.25]\n")
                .append("edge [fontsize=10]\n");
        for (var jp : getAllJoinPoints()) {
            // "Invert" arrow direction
            jp.getExtend().map(parent -> dot.append("\"").append(jp.getName()).append("\"->\"").append(parent.getName())
                    .append("\"\n"));
        }
        dot.append("}\n");

        return dot.toString();
    }
}
