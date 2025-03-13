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

import org.lara.interpreter.exception.ActionException;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract class used by the LARA interpreter to define a join point instance
 */
public abstract class JoinPoint {

    private static final JoinPointClass LARA_JOIN_POINT = new JoinPointClass("LaraJoinPoint");

    static {
        // JoinPointSpecification.addAttribute(null, "srcCode"));
        LARA_JOIN_POINT.addAttribute(PrimitiveClasses.STRING, "dump");
        LARA_JOIN_POINT.addAttribute(PrimitiveClasses.STRING, "joinPointType");
        LARA_JOIN_POINT.addAttribute(PrimitiveClasses.OBJECT, "node");
        LARA_JOIN_POINT.addAttribute(JPType.of(LARA_JOIN_POINT), "self");
        LARA_JOIN_POINT.addAttribute(JPType.of(LARA_JOIN_POINT), "super");
        LARA_JOIN_POINT.addAttribute(ArrayType.of(JPType.of(LARA_JOIN_POINT)), "children");
        LARA_JOIN_POINT.addAttribute(ArrayType.of(JPType.of(LARA_JOIN_POINT)), "descendants");
        LARA_JOIN_POINT.addAttribute(ArrayType.of(JPType.of(LARA_JOIN_POINT)), "scopeNodes");
        LARA_JOIN_POINT.addAction(JPType.of(LARA_JOIN_POINT), "insert",
                new Parameter(LiteralEnum.of("Position", "before", "after", "replace"), "position"),
                new Parameter(PrimitiveClasses.STRING, "code"));
        LARA_JOIN_POINT.addAction(JPType.of(LARA_JOIN_POINT), "insert",
                new Parameter(LiteralEnum.of("Position", "before", "after", "replace"), "position"),
                new Parameter(JPType.of(LARA_JOIN_POINT), "joinpoint"));
        LARA_JOIN_POINT.addAction(PrimitiveClasses.VOID, "def",
                new Parameter(PrimitiveClasses.STRING, "attribute"),
                new Parameter(PrimitiveClasses.OBJECT, "value"));
        LARA_JOIN_POINT.addAction(PrimitiveClasses.STRING, "toString");
        LARA_JOIN_POINT.addAction(PrimitiveClasses.BOOLEAN, "equals",
                new Parameter(JPType.of(LARA_JOIN_POINT), "jp"));
        LARA_JOIN_POINT.addAction(PrimitiveClasses.BOOLEAN, "instanceOf",
                new Parameter(PrimitiveClasses.STRING, "name"));
        LARA_JOIN_POINT.addAction(PrimitiveClasses.BOOLEAN, "instanceOf",
                new Parameter(ArrayType.of(PrimitiveClasses.STRING), "names"));
    }

    public static JoinPointClass getLaraJoinPoint() {
        return LARA_JOIN_POINT;
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
    public abstract boolean same(JoinPoint iJoinPoint);

    /**
     * Returns the tree node reference of this join point.<br>
     *
     * @return Tree node reference
     */
    public abstract Object getNode();

    /**
     * Returns the join point class
     *
     * @return
     */
    public String get_class() {
        // return BASE_JOINPOINT_CLASS;
        return LanguageSpecification.getBaseJoinpointClass();
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
     * @param position before|after|replace|around
     * @param code     the code to inject
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
     * @param position before|after|replace|around
     * @param code     the code to inject
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
     * @param position before|after|replace|around
     * @param code     the code to inject
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
     * @param position          before|after|replace|around
     * @param code              the code to inject
     * @param farthestInsertion if true will insert the code as far as possible from the join point, based on the other insertions
     *                          over this targeted join point
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

    public Object getChildren() {
        throw new NotImplementedException(this);
    }

    public Object getDescendants() {
        throw new NotImplementedException(this);
    }

    public Object getScopeNodes() {
        throw new NotImplementedException(this);
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

    public List<JoinPoint> getJpDescendants() {
        return getJpDescendantsStream().toList();
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
