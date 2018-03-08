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

import java.util.LinkedHashMap;
import java.util.Map;

import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.Primitive;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.dsl.types.TypeDef;

import tdrc.utils.StringUtils;

public class LanguageSpecificationV2 {

    private JoinPointClass root;
    private String rootAlias;
    private Map<String, JoinPointClass> joinPoints;
    private JoinPointClass global;
    private Map<String, TypeDef> typeDefs;
    private Map<String, EnumDef> enumDefs;

    public LanguageSpecificationV2(JoinPointClass root, String rootAlias) {
        super();
        this.root = root;
        this.rootAlias = rootAlias == null ? "" : rootAlias;
        joinPoints = new LinkedHashMap<>();
        typeDefs = new LinkedHashMap<>();
        setEnumDefs(new LinkedHashMap<>());
    }

    public LanguageSpecificationV2() {
        this(null, null);
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
        if (name.equals("joinpoint")) {
            return global;
        }
        return joinPoints.get(name);
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
        return typeDefs;
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
        return enumDefs;
    }

    public void setEnumDefs(Map<String, EnumDef> enumDefs) {
        this.enumDefs = enumDefs;
    }
}
