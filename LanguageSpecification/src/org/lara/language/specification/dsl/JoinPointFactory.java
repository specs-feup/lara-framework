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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.ActionModel;
import org.lara.language.specification.artifactsmodel.ArtifactsModel;
import org.lara.language.specification.artifactsmodel.schema.Global;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.EnumValue;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.dsl.types.TypeDef;
import org.lara.language.specification.joinpointmodel.JoinPointModel;
import org.lara.language.specification.joinpointmodel.schema.GlobalJoinPoints;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;

public class JoinPointFactory {

    public static LanguageSpecificationV2 fromOld(LanguageSpecification langSpec) {

        JoinPointModel jpModel = langSpec.getJpModel();
        ArtifactsModel artifacts = langSpec.getArtifacts();
        ActionModel actionModel = langSpec.getActionModel();

        LanguageSpecificationV2 langSpecV2 = new LanguageSpecificationV2();
        JoinPointClass global = JoinPointClass.globalJoinPoint(langSpecV2);
        langSpecV2.setGlobal(global);

        /**
         * First create the join points and typedefs so next we can see if they exist and get the reference
         */
        List<org.lara.language.specification.artifactsmodel.schema.TypeDef> objects = artifacts.getTypeDefs();
        for (org.lara.language.specification.artifactsmodel.schema.TypeDef newObject : objects) {
            langSpecV2.add(new TypeDef(newObject.getName()));
        }

        List<org.lara.language.specification.artifactsmodel.schema.EnumDef> enumDefs = artifacts.getEnumDefs();
        for (org.lara.language.specification.artifactsmodel.schema.EnumDef enumDef : enumDefs) {
            EnumDef type = new EnumDef(enumDef.getName());
            langSpecV2.add(type);

            type.setToolTip(enumDef.getTooltip());
            List<EnumValue> valuesList = convertEnumValues(enumDef.getValue(), langSpecV2);
            type.setValues(valuesList);
        }

        List<JoinPointType> joinPoints = new ArrayList<>(jpModel.getJoinPointList().getJoinpoint());
        Collections.sort(joinPoints);
        // for (JoinPointType jpType : jpModel.getJoinPointList().getJoinpoint()) {
        for (JoinPointType jpType : joinPoints) {
            JoinPointClass newJPClass = new JoinPointClass(jpType.getClazz(), langSpecV2);
            newJPClass.setToolTip(jpType.getTooltip());
            langSpecV2.add(newJPClass);
        }

        langSpecV2.setRoot(jpModel.getRoot().getClazz());
        langSpecV2.setRootAlias(jpModel.getJoinPointList().getRootAlias());
        // langSpecV2.setRoot(jpModel.getJoinPointList().getRootAlias());

        populateGlobal(jpModel, artifacts, actionModel, langSpecV2, global);

        for (org.lara.language.specification.artifactsmodel.schema.TypeDef newObject : objects) {

            TypeDef typeDef = langSpecV2.getTypeDefs().get(newObject.getName());
            typeDef.setToolTip(newObject.getTooltip());
            List<Attribute> artifactsList = convertAttributes(newObject.getAttribute(), langSpecV2);
            typeDef.setFields(artifactsList);
        }

        for (JoinPointType jpType : jpModel.getJoinPointList().getJoinpoint()) {
            String clazz = jpType.getClazz();
            JoinPointClass jpNode = langSpecV2.getJoinPoint(clazz);
            JoinPointType extendsJP = (JoinPointType) jpType.getExtends();
            if (!jpType.equals(extendsJP)) {
                jpNode.setExtend(langSpecV2.getJoinPoint(extendsJP.getClazz()));
            } else {
                jpNode.setExtend(global);
            }

            // Add attributes
            jpNode.setAttributes(convertAttributes(artifacts.getAttributes(clazz), langSpecV2));

            // Add selects
            jpNode.setSelects(convertSelects(langSpecV2, jpType.getSelect()));

            // Add actions
            jpNode.setActions(convertActions(langSpecV2, actionModel.getJoinPointOwnActions(jpType.getClazz())));

            var artifactList = artifacts.getArtifactsList();
            if (artifactList != null) {
                for (var artifact : artifactList.getArtifact()) {
                    if (artifact.getDefault() != null) {
                        // Get corresponding join point and set default
                        langSpecV2.getJoinPoint(artifact.getClazz()).setDefaultAttribute(artifact.getDefault());
                    }
                }
            }

        }

        // Add default global attributes (e.g., joinPointType, instanceOf)
        addDefaultGlobalAttributes(langSpecV2);

        return langSpecV2;

    }

    private static void addDefaultGlobalAttributes(LanguageSpecificationV2 langSpec) {
        var joinPointType = new Attribute(PrimitiveClasses.STRING, "joinPointType");
        joinPointType.setDefault(true);
        joinPointType.setToolTip("a String with the type of the join point");
        langSpec.getGlobal().add(joinPointType);

        var instanceOf = new Attribute(PrimitiveClasses.BOOLEAN, "instanceOf",
                Arrays.asList(new Declaration(PrimitiveClasses.STRING, "name")));
        instanceOf.setDefault(true);
        instanceOf.setToolTip("true if the current join point is an instance of the given type");
        langSpec.getGlobal().add(instanceOf);

    }

    private static List<Action> convertActions(LanguageSpecificationV2 langSpecV2,
            List<org.lara.language.specification.actionsmodel.schema.Action> actions) {
        List<Action> newActions = new ArrayList<>();
        for (org.lara.language.specification.actionsmodel.schema.Action action : actions) {

            List<org.lara.language.specification.actionsmodel.schema.Parameter> parameters = action
                    .getParameter();

            List<Parameter> declarations = new ArrayList<>();
            if (parameters != null && !parameters.isEmpty()) {
                for (org.lara.language.specification.actionsmodel.schema.Parameter param : parameters) {
                    IType type = langSpecV2.getType(param.getType());
                    String defaultValue = param.getDefault();
                    if (defaultValue == null) {
                        defaultValue = "";
                    }
                    declarations.add(new Parameter(type, param.getName(), defaultValue));
                }
            }

            Action newAction = new Action(langSpecV2.getType(action.getReturn()), action.getName(), declarations);
            newAction.setToolTip(action.getTooltip());
            newActions.add(newAction);
        }

        Collections.sort(newActions);
        return newActions;
    }

    private static void populateGlobal(JoinPointModel jpModel, ArtifactsModel artifacts, ActionModel actionModel,
            LanguageSpecificationV2 langSpecV2,
            JoinPointClass global) {

        GlobalJoinPoints globalSelects = jpModel.getJoinPointList().getGlobal();
        if (globalSelects != null) {
            convertSelects(langSpecV2, globalSelects.getSelect())
                    .forEach(global::add);
            // global.setSelects();
        }

        Global globalAttributes = artifacts.getArtifactsList().getGlobal();

        if (globalAttributes != null) {
            convertAttributes(globalAttributes.getAttribute(), langSpecV2)
                    .forEach(global::add);
            // global.setAttributes();
        }

        // global.getActions().addAll(convertActions(langSpecV2, actionModel.getActionsForAll()));
        convertActions(langSpecV2, actionModel.getActionsForAll()).stream()
                .forEach(global::add);
    }

    private static List<Select> convertSelects(LanguageSpecificationV2 langSpecV2,
            List<org.lara.language.specification.joinpointmodel.schema.Select> globalSelects) {
        List<Select> selects = new ArrayList<>();
        if (globalSelects != null) {
            for (org.lara.language.specification.joinpointmodel.schema.Select select : globalSelects) {

                JoinPointType selectJPT = select.getClazz();
                JoinPointClass selectJP = langSpecV2.getJoinPoint(selectJPT.getClazz());

                // Validation: selectJP must not be null
                if (selectJP == null) {
                    throw new RuntimeException(
                            "Select has invalid join point name as class: " + select.getClazz().getClazz());
                }

                String alias = select.getAlias();
                alias = alias.equals(selectJPT.getClazz()) ? "" : alias;
                Select newSelect = new Select(selectJP, alias);
                newSelect.setToolTip(select.getTooltip());
                selects.add(newSelect);
            }
        }

        // Collections.sort(selects);

        return selects;
    }

    private static List<Attribute> convertAttributes(
            List<org.lara.language.specification.artifactsmodel.schema.Attribute> artifact,
            LanguageSpecificationV2 langSpec) {
        List<Attribute> attributes = new ArrayList<>();
        if (artifact != null) {
            for (org.lara.language.specification.artifactsmodel.schema.Attribute attribute : artifact) {

                Attribute newAttribute = getAttribute(attribute, langSpec);
                attributes.add(newAttribute);

            }
        }

        Collections.sort(attributes);

        return attributes;
    }

    private static List<EnumValue> convertEnumValues(
            List<org.lara.language.specification.artifactsmodel.schema.EnumValue> enumValues,
            LanguageSpecificationV2 langSpec) {
        List<EnumValue> attributes = new ArrayList<>();
        if (enumValues != null) {
            for (org.lara.language.specification.artifactsmodel.schema.EnumValue value : enumValues) {

                EnumValue newAttribute = new EnumValue(value.getName(), value.getString());
                attributes.add(newAttribute);
            }
        }

        Collections.sort(attributes);

        return attributes;
    }

    private static Attribute getAttribute(
            org.lara.language.specification.artifactsmodel.schema.Attribute attribute,
            LanguageSpecificationV2 langSpec) {
        String type = attribute.getType();
        Attribute newAttribute = new Attribute(langSpec.getType(type), attribute.getName());
        newAttribute.setToolTip(attribute.getTooltip());
        List<org.lara.language.specification.artifactsmodel.schema.Parameter> parameters = attribute.getParameter();
        if (parameters != null && !parameters.isEmpty()) {
            parameters.forEach(p -> newAttribute.addParameter(langSpec.getType(p.getType()), p.getName()));
        }
        return newAttribute;
    }
}
