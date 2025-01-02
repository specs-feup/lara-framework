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

package org.lara.interpreter.utils;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.language.specification.dsl.*;
import pt.up.fe.specs.jsengine.JsEngine;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WeaverSpecification {
    private final LanguageSpecification ls;
    private final JsEngine engine;

    public static WeaverSpecification newInstance(LanguageSpecification ls, JsEngine engine) {
        return new WeaverSpecification(ls, engine);
    }

    private WeaverSpecification(LanguageSpecification ls, JsEngine engine) {
        this.ls = ls;
        this.engine = engine;
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

    public boolean isJoinPoint(Object obj, String type) {
        return isJoinPoint(obj) && ((JoinPoint) obj).instanceOf(type);
    }

    public Object getJoinpoints() {

        List<String> joinPoints = ls.getJoinPoints().values().stream()
                .map(JoinPointClass::getName)
                .collect(Collectors.toList());
        joinPoints.add(JoinPointClass.getGlobalName());
        return engine.toNativeArray(joinPoints);
    }

    public Object attributesOf(String joinPoint) {
        return attributesOf(joinPoint, true);
    }

    public Object selectsOf(String joinPointName) {
        return selectsOf(joinPointName, true);
    }

    public Object actionsOf(String joinPointName) {
        return actionsOf(joinPointName, true);
    }

    public Object attributesOf(String joinPointName, boolean allInformation) {

        JoinPointClass joinPoint = getJoinPoint(joinPointName);
        if (joinPoint == null) {
            warnMissingJoinPointType(joinPointName, "attributes");
            return engine.newNativeArray();
        }
        Collection<Attribute> attributes;
        if (allInformation) {
            attributes = joinPoint.getAttributes();
        } else {
            attributes = joinPoint.getAttributesSelf();
        }
        List<String> attributeStrings = attributes.stream()
                .map(Attribute::toString)
                .collect(Collectors.toList());
        return engine.toNativeArray(attributeStrings);
    }

    private static void warnMissingJoinPointType(String joinPointName, String collectionType) {
        System.out.println(
                "[Warning] The join point '" + joinPointName
                        + "' does not exist in the language specification. Returning an empty array of "
                        + collectionType);
    }

    public Object selectsOf(String joinPointName, boolean allInformation) {

        JoinPointClass joinPoint = getJoinPoint(joinPointName);
        if (joinPoint == null) {
            warnMissingJoinPointType(joinPointName, "selects");
            return engine.newNativeArray();
        }
        Collection<Select> attributes;
        if (allInformation) {
            attributes = joinPoint.getSelects();
        } else {
            attributes = joinPoint.getSelectsSelf();
        }
        List<String> attributeStrings = attributes.stream()
                .map(Select::toString)
                .collect(Collectors.toList());
        return engine.toNativeArray(attributeStrings);
    }

    private JoinPointClass getJoinPoint(String joinPointName) {
        boolean isGlobal = joinPointName.equals(JoinPointClass.getGlobalName());
        JoinPointClass joinPoint = isGlobal ? ls.getGlobal() : ls.getJoinPoint(joinPointName);
        return joinPoint;
    }

    public Object actionsOf(String joinPointName, boolean allInformation) {

        JoinPointClass joinPoint = getJoinPoint(joinPointName);
        if (joinPoint == null) {
            warnMissingJoinPointType(joinPointName, "actions");
            return engine.newNativeArray();
        }
        Collection<Action> attributes;
        if (allInformation) {
            attributes = joinPoint.getActions();
        } else {
            attributes = joinPoint.getActionsSelf();
        }
        List<String> attributeStrings = attributes.stream()
                .map(Action::toString)
                .collect(Collectors.toList());
        return engine.toNativeArray(attributeStrings);
    }
}
