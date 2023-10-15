/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.weaver;

import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.exception.FilterException;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.utils.FilterExpression;

import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsFileType;

/**
 * Utility class for handling the LaraJoinpoint class
 *
 * @author Tiago
 */
public class JoinpointUtils {

    private static final String REFERENCE_PROPERTY = "getReference";
    private static final String ALIAS_PROPERTY = "classAlias";
    private static final String PARENT_PROPERTY = "_jp_parent_";
    private static final String HAS_CHILDREN_FUNCTION = "hasChildren";
    private final JsEngine scriptEngine;

    public JoinpointUtils(JsEngine engine) {
        scriptEngine = engine;
    }

    /**
     * Evaluate a joinpoint selection according to its information and the required filter
     *
     * @param jps
     *            map containing the joinpoint information. Ex: {name->"f1",type->"void"}
     * @param filter
     *            conditional expression to evaluate. Ex. 'name == "f1"'
     * @return the evaluation of the conditional expression
     */
    public static final String EVAL_NAME = "_EVAL_";
    public static final String EVAL_REFERENCE = getReferenceProperty();

    public boolean evalFilter(JoinPoint jp, FilterExpression[] filter, Object localScope) {
        if (filter.length == 0 || filter[0].isEmpty()) {
            return true;
        }
        
        scriptEngine.put(localScope, JoinpointUtils.EVAL_NAME, jp);

        final StringBuilder sb = new StringBuilder();

        List<String> toClear = new ArrayList<>();
        toClear.add(JoinpointUtils.EVAL_NAME);
        for (int i = 0; i < filter.length; i++) {

            FilterExpression filterExpression = filter[i];

            if (filterExpression.isFilterComparator()) {
                sb.append(" " + filterExpression.getOperator() + " ");
                continue;
            }
            String attributeVar = "_expected_" + i;
            scriptEngine.put(localScope, attributeVar, filterExpression.getExpected());

            toClear.add(attributeVar);
            if (filterExpression.isMatch()) {
                sb.append("String(");
                sb.append(JoinpointUtils.EVAL_NAME + "." + filterExpression.getAttribute());
                sb.append(").match(new RegExp(");
                sb.append(attributeVar);
                sb.append(")) != null");
            } else {

                sb.append(JoinpointUtils.EVAL_NAME + "." + filterExpression.getAttribute());
                sb.append(filterExpression.getOperator() + attributeVar);
            }

        }

        boolean res;
        try {
            Object result = scriptEngine.eval(sb.toString(), localScope, JsFileType.NORMAL,
                    "JoinpointUtils.evalFilter()");

            res = scriptEngine.asBoolean(result);
        } catch (Exception e) {
            throw new FilterException(jp, filter.toString(), e);
        }
        toClear.forEach(key -> scriptEngine.remove(localScope, key));
        return res;
    }

    public static String getReferenceProperty() {
        return REFERENCE_PROPERTY + "()";
    }

    public static String getAliasProperty() {
        return ALIAS_PROPERTY;
    }

    public static String getParentProperty() {
        return PARENT_PROPERTY;
    }

    public static String invokeHasChildren() {
        return HAS_CHILDREN_FUNCTION + "()";
    }

}
