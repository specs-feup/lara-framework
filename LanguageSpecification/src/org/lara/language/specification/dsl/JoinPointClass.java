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

package org.lara.language.specification.dsl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import pt.up.fe.specs.util.lazy.Lazy;

public class JoinPointClass extends BaseNode implements Comparable<JoinPointClass> {

    private static final String GLOBAL_NAME = "joinpoint";
    private String name;
    private Optional<JoinPointClass> extend;
    private List<Attribute> attributes;
    private List<Select> selects;
    private List<Action> actions;
    private LanguageSpecificationV2 langSpec;

    private final Lazy<Map<String, Attribute>> attributeMap;
    private final Lazy<Map<String, Action>> actionsMap;

    public JoinPointClass(String name, LanguageSpecificationV2 langSpec) {
        this(name, null, langSpec);
    }

    public JoinPointClass(String name, JoinPointClass extend, LanguageSpecificationV2 langSpec) {
        setName(name);
        setExtend(extend);
        setLangSpec(langSpec);
        attributes = new ArrayList<>();
        selects = new ArrayList<>();
        actions = new ArrayList<>();

        attributeMap = Lazy.newInstance(() -> buildMap(getAllAttributes(), attr -> attr.getName()));
        actionsMap = Lazy.newInstance(() -> buildMap(getAllActions(), action -> action.getName()));
    }

    private <T extends BaseNode> Map<String, T> buildMap(List<T> nodes, Function<T, String> keyMapper) {
        Map<String, T> map = new HashMap<>();

        for (var node : nodes) {
            map.put(keyMapper.apply(node), node);
        }

        return map;
    }

    /**
     * 
     * @param name
     * @return the attribute corresponding to the given name, or null if none exists.
     */
    public Attribute getAttribute(String name) {
        return attributeMap.get().get(name);
    }

    /**
     * 
     * @param name
     * @return the action corresponding to the given name, or null if none exists.
     */
    public Action getAction(String name) {
        return actionsMap.get().get(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<JoinPointClass> getExtend() {
        return extend;
    }

    public void setExtend(JoinPointClass extend) {
        if (extend == null) {
            this.extend = Optional.empty();
        } else {
            this.extend = Optional.of(extend);
        }
    }

    public void add(Attribute attribute) {
        attributes.add(attribute);
    }

    public void add(Select select) {
        selects.add(select);
    }

    public void add(Action action) {
        // action.addJoinPoint(this);
        actions.add(action);
        action.setJoinPoint(this);
    }

    public void setActions(List<Action> actions) {
        // Unset previous actions
        // this.actions.stream().forEach(action -> action.removeJoinPoint(this));
        this.actions.stream().forEach(action -> action.setJoinPoint(null));

        // Set new actions
        this.actions = actions;
        // this.actions.stream().forEach(action -> action.addJoinPoint(this));
        this.actions.stream().forEach(action -> action.setJoinPoint(this));
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<Select> getSelects() {
        return selects;
    }

    /**
     * Get selects directly related to this join point. No selects from the extended classes
     * 
     * @param selects
     */
    public void setSelects(List<Select> selects) {
        this.selects = selects;
        if (selects != null && !selects.isEmpty()) {
            selects.forEach(s -> s.setSelector(this));
        }
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Get all selects for this join point
     * 
     * @return
     */
    public List<Select> getAllSelects() {
        List<Select> selects = new ArrayList<>();
        selects.addAll(this.selects);
        if (extend.isPresent()) {
            selects.addAll(extend.get().getAllSelects());
        }

        return selects;
    }

    /**
     * Get selects in which this join point is selected
     * 
     * @return
     */
    public List<Select> getSelectedBy() {
        List<Select> selectedBy = new ArrayList<>();

        // Get
        JoinPointClass global = langSpec.getGlobal();
        global.getSelects().stream().filter(sel -> sel.getClazz().equals(this)).forEach(selectedBy::add);

        Collection<JoinPointClass> allJPs = langSpec.getJoinPoints().values();
        for (JoinPointClass joinPointClass : allJPs) {
            joinPointClass.getSelects().stream().filter(sel -> sel.getClazz().equals(this)).forEach(selectedBy::add);
        }
        return selectedBy;
    }

    /**
     * Get all selects for this join point
     * 
     * @return
     */
    public List<Attribute> getAllAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(this.attributes);

        if (extend.isPresent()) {
            attributes.addAll(extend.get().getAllAttributes());
        }

        return attributes;
    }

    /**
     * Get all selects for this join point
     * 
     * @return
     */
    public List<Action> getAllActions() {
        List<Action> actions = new ArrayList<>();
        actions.addAll(this.actions);
        if (extend.isPresent()) {
            actions.addAll(extend.get().getAllActions());
        }
        return actions;
    }

    public boolean hasExtend() {
        return extend.isPresent();
    }

    public static JoinPointClass globalJoinPoint(LanguageSpecificationV2 langSpec) {
        JoinPointClass globalNode = new JoinPointClass(JoinPointClass.GLOBAL_NAME, langSpec);
        globalNode.add(Attribute.getAttributesAttribute());
        globalNode.add(Attribute.getSelectsAttribute());
        globalNode.add(Attribute.getActionsAttribute());
        globalNode.add(Action.getInsertAction());
        globalNode.add(Action.getDefAction());
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
        string += selects.stream().map(Select::toString)
                .collect(Collectors.joining("\n\t\t", "\tselects {\n\t\t", "\n\t}\n"));
        string += actions.stream().map(Action::toString)
                .collect(Collectors.joining("\n\t\t", "\tactions {\n\t\t", "\n\t}\n"));

        return string + "}";
    }

    public static String getGlobalName() {
        return GLOBAL_NAME;
    }

    public LanguageSpecificationV2 getLangSpec() {
        return langSpec;
    }

    public void setLangSpec(LanguageSpecificationV2 langSpec) {
        this.langSpec = langSpec;
    }

    @Override
    public int compareTo(JoinPointClass o) {
        return getName().compareTo(o.getName());
    }
}
