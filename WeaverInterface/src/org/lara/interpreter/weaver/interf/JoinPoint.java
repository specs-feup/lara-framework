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
package org.lara.interpreter.weaver.interf;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lara.interpreter.exception.ActionException;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.language.specification.dsl.LanguageSpecificationV2;

import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

/**
 * Abstract class used by the LARA interpreter to define a join point instance
 */
public abstract class JoinPoint {

    private static final Map<Class<? extends JoinPoint>, Set<String>> JOIN_POINTS_ATTRIBUTES;
    static {
        JOIN_POINTS_ATTRIBUTES = new HashMap<Class<? extends JoinPoint>, Set<String>>();
    }

    // private static final String BASE_JOINPOINT_CLASS = "joinpoint";
    private static final String LARA_GETTER = "laraGetter";

    // public static String getBaseJoinpointClass() {
    // return BASE_JOINPOINT_CLASS;
    // }

    public static String getLaraGetterName() {
        return LARA_GETTER;
    }

    public static boolean isJoinPoint(Object value) {
        return value instanceof JoinPoint;
    }

    /**
     * Function used by the lara interpreter to verify if a join point is the same (equals) as another join point
     *
     * @param iJoinPoint
     * @return
     */
    public abstract boolean same(JoinPoint iJoinPoint); // {
    // throw new UnsupportedOperationException("The 'same' method is not
    // implemented for the join point "
    // + get_class());
    // if (this.get_class().equals(iJoinPoint.get_class())) {
    // boolean equals = compareNodes(iJoinPoint);
    // return equals;
    // }
    // return false;
    // }

    /**
     * Returns the tree node reference of this join point.<br>
     *
     * @return Tree node reference
     */
    public abstract Object getNode();

    /**
     * Compares the two join points based on their node reference of the used compiler/parsing tool. <br>
     * This is the default implementation for comparing two join points. <br>
     * <b>Note for developers:</b> A weaver may override this implementation in the (editable) abstract join point, so
     * the changes are made for all join points, or override this method in the specific join points.
     *
     * @param iJoinPoint
     * @return
     */
    // protected abstract boolean compareNodes(JoinPoint<T> iJoinPoint); // {
    // return this.getNode().equals(iJoinPoint.getNode());
    // }
    //
    // /**
    // * Returns the tree node reference representing this join point (used for
    // join operations)
    // *
    // * @return the tree node reference
    // */
    // public abstract T getNode();

    /**
     * Select from a given joinPoint class name
     *
     * @return
     */
    public List<? extends JoinPoint> select(String joinPoint) {
        throw new RuntimeException("Select '" + joinPoint + "' does not exist for join point " + get_class());
    }

    /**
     * Returns the join point class
     *
     * @return
     */
    public String get_class() {
        // return BASE_JOINPOINT_CLASS;
        return LanguageSpecificationV2.getBaseJoinpointClass();
    }

    /**
     * Returns the join point type
     *
     * @return
     */
    public String getJoinPointType() {
        return get_class();
    }

    /**
     * Returns the super type of this joinPoint or empty if no super exists
     *
     * @return
     */
    public Optional<? extends JoinPoint> getSuper() {
        return Optional.empty();
    }

    /**
     * 
     * @param type
     * @return true, if this join point is an instance of the given type, false otherwise
     */
    public boolean instanceOf(String type) {

        // Base type is 'joinpoint', is always true
        if (type.equals("joinpoint")) {
            return true;
        }

        if (getJoinPointType().equals(type)) {
            return true;
        }
        Optional<? extends JoinPoint> superType = getSuper();
        return superType.isPresent() ? superType.get().instanceOf(type) : false;
    }

    /**
     * 
     * @param type
     * @return true, if this join point is an instance of any of the given types, false otherwise
     */
    public boolean instanceOf(String[] types) {
        for (var type : types) {
            if (instanceOf(type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Defines if this joinpoint is an instanceof joinpointclass
     *
     * @return
     */
    // public boolean instanceOf(String joinpointClass) {
    // return get_class().equals(BASE_JOINPOINT_CLASS);
    // }

    /**
     * Fill the list with available actions
     *
     * @param actions
     */
    protected void fillWithActions(List<String> actions) {
        // DEFAULT ACTIONS
        actions.add("insert(String position, String code [, boolean farthestInsertion])");
        actions.add("insert(String position, JoinPoint joinPoint [, boolean farthestInsertion])");
        actions.add("def(String attribute, Object value)");
    }

    /**
     * Fill the list with possible selects from this join point
     *
     * @param selects
     */
    protected void fillWithSelects(List<String> selects) {

    }

    /**
     * Fill the list with attributes
     *
     * @param attributes
     */
    protected void fillWithAttributes(List<String> attributes) {
        // DEFAULT ATTRIBUTES
        attributes.add("srcCode");
        attributes.add("selects");
        attributes.add("attributes");
        attributes.add("actions");
        attributes.add("self");
    }

    /**
     * Return an array containing the actions this current join point can apply
     *
     * @return an array of actions
     */
    public final Object getActions() {
        final List<String> actions = new ArrayList<>();
        fillWithActions(actions);
        Object[] array = actions.toArray();
        Arrays.sort(array);
        return getWeaverEngine().getScriptEngine().toNativeArray(array);
        // return Converter.toNativeArray(array);
    }

    /**
     * Return an array containing the join points this current join point can select
     *
     * @return an array of possible selects
     */
    public final Object getSelects() {
        final List<String> selects = new ArrayList<>();
        fillWithSelects(selects);
        Object[] array = selects.toArray();
        Arrays.sort(array);
        return getWeaverEngine().getScriptEngine().toNativeArray(array);
        // return Converter.toNativeArray(array);
    }

    /**
     * Return an array containing the attributes this current join point has
     *
     * @return an array of attributes
     */
    public final Object getAttributes() {
        final List<String> attributes = new ArrayList<>();
        fillWithAttributes(attributes);
        Object[] array = attributes.toArray();
        Arrays.sort(array);
        return getWeaverEngine().getScriptEngine().toNativeArray(array);
        // return Converter.toNativeArray(array);
    }

    /**
     * @see JoinPoint#insert(String, JoinPoint)
     */
    // public <T extends JoinPoint> JoinPoint[] insertImpl(String position, T JoinPoint) {
    public JoinPoint[] insertImpl(String position, JoinPoint JoinPoint) {
        throw new UnsupportedOperationException(
                "Join point " + get_class() + ": Action insert(String,joinpoint) not implemented ");
    }

    /**
     * @see JoinPoint#insert(String, String)
     */
    public JoinPoint[] insertImpl(String position, String code) {
        throw new UnsupportedOperationException(
                "Join point " + get_class() + ": Action insert(String,String) not implemented ");
    }

    /**
     * @see JoinPoint#insert(String, JoinPoint, boolean)
     */
    public <T extends JoinPoint> void insertFarImpl(String position, T JoinPoint) {
        throw new UnsupportedOperationException(
                "Join point " + get_class() + ": Action insert(String,joinpoint,boolean) not implemented ");
    }

    /**
     * @see JoinPoint#insert(String, String, boolean)
     */
    public void insertFarImpl(String position, String code) {
        throw new UnsupportedOperationException(
                "Join point " + get_class() + ": Action insert far(String,String) not implemented ");
    }

    /**
     * Action insert that accepts a string containing the code snippet to inject
     *
     * @param position
     *            before|after|replace|around
     * @param code
     *            the code to inject
     */
    public final JoinPoint[] insert(String position, String code) {
        try {
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "insert", this, Optional.empty(), position, code);
            }
            JoinPoint[] result = this.insertImpl(position, code);
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.END, "insert", this, Optional.ofNullable(result), position, code);
            }

            return result;
        } catch (Exception e) {
            throw new ActionException(get_class(), "insert", e);
        }
    }

    /**
     * Action insert that accepts a join point to inject
     *
     * @param position
     *            before|after|replace|around
     * @param code
     *            the code to inject
     */
    public final <T extends JoinPoint> void insert(String position, T joinPoint) {
        try {
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "insert", this, Optional.empty(), position, joinPoint);
            }
            JoinPoint[] result = this.insertImpl(position, joinPoint);
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.END, "insert", this, Optional.ofNullable(result), position,
                        joinPoint);
            }
        } catch (Exception e) {
            throw new ActionException(get_class(), "insert", e);
        }
    }

    /**
     * Action insert that accepts a string containing the code snippet to inject as far as possible from the join point
     * based on other insertions over the targeted join point
     *
     * @param position
     *            before|after|replace|around
     * @param code
     *            the code to inject
     */
    public final void insertFar(String position, String code) {
        try {
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "insertFar", this, Optional.empty(), position, code);
            }

            this.insertFarImpl(position, code);
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.END, "insertFar", this, Optional.empty(), position, code);
            }
        } catch (Exception e) {
            throw new ActionException(get_class(), "insert", e);
        }
    }

    /**
     * Action insert that accepts a join point to inject and a boolean to indicate if the insertion must be as close as
     * possible to the join point (false) or as far as possible to the join point (true) based on other insertions over
     * the targeted join point
     *
     * @param position
     *            before|after|replace|around
     * @param code
     *            the code to inject
     * @param farthestInsertion
     *            if true will insert the code as far as possible from the join point, based on the other insertions
     *            over this targeted join point
     */
    public final <T extends JoinPoint> void insertFar(String position, T joinPoint) {
        try {
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "insertFar", this, Optional.empty(), position, joinPoint);
            }

            this.insertFarImpl(position, joinPoint);
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.END, "insertFar", this, Optional.empty(), position, joinPoint);
            }
        } catch (Exception e) {
            throw new ActionException(get_class(), "insert", e);
        }
    }

    /**
     *
     */
    public void defImpl(String attribute, Object value) {
        throw new UnsupportedOperationException("Join point " + get_class() + ": Action def not implemented ");
    }

    protected void unsupportedTypeForDef(String attribute, Object value) {
        String valueType;
        if (value == null) {
            valueType = "null";
        } else {
            valueType = value.getClass().getSimpleName();
            if (value instanceof JoinPoint) {
                valueType = ((JoinPoint) value).getJoinPointType();
            }
        }
        throw new UnsupportedOperationException("Join point " + get_class() + ": attribute '" + attribute
                + "' cannot be defined with the input type '" + valueType + "'");
    }

    /**
     *
     */
    public final void def(String attribute, Object value) {
        try {
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "def", this, Optional.empty(), attribute, value);
            }

            value = parseDefValue(value);

            this.defImpl(attribute, value);
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "def", this, Optional.empty(), attribute, value);
            }
        } catch (Exception e) {
            throw new ActionException(get_class(), "def", e);
        }
    }

    // /**
    // * Set the weaver engine that uses this join point. <br>
    // * <b>NOTE:</b> this method is automatically used by {@link JoinPoint#select} method. Use this method if you
    // create
    // * new join points not related to a select. For instance when you create a new join point when applying an action
    // * and return that join point as the action output.
    // *
    // * @param engine
    // */
    // public void setWeaverEngine(WeaverEngine engine) {
    // // this.engine = engine;
    //
    // }
    //
    // /**
    // * This is an overload of method {@link JoinPoint#setWeaverEngine(WeaverEngine)} that takes the reference of the
    // * weaverEngine on a given JoinPoint.
    // *
    // * @param engine
    // */
    // public void setWeaverEngine(JoinPoint reference) {
    // // engine = reference.getWeaverEngine();
    // }

    /**
     * Handles special cases in def.
     *
     * @param value
     * @return
     */
    private Object parseDefValue(Object value) {
        return parseDefValue(value, new HashSet<>());
    }

    private Object parseDefValue(Object value, Set<Object> seenObjects) {

        // If object already appear stop, cyclic dependencies not supported
        if (seenObjects.contains(value)) {
            throw new RuntimeException("Detected a cyclic dependency in 'def' value: " + value);
        }

        seenObjects.add(value);
        JsEngine jsEngine = getWeaverEngine().getScriptEngine();

        // Convert value to a Java array, if necessary
        // if (value instanceof ScriptObjectMirror && ((ScriptObjectMirror) value).isArray()) {
        if (jsEngine.isArray(value)) {

            var elements = jsEngine.getValues(value);
            // if (((ScriptObjectMirror) value).isEmpty()) {
            if (elements.isEmpty()) {
                throw new RuntimeException("Cannot pass an empty array to a 'def'");
            }

            // ScriptObjectMirror jsObject = (ScriptObjectMirror) value;
            // List<Class<?>> classes = jsObject.values().stream().map(Object::getClass).collect(Collectors.toList());
            List<Class<?>> classes = elements.stream().map(Object::getClass).collect(Collectors.toList());

            // Get common class of given instances
            List<Class<?>> superClasses = SpecsSystem.getCommonSuperClasses(classes);
            Class<?> superClass = superClasses.isEmpty() ? Object.class : superClasses.get(0);

            // Object[] objectArray = (Object[]) ScriptUtils.convert(value, Array.newInstance(superClass,
            // 0).getClass());
            Object[] objectArray = (Object[]) jsEngine.convert(value, Array.newInstance(superClass, 0).getClass());

            // Recursively convert the elements of the array
            Object[] convertedArray = (Object[]) Array.newInstance(superClass, objectArray.length);
            for (int i = 0; i < objectArray.length; i++) {
                convertedArray[i] = parseDefValue(objectArray[i], seenObjects);
            }

            return convertedArray;
        }

        return value;

    }

    public WeaverEngine getWeaverEngine() {
        return WeaverEngine.getThreadLocalWeaver();
    }

    protected EventTrigger eventTrigger() {
        return getWeaverEngine().getEventTrigger();
    }

    protected boolean hasListeners() {
        return getWeaverEngine().hasListeners();
    }

    public WeaverProfiler getWeaverProfiler() {
        return getWeaverEngine().getWeaverProfiler();
    }

    public static Object getUndefinedValue() {
        // To avoid using reference to internal package jdk.nashorn.internal.runtime.Undefined
        return WeaverEngine.getThreadLocalWeaver().getScriptEngine().getUndefined();
        // return Undefined.getUndefined();
    }

    /**
     * Implement this method and getJpParent() in order to obtain tree-like functionality (descendants, etc).
     * 
     * @return
     */

    public Stream<JoinPoint> getJpChildrenStream() {
        throw new NotImplementedException(this);
    }

    /**
     * Implement this method and getJpChildrenStream() in order to obtain tree-like functionality (descendants, etc).
     * 
     * @return
     */
    public JoinPoint getJpParent() {
        throw new NotImplementedException(this);
    }

    public List<JoinPoint> getJpChildren() {
        return getJpChildrenStream().collect(Collectors.toList());
    }

    public Stream<JoinPoint> getJpDescendantsStream() {
        return getJpChildrenStream().flatMap(c -> c.getJpDescendantsAndSelfStream());
    }

    public Stream<JoinPoint> getJpDescendantsAndSelfStream() {
        return Stream.concat(Stream.of(this), getJpDescendantsStream());
    }

    @Override
    public String toString() {
        return "Joinpoint '" + getJoinPointType() + "'";
        // return "'" + getJoinPointType() + "'";
    }

    /**
     * Tests if a join point supports a given attribute.
     *
     * @return true if the join point supports the given attribute, false otherwise
     */
    public final boolean hasAttribute(String attributeName) {
        var attributes = JOIN_POINTS_ATTRIBUTES.get(getClass());

        if (attributes == null) {
            attributes = new HashSet<>();
            final List<String> attributesList = new ArrayList<>();
            fillWithAttributes(attributesList);
            attributes.addAll(attributesList);
            JOIN_POINTS_ATTRIBUTES.put(getClass(), attributes);
        }

        return attributes.contains(attributeName);
    }

    public String getDump() {
        return dump(this, "");
    }

    public static String dump(JoinPoint jp, String prefix) {
        StringBuilder dump = new StringBuilder();
        dump.append(prefix).append(jp.toString()).append("\n");

        jp.getJpChildrenStream().forEach(child -> dump.append(dump(child, prefix + "   ")));

        return dump.toString();
    }

    public final JoinPoint getSelf() {
        return this;
    }
}
