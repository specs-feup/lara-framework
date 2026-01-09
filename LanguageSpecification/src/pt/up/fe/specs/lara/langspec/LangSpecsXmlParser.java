/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.langspec;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.EnumValue;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.TypeDef;
import org.lara.language.specification.exception.LanguageSpecificationException;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.xml.XmlDocument;
import pt.up.fe.specs.util.xml.XmlElement;

public class LangSpecsXmlParser {

    public static LanguageSpecification parse(InputStream joinPointModel, InputStream attributeModel,
            InputStream actionModel) {

        return parse(joinPointModel, attributeModel, actionModel, true);
    }

    public static LanguageSpecification parse(ResourceProvider joinPointModel, ResourceProvider attributeModel,
            ResourceProvider actionModel, boolean validate) {

        return parse(SpecsIo.resourceToStream(joinPointModel), SpecsIo.resourceToStream(attributeModel),
                SpecsIo.resourceToStream(actionModel), validate);
    }

    public static LanguageSpecification parse(InputStream joinPointModel, InputStream attributeModel,
            InputStream actionModel, boolean validate) {

        var jpSchema = validate ? SchemaResource.JOIN_POINT_SCHEMA.toStream() : null;
        var attrSchema = validate ? SchemaResource.ATTRIBUTE_SCHEMA.toStream() : null;
        var actionSchema = validate ? SchemaResource.ACTION_SCHEMA.toStream() : null;

        var joinPointModelNode = XmlDocument.newInstance(joinPointModel, jpSchema);
        var attributeModelNode = XmlDocument.newInstance(attributeModel, attrSchema);
        var actionModelNode = XmlDocument.newInstance(actionModel, actionSchema);

        // Setup global JoinPointClass
        LanguageSpecification langSpecV2 = new LanguageSpecification();
        JoinPointClass global = JoinPointClass.globalJoinPoint();
        langSpecV2.setGlobal(global);

        // Initialize types (typedef, enums), to have access to available names
        for (var type : attributeModelNode.getElementsByName("object")) {
            var typeDef = new TypeDef(type.getAttribute("name"));
            setOptional(type.getAttribute("tooltip"), typeDef::setToolTip);

            langSpecV2.add(typeDef);
        }

        for (var type : attributeModelNode.getElementsByName("enum")) {
            var enumDef = new EnumDef(type.getAttribute("name"));
            langSpecV2.add(enumDef);

            setOptional(type.getAttribute("tooltip"), enumDef::setToolTip);

            List<EnumValue> valuesList = toEnumValues(type.getElementsByName("value"), langSpecV2);
            enumDef.setValues(valuesList);
        }

        for (var type : attributeModelNode.getElementsByName("typedef")) {
            var typeDef = new TypeDef(type.getAttribute("name"));

            // Add typedef attributes
            var typedefAttrs = convertAttributes(type.getElementsByName("attribute"), langSpecV2);
            typeDef.setFields(typedefAttrs);

            setOptional(type.getAttribute("tooltip"), typeDef::setToolTip);

            langSpecV2.add(typeDef);
        }

        List<JoinPointClass> jps = new ArrayList<>();
        for (var jpNode : joinPointModelNode.getElementsByName("joinpoint")) {
            var jp = new JoinPointClass(jpNode.getAttribute("class"));
            setOptional(jpNode.getAttribute("tooltip"), jp::setToolTip);
            jps.add(jp);
        }
        Collections.sort(jps);
        jps.forEach(langSpecV2::add);

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

            for (String className : classNames.split(",")) {
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
                JoinPointClass parent = langSpecV2.getJoinPoint(extendsType);
                if (parent == null) {
                    throw new LanguageSpecificationException(
                            "Unknown extends target '" + extendsType + "' for join point '" + jpClass + "'");
                }

                validateNoInheritanceCycle(jp, parent);
                jp.setExtend(parent);
            } else {
                jp.setExtend(global);
            }

            // Obtain attribute nodes from artifacts
            List<XmlElement> artifactNodes = attributeModelNode.getElementsByName("artifact").stream()
                    .filter(attribute -> attribute.getAttribute("class").equals(jpClass))
                    .toList();

            var attributeNodes = artifactNodes.stream()
                    .flatMap(art -> art.getElementsByName("attribute").stream())
                    .collect(Collectors.toList());

            // Add attributes
            jp.setAttributes(convertAttributes(attributeNodes, langSpecV2));

            // Add actions
            jp.setActions(convertActions(langSpecV2, joinPointActions.get(jpClass), jpClass));
        }

        // Set default attributes
        for (var artifact : attributeModelNode.getElementsByName("artifact")) {
            var defaultValue = artifact.getAttribute("default");
            if (defaultValue.isEmpty()) {
                continue;
            }

            var artifactJp = langSpecV2.getJoinPoint(artifact.getAttribute("class"));

            if (artifactJp == null) {
                SpecsLogs.info("Artifact without join point: " + artifact.getAttribute("class"));
                continue;
            }

            artifactJp.setDefaultAttribute(defaultValue);
        }

        return langSpecV2;

    }

    private static void setOptional(String attributeValue, Consumer<String> setter) {
        if (attributeValue.isBlank()) {
            return;
        }

        setter.accept(attributeValue);
    }

    private static List<EnumValue> toEnumValues(List<XmlElement> enumValues, LanguageSpecification langSpecV2) {
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
            LanguageSpecification langSpecV2, JoinPointClass global, List<XmlElement> globalActionNodes) {

        var globalAttributes = artifacts.getElementByName("global");
        if (globalAttributes != null) {
            convertAttributes(globalAttributes.getElementsByName("attribute"), langSpecV2)
                    .forEach(global::add);
        }

        convertActions(langSpecV2, globalActionNodes, JoinPointClass.getGlobalName())
                .forEach(global::add);
    }

    private static List<Attribute> convertAttributes(List<XmlElement> attributeNodes,
            LanguageSpecification langSpec) {

        List<Attribute> attributes = new ArrayList<>();
        for (var attributeNode : attributeNodes) {

            Attribute newAttribute = getAttribute(attributeNode, langSpec);
            attributes.add(newAttribute);

        }

        Collections.sort(attributes);

        return attributes;
    }

    private static Attribute getAttribute(XmlElement attributeNode, LanguageSpecification langSpec) {
        String type = getType(attributeNode);
        Attribute newAttribute = new Attribute(langSpec.getType(type), attributeNode.getAttribute("name"));
        setOptional(attributeNode.getAttribute("tooltip"), newAttribute::setToolTip);

        var parameterNodes = attributeNode.getElementsByName("parameter");
        for (var parameterNode : parameterNodes) {
            newAttribute.addParameter(langSpec.getType(getType(parameterNode)),
                    parameterNode.getAttribute("name"), parameterNode.getAttribute("default"));
        }

        return newAttribute;
    }

    private static String getType(XmlElement node) {
        // Default type is "void"
        return node.getAttribute("type", "void");
    }

    private static List<Action> convertActions(LanguageSpecification langSpecV2,
            List<XmlElement> actionNodes, String ownerName) {

        if (actionNodes == null || actionNodes.isEmpty()) {
            return new ArrayList<>();
        }

        List<Action> newActions = new ArrayList<>();
        var seenSignatures = new HashSet<String>();
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
            var signature = newAction.getSignature();
            if (!seenSignatures.add(signature)) {
                throw new LanguageSpecificationException(
                        "Duplicate action signature '" + signature + "' for join point '" + ownerName + "'");
            }
            newActions.add(newAction);
        }

        Collections.sort(newActions);
        return newActions;
    }

    private static void validateNoInheritanceCycle(JoinPointClass child, JoinPointClass parent) {
        if (parent == null) {
            return;
        }

        JoinPointClass current = parent;
        while (current != null) {
            if (current == child) {
                throw new LanguageSpecificationException(
                        "Inheritance cycle detected for join point '" + child.getName() + "'");
            }

            current = current.getExtend().orElse(null);
        }
    }
}
