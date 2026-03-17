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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.language.specification.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lara.language.specification.dsl.types.IType;

import pt.up.fe.specs.util.collections.MultiMap;

public class JoinPointClass extends BaseNode implements Comparable<JoinPointClass> {

    private static final String GLOBAL_NAME = "joinpoint";
    private String name;
    private Optional<JoinPointClass> extend;
    private Optional<String> defaultAttribute;
    private List<Attribute> attributes;
    private List<Action> actions;

    public JoinPointClass(String name) {
        this(name, null, null);
    }

    public JoinPointClass(String name, JoinPointClass extend, String defaultAttribute) {

        setName(name);
        setExtend(extend);
        setDefaultAttribute(defaultAttribute);
        attributes = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        IdentifierValidator.requireValid(name, "join point name");
        this.name = name;
    }

    private <T extends BaseNode> MultiMap<String, T> buildMultiMap(List<T> nodes, Function<T, String> keyMapper) {
        MultiMap<String, T> map = new MultiMap<>();

        for (var node : nodes) {
            map.put(keyMapper.apply(node), node);
        }

        return map;
    }

    private MultiMap<String, Attribute> buildAttributeMap() {
        return buildMultiMap(getAttributesSelf(), Attribute::getName);
    }

    private MultiMap<String, Action> buildActionMap() {
        return buildMultiMap(getActionsSelf(), Action::getName);
    }

    public boolean hasExtend() {
        return extend.isPresent();
    }

    /**
     * Which join point class this join point extends. All join points extends
     * 'joinpoint', except for joinpoint itself.
     *
     * @return the join point class it extends, or Optional.empty() if does not
     *         extend anything.
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
        return getExtend().flatMap(JoinPointClass::getDefaultAttribute);
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
        return buildAttributeMap().get(name);
    }

    /**
     * @return the actions corresponding to the given name, or empty list if none
     *         exists.
     */
    public List<Action> getActionSelf(String name) {
        return buildActionMap().get(name);
    }

    public boolean hasAttributeSelf(String name) {
        return buildAttributeMap().containsKey(name);
    }

    public boolean hasActionSelf(String name) {
        return buildActionMap().containsKey(name);
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
     */
    public List<Attribute> getAttributes() {
        List<Attribute> attributes = new ArrayList<>(this.attributes);
        extend.ifPresent(superJp -> attributes.addAll(superJp.getAttributes()));

        return attributes;
    }

    /**
     * Get all actions for this join point, included inherited.
     *
     */
    public List<Action> getActions() {
        List<Action> actions = new ArrayList<>(this.actions);
        extend.ifPresent(superJp -> actions.addAll(superJp.getActions()));

        return actions;
    }

    /**
     * @return the attributes corresponding to the given name, or empty list if none
     *         exists. Considers all available
     *         attributes of this join point, including hierarchy
     */
    public List<Attribute> getAttribute(String name) {
        List<Attribute> attribute = new ArrayList<>(buildAttributeMap().get(name));
        extend.ifPresent(superJp -> attribute.addAll(superJp.getAttribute(name)));
        return attribute;
    }

    /**
     * @return the actions corresponding to the given name, or empty list if none
     *         exists. Considers all available
     *         actions of this join point, including hierarchy
     */
    public List<Action> getAction(String name) {
        List<Action> action = new ArrayList<>(buildActionMap().get(name));
        extend.ifPresent(superJp -> action.addAll(superJp.getAction(name)));
        return action;
    }

    public static JoinPointClass globalJoinPoint() {
        return LaraJoinPointContract.build(JoinPointClass.GLOBAL_NAME);
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
