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

package org.lara.language.specification.dsl;

import org.lara.language.specification.dsl.types.IType;
import pt.up.fe.specs.util.collections.MultiMap;
import pt.up.fe.specs.util.lazy.Lazy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JoinPointClass extends BaseNode implements Comparable<JoinPointClass> {

    private static final String GLOBAL_NAME = "joinpoint";
    private String name;
    private Optional<JoinPointClass> extend;
    private Optional<String> defaultAttribute;
    private List<Attribute> attributes;
    private List<Action> actions;

    // TODO: There is attribute and action overloading, fix this
    private final Lazy<MultiMap<String, Attribute>> attributeMap;
    private final Lazy<MultiMap<String, Action>> actionsMap;

    public JoinPointClass(String name) {
        this(name, null, null);
    }

    public JoinPointClass(String name, JoinPointClass extend, String defaultAttribute) {

        setName(name);
        setExtend(extend);
        setDefaultAttribute(defaultAttribute);
        attributes = new ArrayList<>();
        actions = new ArrayList<>();

        attributeMap = Lazy.newInstance(() -> buildMultiMap(getAttributesSelf(), attr -> attr.getName()));
        actionsMap = Lazy.newInstance(() -> buildMultiMap(getActionsSelf(), action -> action.getName()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private <T extends BaseNode> MultiMap<String, T> buildMultiMap(List<T> nodes, Function<T, String> keyMapper) {
        MultiMap<String, T> map = new MultiMap<>();

        for (var node : nodes) {
            map.put(keyMapper.apply(node), node);
        }

        return map;
    }

    public boolean hasExtend() {
        return extend.isPresent();
    }

    /**
     * Which join point class this join point extends. All join points extends
     * 'joinpoint', except for joinpoint itself.
     *
     * @return the join point class it extends, or Optional.empty() if does not extend anything. 
     */
    public Optional<JoinPointClass> getExtend() {
        return extend;
    }

    /**
     * @return the join point class it explicitly extends, or Optional.empty() if
     *         does not extend a class or extends 'joinpoint'
     */
    public Optional<JoinPointClass> getExtendExplicit() {
        return getExtend().filter(jp -> !jp.getName().equals(getGlobalName()));
    }

    public void setExtend(JoinPointClass extend) {
        if (extend == null) {
            this.extend = Optional.empty();
        } else {
            this.extend = Optional.of(extend);
        }
    }

    public void setDefaultAttribute(String defaultAttribute) {
        if (defaultAttribute == null) {
            this.defaultAttribute = Optional.empty();
        } else {
            this.defaultAttribute = Optional.of(defaultAttribute);
        }
    }

    public Optional<String> getDefaultAttribute() {
        // If present return
        if (defaultAttribute.isPresent()) {
            return defaultAttribute;
        }

        // Check if super has default attribute
        return getExtend().map(superJp -> superJp.getDefaultAttribute()).orElse(Optional.empty());
    }

    public void add(Attribute attribute) {
        attributes.add(attribute);
    }

    public void add(Action action) {
        actions.add(action);
    }

    public void addAttribute(IType type, String name, Parameter... parameters) {
        attributes.add(new Attribute(type, name, Arrays.asList(parameters)));
    }

    public void addAction(IType returnType, String name, Parameter... parameters) {
        actions.add(new Action(returnType, name, Arrays.asList(parameters)));
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<Attribute> getAttributesSelf() {
        return Collections.unmodifiableList(attributes);
    }

    public List<Action> getActionsSelf() {
        return Collections.unmodifiableList(actions);
    }

    public List<Attribute> getAttributeSelf(String name) {
        return attributeMap.get().get(name);
    }

    /**
     * @param name
     * @return the actions corresponding to the given name, or empty list if none
     *         exists.
     */
    public List<Action> getActionSelf(String name) {
        return actionsMap.get().get(name);
    }

    public boolean hasAttributeSelf(String name) {
        return attributeMap.get().containsKey(name);
    }

    public boolean hasActionSelf(String name) {
        return actionsMap.get().containsKey(name);
    }

    public boolean hasAttribute(String name) {
        // If attribute present, return immediately
        if (hasAttributeSelf(name)) {
            return true;
        }

        // If extends join point, find in super
        return extend.isPresent() && extend.get().hasAttribute(name);
    }

    public boolean hasAction(String name) {
        // If action present, return immediately
        if (hasActionSelf(name)) {
            return true;
        }

        // If extends join point, find in super
        return extend.isPresent() && extend.get().hasAction(name);
    }

    /**
     * Get all attributes for this join point
     *
     * @return
     */
    public List<Attribute> getAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(this.attributes);
        extend.ifPresent(superJp -> attributes.addAll(superJp.getAttributes()));

        return attributes;
    }

    /**
     * Get all actions for this join point, included inherited.
     *
     * @return
     */
    public List<Action> getActions() {
        List<Action> actions = new ArrayList<>();
        actions.addAll(this.actions);
        extend.ifPresent(superJp -> actions.addAll(superJp.getActions()));

        return actions;
    }

    /**
     * @param name
     * @return the attributes corresponding to the given name, or empty list if none
     *         exists. Considers all available
     *         attributes of this join point, including hierarchy
     */
    public List<Attribute> getAttribute(String name) {
        List<Attribute> attribute = new ArrayList<>();
        attributeMap.get().get(name).forEach(attribute::add);
        extend.ifPresent(superJp -> superJp.getAttribute(name)
                .forEach(attribute::add));
        return attribute;
    }

    /**
     * @param name
     * @return the actions corresponding to the given name, or empty list if none
     *         exists. Considers all available
     *         actions of this join point, including hierarchy
     */
    public List<Action> getAction(String name) {
        List<Action> action = new ArrayList<>();
        actionsMap.get().get(name).forEach(action::add);
        extend.ifPresent(superJp -> superJp.getAction(name)
                .forEach(action::add));
        return action;
    }

    public static JoinPointClass globalJoinPoint() {
        JoinPointClass globalNode = new JoinPointClass(JoinPointClass.GLOBAL_NAME);

        // TODO: Handle this in another way, it is here for compatibility reasons with
        // the weaver generator
        // Add default define (def) action
        // var defAction = new Action(new GenericType("Object", false), "def");
        // globalNode.add(defAction);
        /*
         * globalNode.add(new Action(new ArrayType(new JPType(globalNode)), "insert",
         * List.of(new Parameter(PrimitiveClasses.STRING, "position"),
         * new Parameter(PrimitiveClasses.STRING, "code"))));
         * 
         * globalNode.add(new Action(new ArrayType(new JPType(globalNode)), "insert",
         * List.of(new Parameter(PrimitiveClasses.STRING, "position"),
         * new Parameter(new GenericType("Joinpoint", false), "code"))));
         */
        return globalNode;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String toDSLString() {

        String string = "joinpoint " + getName();
        if (hasExtend() && !extend.get().getName().equals(JoinPointClass.GLOBAL_NAME)) {
            string += " extends " + extend.get().getName();
        }

        string += " {";
        string += attributes.stream().map(Attribute::toString).collect(Collectors.joining("\n\t", "\n\t", "\n"));
        string += actions.stream().map(Action::toString)
                .collect(Collectors.joining("\n\t\t", "\tactions {\n\t\t", "\n\t}\n"));

        return string + "}";
    }

    public static String getGlobalName() {
        return GLOBAL_NAME;
    }

    @Override
    public int compareTo(JoinPointClass o) {
        return getName().compareTo(o.getName());
    }
}
