package org.lara.langspec2.model;

import java.util.*;

/**
 * A join point class in the specification hierarchy.
 * <p>
 * Supports single inheritance via an optional parent. The "global" (root) join point
 * has no parent. All other join points inherit from the global or from another join point.
 */
public final class JpClass {

    private final String name;
    private JpClass parent; // null for global
    private final List<Attribute> attributes;
    private final List<Action> actions;
    private String defaultAttribute;
    private String tooltip;

    public JpClass(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("JpClass name must not be null or blank");
        }
        this.name = name;
        this.attributes = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Optional<JpClass> getParent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(JpClass parent) {
        this.parent = parent;
    }

    /**
     * Returns the attributes declared directly on this join point (own, not inherited).
     */
    public List<Attribute> getOwnAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    /**
     * Returns all attributes including inherited ones, from root to this class.
     */
    public List<Attribute> getAllAttributes() {
        var all = new ArrayList<Attribute>();
        if (parent != null) {
            all.addAll(parent.getAllAttributes());
        }
        all.addAll(attributes);
        return Collections.unmodifiableList(all);
    }

    /**
     * Returns the actions declared directly on this join point (own, not inherited).
     */
    public List<Action> getOwnActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Returns all actions including inherited ones, from root to this class.
     */
    public List<Action> getAllActions() {
        var all = new ArrayList<Action>();
        if (parent != null) {
            all.addAll(parent.getAllActions());
        }
        all.addAll(actions);
        return Collections.unmodifiableList(all);
    }

    public void addAttribute(Attribute attr) {
        attributes.add(attr);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public Optional<String> getDefaultAttribute() {
        return Optional.ofNullable(defaultAttribute);
    }

    public void setDefaultAttribute(String defaultAttribute) {
        this.defaultAttribute = defaultAttribute;
    }

    public Optional<String> getTooltip() {
        return Optional.ofNullable(tooltip);
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * Returns the ancestor chain from this class up to (and including) the root,
     * ordered from this class to root.
     */
    public List<JpClass> getAncestorChain() {
        var chain = new ArrayList<JpClass>();
        var current = this;
        while (current != null) {
            chain.add(current);
            current = current.parent;
        }
        return Collections.unmodifiableList(chain);
    }

    /**
     * Returns true if this class is or extends the given class.
     */
    public boolean isOrExtends(JpClass other) {
        var current = this;
        while (current != null) {
            if (current == other) {
                return true;
            }
            current = current.parent;
        }
        return false;
    }

    @Override
    public String toString() {
        return "JpClass[" + name + "]";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof JpClass jp && name.equals(jp.name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
