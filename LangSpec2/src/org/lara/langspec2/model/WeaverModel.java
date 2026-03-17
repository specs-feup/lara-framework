package org.lara.langspec2.model;

import java.util.*;

/**
 * The complete weaver model: the single source of truth for a weaver's join point specification.
 * <p>
 * Contains the weaver identity, the global (root) join point, all declared join point classes,
 * and any user-defined types and enums.
 */
public final class WeaverModel {

    private final String weaverName;
    private final String basePackage;
    private final JpClass global;
    private JpClass root; // the "entry point" JP (e.g., "program")
    private final Map<String, JpClass> joinPoints;
    private final Map<String, TypeDef> typeDefs;
    private final Map<String, EnumDef> enumDefs;

    public WeaverModel(String weaverName, String basePackage, JpClass global) {
        if (weaverName == null || weaverName.isBlank()) {
            throw new IllegalArgumentException("Weaver name must not be null or blank");
        }
        if (basePackage == null || basePackage.isBlank()) {
            throw new IllegalArgumentException("Base package must not be null or blank");
        }
        if (global == null) {
            throw new IllegalArgumentException("Global join point class must not be null");
        }
        this.weaverName = weaverName;
        this.basePackage = basePackage;
        this.global = global;
        this.joinPoints = new LinkedHashMap<>();
        this.typeDefs = new LinkedHashMap<>();
        this.enumDefs = new LinkedHashMap<>();
    }

    public String getWeaverName() {
        return weaverName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public JpClass getGlobal() {
        return global;
    }

    public Optional<JpClass> getRoot() {
        return Optional.ofNullable(root);
    }

    public void setRoot(JpClass root) {
        this.root = root;
    }

    /**
     * Returns all declared join points (not including the global) in declaration order.
     */
    public Map<String, JpClass> getJoinPoints() {
        return Collections.unmodifiableMap(joinPoints);
    }

    public void addJoinPoint(JpClass jp) {
        if (joinPoints.containsKey(jp.getName())) {
            throw new IllegalArgumentException("Duplicate join point: " + jp.getName());
        }
        joinPoints.put(jp.getName(), jp);
    }

    public Optional<JpClass> getJoinPoint(String name) {
        if (global.getName().equals(name)) {
            return Optional.of(global);
        }
        return Optional.ofNullable(joinPoints.get(name));
    }

    public Map<String, TypeDef> getTypeDefs() {
        return Collections.unmodifiableMap(typeDefs);
    }

    public void addTypeDef(TypeDef td) {
        if (typeDefs.containsKey(td.name())) {
            throw new IllegalArgumentException("Duplicate type definition: " + td.name());
        }
        typeDefs.put(td.name(), td);
    }

    public Map<String, EnumDef> getEnumDefs() {
        return Collections.unmodifiableMap(enumDefs);
    }

    public void addEnumDef(EnumDef ed) {
        if (enumDefs.containsKey(ed.name())) {
            throw new IllegalArgumentException("Duplicate enum definition: " + ed.name());
        }
        enumDefs.put(ed.name(), ed);
    }

    /**
     * Returns all join point classes including the global, ordered: global first, then declared order.
     */
    public List<JpClass> getAllJpClasses() {
        var all = new ArrayList<JpClass>();
        all.add(global);
        all.addAll(joinPoints.values());
        return Collections.unmodifiableList(all);
    }

    /**
     * Returns direct children of the given join point class.
     */
    public List<JpClass> getDirectChildren(JpClass parent) {
        var children = new ArrayList<JpClass>();
        for (var jp : joinPoints.values()) {
            if (jp.getParent().orElse(null) == parent) {
                children.add(jp);
            }
        }
        return children;
    }
}
