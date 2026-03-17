package org.lara.langspec2.dsl;

import org.lara.langspec2.model.*;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.*;

import java.util.*;

/**
 * Fluent builder for constructing a join point class within a {@link WeaverSpec}.
 * <p>
 * Supports adding attributes and actions with a chainable API.
 */
public final class JpBuilder {

    private final WeaverSpec spec;
    private final JpClass jpClass;

    JpBuilder(WeaverSpec spec, JpClass jpClass) {
        this.spec = spec;
        this.jpClass = jpClass;
    }

    /**
     * Declares that this join point extends (inherits from) the given parent.
     */
    public JpBuilder extending(JpBuilder parent) {
        jpClass.setParent(parent.jpClass);
        return this;
    }

    /**
     * Declares that this join point extends (inherits from) the named join point.
     * The name is resolved later during model construction.
     */
    public JpBuilder extending(String parentName) {
        spec.deferParent(jpClass, parentName);
        return this;
    }

    /**
     * Adds a simple attribute (no parameters) to this join point.
     */
    public JpBuilder attribute(String name, JpDataType type) {
        jpClass.addAttribute(new Attribute(name, type));
        return this;
    }

    /**
     * Adds a simple attribute with a tooltip.
     */
    public JpBuilder attribute(String name, JpDataType type, String tooltip) {
        jpClass.addAttribute(new Attribute(name, type, List.of(), tooltip));
        return this;
    }

    /**
     * Adds a parameterized attribute and returns an {@link AttributeParamBuilder} for defining parameters.
     */
    public AttributeParamBuilder attribute(String name) {
        return new AttributeParamBuilder(this, name);
    }

    /**
     * Starts defining an action on this join point.
     */
    public ActionBuilder action(String name) {
        return new ActionBuilder(this, name);
    }

    /**
     * Sets the default attribute for this join point.
     */
    public JpBuilder defaultAttribute(String name) {
        jpClass.setDefaultAttribute(name);
        return this;
    }

    /**
     * Sets the tooltip/documentation for this join point.
     */
    public JpBuilder tooltip(String tooltip) {
        jpClass.setTooltip(tooltip);
        return this;
    }

    JpClass getJpClass() {
        return jpClass;
    }

    void addAction(Action action) {
        jpClass.addAction(action);
    }

    void addAttribute(Attribute attribute) {
        jpClass.addAttribute(attribute);
    }

    /**
     * Builder for parameterized attributes.
     */
    public static final class AttributeParamBuilder {
        private final JpBuilder parent;
        private final String name;
        private JpDataType type;
        private final List<Parameter> parameters = new ArrayList<>();
        private String tooltip;

        AttributeParamBuilder(JpBuilder parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public AttributeParamBuilder type(JpDataType type) {
            this.type = type;
            return this;
        }

        public AttributeParamBuilder param(String name, JpDataType type) {
            parameters.add(new Parameter(name, type));
            return this;
        }

        public AttributeParamBuilder param(String name, JpDataType type, String defaultValue) {
            parameters.add(new Parameter(name, type, defaultValue));
            return this;
        }

        public AttributeParamBuilder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Finalizes this attribute and returns the parent JpBuilder.
         */
        public JpBuilder returns(JpDataType returnType) {
            parent.addAttribute(new Attribute(name, returnType, parameters, tooltip));
            return parent;
        }

        /**
         * Finalizes using a previously set type.
         */
        public JpBuilder end() {
            if (type == null) {
                throw new IllegalStateException("Attribute type not set for: " + name);
            }
            parent.addAttribute(new Attribute(name, type, parameters, tooltip));
            return parent;
        }
    }

    /**
     * Builder for actions.
     */
    public static final class ActionBuilder {
        private final JpBuilder parent;
        private final String name;
        private final List<Parameter> parameters = new ArrayList<>();
        private String tooltip;

        ActionBuilder(JpBuilder parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public ActionBuilder param(String name, JpDataType type) {
            parameters.add(new Parameter(name, type));
            return this;
        }

        public ActionBuilder param(String name, JpDataType type, String defaultValue) {
            parameters.add(new Parameter(name, type, defaultValue));
            return this;
        }

        public ActionBuilder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Finalizes this action with a return type and returns the parent JpBuilder.
         */
        public JpBuilder returns(JpDataType returnType) {
            parent.addAction(new Action(name, returnType, parameters, tooltip));
            return parent;
        }
    }
}
