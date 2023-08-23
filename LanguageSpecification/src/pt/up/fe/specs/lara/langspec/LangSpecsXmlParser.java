/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.langspec;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.Declaration;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.Select;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.EnumValue;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.dsl.types.TypeDef;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.xml.XmlDocument;
import pt.up.fe.specs.util.xml.XmlElement;

public class LangSpecsXmlParser {

    public static LanguageSpecificationV2 parse(InputStream joinPointModel, InputStream attributeModel,
            InputStream actionModel) {

        return parse(joinPointModel, attributeModel, actionModel, true);
    }

    public static LanguageSpecificationV2 parse(ResourceProvider joinPointModel, ResourceProvider attributeModel,
            ResourceProvider actionModel, boolean validate) {

        return parse(SpecsIo.resourceToStream(joinPointModel), SpecsIo.resourceToStream(attributeModel),
                SpecsIo.resourceToStream(actionModel), validate);
    }

    public static LanguageSpecificationV2 parse(InputStream joinPointModel, InputStream attributeModel,
            InputStream actionModel, boolean validate) {

        // System.out.println("JP SCHEMA: " + SchemaResource.JOIN_POINT_SCHEMA.read());
        // System.out.println("JP SCHEMA: " + SchemaResource.JOIN_POINT_SCHEMA.getResource());
        var jpSchema = validate ? SchemaResource.JOIN_POINT_SCHEMA.toStream() : null;
        var attrSchema = validate ? SchemaResource.ATTRIBUTE_SCHEMA.toStream() : null;
        var actionSchema = validate ? SchemaResource.ACTION_SCHEMA.toStream() : null;

        var joinPointModelNode = XmlDocument.newInstance(joinPointModel, jpSchema);
        var attributeModelNode = XmlDocument.newInstance(attributeModel, attrSchema);
        var actionModelNode = XmlDocument.newInstance(actionModel, actionSchema);

        // Setup global JoinPointClass
        LanguageSpecificationV2 langSpecV2 = new LanguageSpecificationV2();
        JoinPointClass global = JoinPointClass.globalJoinPoint(langSpecV2);
        langSpecV2.setGlobal(global);

        // Initialize types (typedef, enums), to have access to available names
        for (var type : attributeModelNode.getElementsByName("object")) {
            var typeDef = new TypeDef(type.getAttribute("name"));
            langSpecV2.add(typeDef);

            setOptional(type.getAttribute("tooltip"), typeDef::setToolTip);
        }

        for (var type : attributeModelNode.getElementsByName("enum")) {
            var enumDef = new EnumDef(type.getAttribute("name"));
            langSpecV2.add(enumDef);

            setOptional(type.getAttribute("tooltip"), enumDef::setToolTip);

            List<EnumValue> valuesList = toEnumValues(type.getElementsByName("value"), langSpecV2);
            enumDef.setValues(valuesList);
        }

        List<JoinPointClass> jps = new ArrayList<>();
        for (var jpNode : joinPointModelNode.getElementsByName("joinpoint")) {
            var jp = new JoinPointClass(jpNode.getAttribute("class"), langSpecV2);
            setOptional(jpNode.getAttribute("tooltip"), jp::setToolTip);
            jps.add(jp);
        }
        Collections.sort(jps);
        jps.stream().forEach(langSpecV2::add);

        var joinpoints = joinPointModelNode.getElementsByName("joinpoints").get(0);
        langSpecV2.setRoot(joinpoints.getAttribute("root_class"));
        setOptional(joinpoints.getAttribute("root_alias"), langSpecV2::setRootAlias);

        // Map of actions according to class
        MultiMap<String, XmlElement> joinPointActions = new MultiMap<>();
        List<XmlElement> globalActions = new ArrayList<>();
        for (var actionNode : actionModelNode.getElementsByName("action")) {
            var classNames = actionNode.getAttribute("class");

            // Global actions do not have a class value, or its value is '*'
            if (classNames.isEmpty() || classNames.equals("*")) {
                globalActions.add(actionNode);
                continue;
            }
            // System.out.println("CLASS NAMES: " + classNames);
            for (String className : classNames.split(",")) {
                // System.out.println("NAME: " + className);
                joinPointActions.add(className.strip(), actionNode);
            }
        }

        populateGlobal(joinPointModelNode, attributeModelNode, actionModelNode, langSpecV2, global, globalActions);

        // Populate TypeDef
        for (var typeNode : attributeModelNode.getElementsByName("object")) {
            TypeDef typeDef = langSpecV2.getTypeDefs().get(typeNode.getAttribute("name"));
            List<Attribute> attributesList = convertAttributes(typeNode.getElementsByName("attribute"), langSpecV2);
            typeDef.setFields(attributesList);
        }

        for (var jpNode : joinPointModelNode.getElementsByName("joinpoint")) {
            String jpClass = jpNode.getAttribute("class");

            JoinPointClass jp = langSpecV2.getJoinPoint(jpClass);
            String extendsType = jpNode.getAttribute("extends");
            if (!extendsType.isEmpty()) {
                jp.setExtend(langSpecV2.getJoinPoint(extendsType));
            } else {
                jp.setExtend(global);
            }

            // Obtain attribute nodes from artifacts
            List<XmlElement> artifactNodes = attributeModelNode.getElementsByName("artifact").stream()
                    .filter(attribute -> attribute.getAttribute("class").equals(jpClass))
                    .collect(Collectors.toList());

            var attributeNodes = artifactNodes.stream()
                    .flatMap(art -> art.getElementsByName("attribute").stream())
                    .collect(Collectors.toList());

            // Add attributes
            jp.setAttributes(convertAttributes(attributeNodes, langSpecV2));

            // Add selects
            jp.setSelects(convertSelects(langSpecV2, jpNode.getElementsByName("select")));

            // Add actions
            jp.setActions(convertActions(langSpecV2, joinPointActions.get(jpClass)));

            // Set default attributes
            for (var artifact : attributeModelNode.getElementsByName("artifact")) {
                var defaultValue = artifact.getAttribute("default");
                if (defaultValue.isEmpty()) {
                    continue;
                }

                // Get corresponding join point and set default
                // System.out.println("ARTIFACT CLASS: " + artifact.getAttribute("class"));
                // System.out.println("JP: " + langSpecV2.getJoinPoint(artifact.getAttribute("class")));

                var artifactJp = langSpecV2.getJoinPoint(artifact.getAttribute("class"));

                if (artifactJp == null) {
                    SpecsLogs.info("Artifact without join point: " + artifact.getAttribute("class"));
                    continue;
                }

                artifactJp.setDefaultAttribute(defaultValue);
                // System.out.println("SETTING DEFAULT '" + defaultValue + "' for JP " +
                // artifact.getAttribute("class"));
            }
        }

        // Add default global attributes (e.g., joinPointType, instanceOf)
        addDefaultGlobalAttributes(langSpecV2);

        // System.out.println("JP: " + joinPointModelNode);
        // System.out.println("ATTR: " + attributeModelNode);
        // System.out.println("ACTIONS: " + actionModelNode);

        return langSpecV2;

    }

    private static void setOptional(String attributeValue, Consumer<String> setter) {
        if (attributeValue.isBlank()) {
            return;
        }

        setter.accept(attributeValue);
    }

    private static List<EnumValue> toEnumValues(List<XmlElement> enumValues, LanguageSpecificationV2 langSpecV2) {
        List<EnumValue> attributes = new ArrayList<>();

        for (var enumValue : enumValues) {
            EnumValue newAttribute = new EnumValue(enumValue.getAttribute("name"), null);
            setOptional(enumValue.getAttribute("string"), newAttribute::setString);
            attributes.add(newAttribute);
        }

        Collections.sort(attributes);

        return attributes;
    }

    private static void populateGlobal(XmlDocument jpModel, XmlDocument artifacts, XmlDocument actionModel,
            LanguageSpecificationV2 langSpecV2, JoinPointClass global, List<XmlElement> globalActionNodes) {

        /*
        GlobalJoinPoints globalSelects = jpModel.getJoinPointList().getGlobal();
        if (globalSelects != null) {
            convertSelects(langSpecV2, globalSelects.getSelect())
                    .forEach(global::add);
            // global.setSelects();
        }
        */

        var globalAttributes = artifacts.getElementByName("global");
        if (globalAttributes != null) {
            convertAttributes(globalAttributes.getElementsByName("attribute"), langSpecV2)
                    .forEach(global::add);
        }

        // Global actions either do not have 'class' set, or is set to '*'
        // List<XmlElement> globalActionNodes = actionModel.getElementsByName("action").stream()
        // .filter(node -> node.getAttribute("class").isBlank() || node.getAttribute("class").equals("*"))
        // .collect(Collectors.toList());

        // global.getActions().addAll(convertActions(langSpecV2, globalActionNodes));
        convertActions(langSpecV2, globalActionNodes).stream()
                .forEach(global::add);
    }

    private static List<Attribute> convertAttributes(List<XmlElement> attributeNodes,
            LanguageSpecificationV2 langSpec) {

        List<Attribute> attributes = new ArrayList<>();
        for (var attributeNode : attributeNodes) {

            Attribute newAttribute = getAttribute(attributeNode, langSpec);
            attributes.add(newAttribute);

        }

        Collections.sort(attributes);

        return attributes;
    }

    private static Attribute getAttribute(XmlElement attributeNode, LanguageSpecificationV2 langSpec) {
        String type = getType(attributeNode);
        Attribute newAttribute = new Attribute(langSpec.getType(type), attributeNode.getAttribute("name"));
        setOptional(attributeNode.getAttribute("tooltip"), newAttribute::setToolTip);

        var parameterNodes = attributeNode.getElementsByName("parameter");
        for (var parameterNode : parameterNodes) {
            newAttribute.addParameter(langSpec.getType(getType(parameterNode)),
                    parameterNode.getAttribute("name"));
        }

        return newAttribute;
    }

    private static String getType(XmlElement node) {
        // Default type is "void"
        return node.getAttribute("type", "void");
    }

    private static List<Action> convertActions(LanguageSpecificationV2 langSpecV2,
            List<XmlElement> actionNodes) {

        List<Action> newActions = new ArrayList<>();
        for (var action : actionNodes) {
            var parameterNodes = action.getElementsByName("parameter");
            List<Parameter> declarations = new ArrayList<>();
            for (var param : parameterNodes) {
                IType type = langSpecV2.getType(getType(param));
                String defaultValue = param.getAttribute("default");
                declarations.add(new Parameter(type, param.getAttribute("name"), defaultValue));
            }

            Action newAction = new Action(langSpecV2.getType(action.getAttribute("return", "void")),
                    action.getAttribute("name"), declarations);
            setOptional(action.getAttribute("tooltip"), newAction::setToolTip);
            newActions.add(newAction);
        }

        Collections.sort(newActions);
        return newActions;
    }

    private static List<Select> convertSelects(LanguageSpecificationV2 langSpecV2,
            List<XmlElement> selectNodes) {

        List<Select> selects = new ArrayList<>();

        for (var selectNode : selectNodes) {
            String selectClassName = selectNode.getAttribute("class");
            JoinPointClass selectJP = langSpecV2.getJoinPoint(selectClassName);

            // Validation: selectJP must not be null
            if (selectJP == null) {
                throw new RuntimeException("Select has invalid join point name as class: " + selectClassName);
            }

            String alias = selectNode.getAttribute("alias");
            alias = alias.equals(selectClassName) ? "" : alias; // Is this necessary?
            Select newSelect = new Select(selectJP, alias);
            newSelect.setToolTip(selectNode.getAttribute("tooltip"));
            selects.add(newSelect);
        }

        // Collections.sort(selects);

        return selects;
    }

    @Deprecated
    private static void addDefaultGlobalAttributes(LanguageSpecificationV2 langSpec) {
    }
}
