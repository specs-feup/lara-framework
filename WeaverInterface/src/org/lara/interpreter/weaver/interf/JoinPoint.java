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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.script.Bindings;

import org.lara.interpreter.exception.ActionException;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.utils.Converter;

import jdk.nashorn.internal.runtime.Undefined;

/**
 * Abstract class used by the LARA interpreter to define a join point instance
 */
public abstract class JoinPoint {

    static final String BASE_JOINPOINT_CLASS = "joinpoint";

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
        return BASE_JOINPOINT_CLASS;
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

    public boolean instanceOf(String type) {
        if (getJoinPointType().equals(type)) {
            return true;
        }
        Optional<? extends JoinPoint> superType = getSuper();
        return superType.isPresent() ? superType.get().instanceOf(type) : false;
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
        actions.add("insert(String position, String code)");
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
    }

    /**
     * Return an array containing the actions this current join point can apply
     * 
     * @return an array of actions
     */
    public final Bindings getActions() {
        final List<String> actions = new ArrayList<>();
        fillWithActions(actions);
        Object[] array = actions.toArray();
        Arrays.sort(array);
        return Converter.toNativeArray(array);
    }

    /**
     * Return an array containing the join points this current join point can select
     * 
     * @return an array of possible selects
     */
    public final Bindings getSelects() {
        final List<String> selects = new ArrayList<>();
        fillWithSelects(selects);
        Object[] array = selects.toArray();
        Arrays.sort(array);
        return Converter.toNativeArray(array);
    }

    /**
     * Return an array containing the attributes this current join point has
     * 
     * @return an array of attributes
     */
    public final Bindings getAttributes() {
        final List<String> attributes = new ArrayList<>();
        fillWithAttributes(attributes);
        Object[] array = attributes.toArray();
        Arrays.sort(array);
        return Converter.toNativeArray(array);
    }

    /**
     * 
     */
    public void insertImpl(String position, String code) {
        throw new UnsupportedOperationException("Join point " + get_class() + ": Action insert not implemented ");
    }

    /**
     * 
     */
    public final void insert(String position, String code) {
        try {
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.BEGIN, "insert", this, Optional.empty(), position, code);
            }
            this.insertImpl(position, code);
            if (hasListeners()) {
                eventTrigger().triggerAction(Stage.END, "insert", this, Optional.empty(), position, code);
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
        return Undefined.getUndefined();
    }
}
