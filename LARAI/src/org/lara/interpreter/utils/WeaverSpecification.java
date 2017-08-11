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

package org.lara.interpreter.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.Bindings;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.utils.Converter;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.lara.language.specification.dsl.Select;

public class WeaverSpecification {
    private final LanguageSpecificationV2 ls;

    public static WeaverSpecification newInstance(LanguageSpecificationV2 ls) {
        return new WeaverSpecification(ls);
    }

    private WeaverSpecification(LanguageSpecificationV2 ls) {
        this.ls = ls;
    }

    public String getRoot() {
        String ret = ls.getRoot().getName();
        String rootAlias = ls.getRootAlias();
        if (!rootAlias.isEmpty()) {
            ret = rootAlias + " (of type " + ret + ")";
        }
        return ret;
    }

    public boolean isJoinPoint(Object obj) {
        return obj instanceof JoinPoint;
    }

    public Bindings getJoinpoints() {

        List<String> joinPoints = ls.getJoinPoints().values().stream()
                .map(JoinPointClass::getName)
                .collect(Collectors.toList());
        joinPoints.add(JoinPointClass.getGlobalName());
        return Converter.toNativeArray(joinPoints);
    }

    public Bindings attributesOf(String joinPoint) {
        return attributesOf(joinPoint, true);
    }

    public Bindings selectsOf(String joinPointName) {
        return selectsOf(joinPointName, true);
    }

    public Bindings actionsOf(String joinPointName) {
        return actionsOf(joinPointName, true);
    }

    public Bindings attributesOf(String joinPointName, boolean allInformation) {

        JoinPointClass joinPoint = getJoinPoint(joinPointName);
        if (joinPoint == null) {
            warnMissingJoinPointType(joinPointName, "attributes");
            return Converter.newNativeArray();
        }
        Collection<Attribute> attributes;
        if (allInformation) {
            attributes = joinPoint.getAllAttributes();
        } else {
            attributes = joinPoint.getAttributes();
        }
        List<String> attributeStrings = attributes.stream()
                .map(Attribute::toString)
                .collect(Collectors.toList());
        return Converter.toNativeArray(attributeStrings);
    }

    private static void warnMissingJoinPointType(String joinPointName, String collectionType) {
        System.out.println(
                "[Warning] The join point '" + joinPointName
                        + "' does not exist in the language specification. Returning an empty array of "
                        + collectionType);
    }

    public Bindings selectsOf(String joinPointName, boolean allInformation) {

        JoinPointClass joinPoint = getJoinPoint(joinPointName);
        if (joinPoint == null) {
            warnMissingJoinPointType(joinPointName, "selects");
            return Converter.newNativeArray();
        }
        Collection<Select> attributes;
        if (allInformation) {
            attributes = joinPoint.getAllSelects();
        } else {
            attributes = joinPoint.getSelects();
        }
        List<String> attributeStrings = attributes.stream()
                .map(Select::toString)
                .collect(Collectors.toList());
        return Converter.toNativeArray(attributeStrings);
    }

    private JoinPointClass getJoinPoint(String joinPointName) {
        boolean isGlobal = joinPointName.equals(JoinPointClass.getGlobalName());
        JoinPointClass joinPoint = isGlobal ? ls.getGlobal() : ls.getJoinPoint(joinPointName);
        return joinPoint;
    }

    public Bindings actionsOf(String joinPointName, boolean allInformation) {

        JoinPointClass joinPoint = getJoinPoint(joinPointName);
        if (joinPoint == null) {
            warnMissingJoinPointType(joinPointName, "actions");
            return Converter.newNativeArray();
        }
        Collection<Action> attributes;
        if (allInformation) {
            attributes = joinPoint.getAllActions();
        } else {
            attributes = joinPoint.getActions();
        }
        List<String> attributeStrings = attributes.stream()
                .map(Action::toString)
                .collect(Collectors.toList());
        return Converter.toNativeArray(attributeStrings);
    }
}
