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

package org.lara.language.specification.ast;

import java.util.Optional;

import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.Declaration;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.JoinPointFactory;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.Select;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.EnumValue;
import org.lara.language.specification.dsl.types.TypeDef;

public class NodeFactory {

    /**
     * @deprecated use LanguageSpecificationV2
     * @param languageSpecification
     * @return
     */
    @Deprecated
    public static RootNode toNode(LanguageSpecification languageSpecification) {
        return toNode(JoinPointFactory.fromOld(languageSpecification));
    }

    public static RootNode toNode(LanguageSpecificationV2 langSpec) {
        RootNode node = new RootNode(langSpec.getRoot().getName(), langSpec.getRootAlias());
        JoinPointNode child = toNode(langSpec.getGlobal());
        node.addChild(child);

        for (JoinPointClass joinPoint : langSpec.getJoinPoints().values()) {
            child = toNode(joinPoint);
            node.addChild(child);
        }

        for (TypeDef typeDef : langSpec.getTypeDefs().values()) {
            TypeDefNode tdNode = toNode(typeDef);
            node.addChild(tdNode);
        }
        for (EnumDef enumDef : langSpec.getEnumDefs().values()) {
            EnumDefNode tdNode = toNode(enumDef);
            node.addChild(tdNode);
        }

        return node;
    }

    private static TypeDefNode toNode(TypeDef typeDef) {
        TypeDefNode tdNode = new TypeDefNode(typeDef.getName());
        tdNode.getToolTip().ifPresent(tdNode::setToolTip);

        for (Attribute attr : typeDef.getFields()) {
            AttributeNode attrNode = toNode(attr);
            tdNode.addChild(attrNode);
        }
        return tdNode;
    }

    private static EnumDefNode toNode(EnumDef enumDef) {
        // for now they both have the same attributes (only name)
        EnumDefNode tdNode = new EnumDefNode(enumDef.getName());
        tdNode.getToolTip().ifPresent(tdNode::setToolTip);

        for (EnumValue value : enumDef.getValues()) {
            DeclarationNode dNode = new DeclarationNode(value.getValue(), value.getString());
            dNode.setNameAttributeString("value");
            dNode.setTypeAttributeString("string");
            tdNode.addChild(dNode);
        }
        return tdNode;
    }

    public static JoinPointNode toNode(JoinPointClass joinPoint) {
        Optional<JoinPointClass> extend = joinPoint.getExtend();
        JoinPointNode jpNode = new JoinPointNode(joinPoint.getName(), extend.isPresent() ? extend.get().getName() : "",
                joinPoint.getDefaultAttribute().orElse(null));
        joinPoint.getToolTip().ifPresent(jpNode::setToolTip);

        for (Attribute attribute : joinPoint.getAttributes()) {
            AttributeNode attrNode = toNode(attribute);
            jpNode.addChild(attrNode);
        }
        for (Select select : joinPoint.getSelects()) {
            SelectNode attrNode = toNode(select);
            jpNode.addChild(attrNode);
        }
        for (Action action : joinPoint.getActions()) {
            ActionNode attrNode = toNode(action);
            jpNode.addChild(attrNode);
        }
        return jpNode;
    }

    public static AttributeNode toNode(Attribute attribute) {

        DeclarationNode declNode = toNode(attribute.getDeclaration());

        AttributeNode attrNode = new AttributeNode(declNode);
        attribute.getToolTip().ifPresent(attrNode::setToolTip);

        for (Parameter parameter : attribute.getParameters()) {
            ParameterNode paramNode = toNode(parameter);
            attrNode.addChild(paramNode);
        }
        return attrNode;
    }

    private static SelectNode toNode(Select select) {
        SelectNode selectNode = new SelectNode(select.getClazz().getName(), select.getAlias());
        select.getToolTip().ifPresent(selectNode::setToolTip);
        return selectNode;
    }

    private static ActionNode toNode(Action action) {
        DeclarationNode declNode = toNode(action.getDeclaration());

        ActionNode actionNode = new ActionNode(declNode);
        action.getToolTip().ifPresent(actionNode::setToolTip);
        for (Parameter parameter : action.getParameters()) {
            ParameterNode paramNode = toNode(parameter);
            actionNode.addChild(paramNode);
        }
        return actionNode;
    }

    private static DeclarationNode toNode(Declaration parameter) {
        return new DeclarationNode(parameter.getName(), parameter.getType().toString());
    }

    private static ParameterNode toNode(Parameter parameter) {
        Declaration declaration = parameter.getDeclaration();
        return new ParameterNode(declaration.getType().toString(), declaration.getName(), parameter.getDefaultValue());
    }

}
