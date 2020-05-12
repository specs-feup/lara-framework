/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.dsl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.Primitive;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.dsl.types.TypeDef;

import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.lazy.Lazy;
import tdrc.utils.StringUtils;

/**
 * New version of the LanguageSpecification.
 * 
 * @author jbispo
 *
 */
public class LanguageSpecificationV2 {

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

    public LanguageSpecificationV2(JoinPointClass root, String rootAlias) {
        super();
        this.root = root;
        this.rootAlias = rootAlias == null ? "" : rootAlias;
        joinPoints = new LinkedHashMap<>();
        typeDefs = new LinkedHashMap<>();
        setEnumDefs(new LinkedHashMap<>());

        availableAttributes = Lazy.newInstance(this::buildAvailableAttributes);
        availableActions = Lazy.newInstance(this::buildAvailableActions);
    }

    public LanguageSpecificationV2() {
        this(null, null);
    }

    private Set<String> buildAvailableAttributes() {
        Set<String> availableAttributes = new HashSet<>();

        for (var jp : getJoinPoints().values()) {
            jp.getAttributes().stream()
                    .map(Attribute::getName)
                    .forEach(availableAttributes::add);
        }

        // Add global attributes
        global.getAttributes().stream()
                .map(Attribute::getName)
                .forEach(availableAttributes::add);

        return availableAttributes;
    }

    private Set<String> buildAvailableActions() {
        Set<String> availableActions = new HashSet<>();

        for (var jp : getJoinPoints().values()) {
            jp.getActions().stream()
                    .map(Action::getName)
                    .forEach(availableActions::add);
        }

        // Add global attributes
        global.getActions().stream()
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
     * 
     * @param name
     * @return true if the given name corresponds to an existing join point (not considering alias)
     */
    public boolean hasJoinPoint(String name) {
        // Join Points
        if (joinPoints.containsKey(name)) {
            return true;
        }

        // Global
        if (getBaseJoinpointClass().equals(name)) {
            return true;
        }

        return false;
    }

    /**
     * 
     * @param name
     * @return true if the given name is a valid join point (considering alias)
     */
    public boolean hasJoinPointName(String name) {
        if (hasJoinPoint(name)) {
            return true;
        }

        // Alias
        for (var jp : joinPoints.values()) {
            if (jp.hasSelect(name)) {
                return true;
            }
        }

        return false;
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

        if (type.toLowerCase().equals("template")) {
            return PrimitiveClasses.STRING;
        }

        if (type.toLowerCase().equals("joinpoint")) {
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
        return rootAlias;
    }

    public void setRootAlias(String rootAlias) {
        this.rootAlias = rootAlias;
    }

    public Map<String, JoinPointClass> getJoinPoints() {
        return joinPoints;
    }

    /**
     * 
     * @return a list of all join points, including the global join point
     */
    public List<JoinPointClass> getAllJoinPoints() {
        return SpecsCollections.concat(global, joinPoints.values());
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

    public void setTypeDefs(Map<String, TypeDef> typeDefs) {
        this.typeDefs = typeDefs;
    }

    @Override
    public String toString() {
        String alias = rootAlias.isEmpty() ? "" : (" as " + rootAlias);
        String string = "root " + root.getName() + alias + "\n";

        string += global.toDSLString();

        for (JoinPointClass joinPoint : joinPoints.values()) {
            string += "\n" + joinPoint.toDSLString();
        }
        string += "\n";
        for (TypeDef type : typeDefs.values()) {
            string += "\n" + type.toDSLString();
        }
        return string;
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
     * 
     * @param name
     * @return the actions with the given name. Since overloading is supported, several actions can have the same name
     */
    public List<Action> getAction(String name) {
        return getAllJoinPoints().stream()
                .map(jp -> jp.getAction(name))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 
     * @param name
     * @return the attributes with the given name. Since overloading is supported, several attributes can have the same
     *         name
     */
    public List<Attribute> getAttributes(String name) {
        return getAllJoinPoints().stream()
                .map(jp -> jp.getAttribute(name))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
