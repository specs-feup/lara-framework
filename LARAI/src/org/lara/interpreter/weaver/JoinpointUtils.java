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

import javax.script.Bindings;

import org.lara.interpreter.exception.FilterException;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;
import org.lara.interpreter.weaver.utils.FilterExpression;

import pt.up.fe.specs.jsengine.JsEngine;

/**
 * Utility class for handling the LaraJoinpoint class
 *
 * @author Tiago
 */
public class JoinpointUtils {

    private static final String LARA_JOIN_POINT_PROPERTY = "laraJoinPoint";
    private static final String IS_EMPTY_PROPERTY = "isEmpty";
    private static final String REFERENCE_PROPERTY = "getReference";
    private static final String ALIAS_PROPERTY = "classAlias";
    private static final String PARENT_PROPERTY = "_jp_parent_";
    private static final String HAS_CHILDREN_FUNCTION = "hasChildren";
    private final JsEngine scriptEngine;
    // TODO: Java 9 replace
    // private final List<NativeFunction> actions = null;

    public JoinpointUtils(JsEngine engine) {
        scriptEngine = engine;
    }

    /**
     * Converts the java LaraJoinpoint class, and its children, to a JavaScript object.
     *
     * @param root
     *            the first joinpoint object used to include all the joinpoints in the beginning of the joinpoint chain.
     *            Usual structure for first joinpoint is to only contain children and no other information.
     * @return A javascript native object.
     */
    @Deprecated
    public Bindings toJavaScript(LaraJoinPoint root) {
        // TODO - must be removed as it is making the program use 10x more memory than needed
        final Bindings obj = scriptEngine.createBindings();
        if (root.getChildren() != null) {

            for (final LaraJoinPoint child : root.getChildren()) {
                toJavaScriptAux(child, obj);
            }
            obj.put(IS_EMPTY_PROPERTY, root.getChildren().isEmpty());
        } else {
            obj.put(IS_EMPTY_PROPERTY, true);
        }
        obj.put(LARA_JOIN_POINT_PROPERTY, root);
        return obj;
    }

    /**
     * Auxiliary function for converting a LaraJoinpoint Object to a JavaScript NativeObject
     *
     * @param jp
     *            the joinpoint to convert
     * @param parent
     *            the parent of the joinpoint
     */
    @Deprecated
    private void toJavaScriptAux(LaraJoinPoint jp, Bindings parent) {
        if (!parent.containsKey(jp.getClassAlias())) {
            final Bindings jps = scriptEngine.newNativeArray();
            parent.put(jp.getClassAlias(), jps); // TODO: change jp.getClassAlias() with "children"
        }
        final Bindings jps = (Bindings) parent.get(jp.getClassAlias());
        final Bindings obj = scriptEngine.createBindings();// cx.newObject(scope);
        jps.put("" + jps.size(), obj);
        obj.put(getReferenceProperty(), jp.getReference());
        obj.put(getParentProperty(), parent);
        if (!jp.isLeaf()) {
            for (final LaraJoinPoint child : jp.getChildren()) {
                toJavaScriptAux(child, obj);
            }
        }
    }

    /**
     * Adds the available actions to the joinpoint
     *
     * @param jp
     *            the joinpoint to update
     * @param actions
     *            list of actions to add to the joinpoint
     * @return jp with the list of actions
     */
    /*
    public Scriptable addActions(Scriptable jp) {
    // action should be, p.e.: function insert(){
    // IWeaverObject.action(this.reference, "insert", arguments);}
    // jp.put(action[i].name, action);
    for (final NativeFunction func : actions) {
        jp.put(func.getFunctionName(), jp, func);
    }
    return jp;
    }*/

    /**
     * Generates methods to call the weaver actions
     *
     * @param actionsStr
     *            list of available actions
     * @return a list of Javascript functions
     */
    /*
    public void generateActionMethods(List<String> actionsStr, String weaverName) {
    actions = new ArrayList<>();
    for (final String action : actionsStr) {
        final StringBuilder sb = new StringBuilder("function ");
        sb.append(action);
        sb.append("(){ this.");
        sb.append(weaverName);
        sb.append(".action(this.reference, \"");
        sb.append(action);
        sb.append("\", toJavaArray(java.lang.Object,arguments));}");
        final Function fAction = cx.compileFunction(scope, sb.toString(), "string", 0, null);
        actions.add((NativeFunction) fAction);
    }
    }*/

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

    public boolean evalFilter(JoinPoint jp, FilterExpression[] filter, Bindings localScope) {
        if (filter.length == 0 || filter[0].isEmpty()) {
            return true;
        }
        // localScope.entrySet().forEach(entry -> System.out.println(entry.getKey() + "-" + entry.getValue()));
        // final Scriptable obj = cx.newObject(localScope);
        // obj.put("reference", obj, jp);
        // localScope.put(" ", localScope, jp);
        // localScope.setMember("_EVAL_", jp);
        // System.out.println("BINDINGS: " + scriptEngine.getBindings());
        // scriptEngine.getJsEngine().put(localScope, JoinpointUtils.EVAL_NAME, jp);

        // TODO: Use JS JP wrapper instead of "naked" JP
        localScope.put(JoinpointUtils.EVAL_NAME, jp);

        final StringBuilder sb = new StringBuilder();
        // final StringBuilder sb = new StringBuilder("with(Object.bindProperties({},_EVAL_)){"); //
        // Object.bindProperties({},
        List<String> toClear = new ArrayList<>();
        toClear.add(JoinpointUtils.EVAL_NAME);
        for (int i = 0; i < filter.length; i++) {

            FilterExpression filterExpression = filter[i];

            if (filterExpression.isFilterComparator()) {
                sb.append(" " + filterExpression.getOperator() + " ");
                continue;
            }
            String attributeVar = "_expected_" + i;
            localScope.put(attributeVar, filterExpression.getExpected());

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

        // nonScriptObject)
        // sb.append("}");
        // final boolean res = (Boolean) cx.evaluateString(localScope, sb.toString(), "filter", 0, null);
        boolean res;
        try {
            // System.out.println("LOCAL SCOPE: " + localScope);
            // System.out.println("CODE: " + sb);
            // System.out.println("BEFORE");
            // res = (Boolean) scriptEngine.eval(sb.toString(), localScope);
            /*
            Object result1 = scriptEngine.eval("_EVAL_.name", localScope);
            System.out.println("_EVAL_.name: " + result1);
            
            Object result2 = scriptEngine.eval("_expected_0", localScope);
            System.out.println("_expected_0: " + result2);
            
            Object result3 = scriptEngine.eval("_EVAL_.name == _expected_0", localScope);
            System.out.println("_EVAL_.name == _expected_0: " + result3);
            */
            Object result = scriptEngine.eval(sb.toString(), localScope);
            res = scriptEngine.asBoolean(result);
            // res = false;

            // System.out.println("CODE: " + sb);
            // System.out.println("RESULT: " + res);
            // System.out.println("RESULT: " + result);
            // System.out.println("AFTER");
        } catch (Exception e) {
            throw new FilterException(jp, filter.toString(), e);
        }
        toClear.forEach(localScope::remove);
        return res;
    }

    /**
     * @return the actions TODO: Java 9 Replace
     */
    // public List<NativeFunction> getActions() {
    // return actions;
    // }

    // public static String getReferenceProperty() {
    // return REFERENCE_PROPERTY;
    // }
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
