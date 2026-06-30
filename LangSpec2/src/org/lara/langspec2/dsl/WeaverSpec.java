package org.lara.langspec2.dsl;

import org.lara.langspec2.model.*;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.*;
import org.lara.langspec2.validation.SpecValidator;

import java.util.*;

/**
 * Abstract base class for defining a weaver specification using a fluent Java DSL.
 * <p>
 * Subclass this and override {@link #define()} to declare join points, attributes, and actions.
 * Call {@link #build()} to construct the validated {@link WeaverModel}.
 *
 * <pre>
 * public class CxxSpec extends WeaverSpec {
 *     {@literal @}Override
 *     public void define() {
 *         weaverPrefix("Cxx");
 *         packageName("pt.up.fe.specs.clava.weaver");
 *
 *         joinPoint("statement").extending(global())
 *             .attribute("isFirst", BOOLEAN);
 *
 *         joinPoint("loop").extending("statement")
 *             .attribute("kind", STRING)
 *             .attribute("body", THIS);
 *     }
 * }
 * </pre>
 */
public abstract class WeaverSpec {

    // ----- Type constants -----
    public static final JpDataType STRING = new DirectType("String");
    public static final JpDataType INT = new DirectType("int");
    public static final JpDataType LONG = new DirectType("long");
    public static final JpDataType DOUBLE = new DirectType("double");
    public static final JpDataType FLOAT = new DirectType("float");
    public static final JpDataType BOOLEAN = new DirectType("boolean");
    public static final JpDataType VOID = new DirectType("void");
    public static final JpDataType OBJECT = new ReferenceType("Object");
    public static final JpDataType INTEGER = new ReferenceType("Integer");
    public static final JpDataType LONG_BOXED = new ReferenceType("Long");
    public static final JpDataType DOUBLE_BOXED = new ReferenceType("Double");
    public static final JpDataType FLOAT_BOXED = new ReferenceType("Float");
    public static final JpDataType BOOLEAN_BOXED = new ReferenceType("Boolean");
    public static final JpDataType THIS = new SelfType();

    // ----- Type constructors -----

    protected static JpDataType array(JpDataType element) {
        return new ArrayType(element);
    }

    protected static JpDataType list(JpDataType element) {
        return new ParameterizedType(new DirectType("List"), List.of(element));
    }

    protected static JpDataType map(JpDataType key, JpDataType value) {
        return new ParameterizedType(new DirectType("Map"), List.of(key, value));
    }

    protected static JpDataType jpRef(String name) {
        return new JpRefType(name);
    }

    protected static JpDataType typeDefRef(String name) {
        return new TypeDefRefType(name);
    }

    protected static JpDataType enumRef(String name) {
        return new EnumRefType(name);
    }

    // ----- Internal state -----

    private String weaverPrefix;
    private String packageName;
    private JpClass global;
    private JpBuilder globalBuilder;
    private String rootName;
    private final Map<String, JpClass> joinPoints = new LinkedHashMap<>();
    private final Map<JpClass, String> deferredParents = new LinkedHashMap<>();
    private final Map<String, TypeDef> typeDefs = new LinkedHashMap<>();
    private final Map<String, EnumDef> enumDefs = new LinkedHashMap<>();

    // ----- Hierarchy builders -----

    /**
     * Returns the builder for the global (base) join point class.
     * All join points inherit from this unless they specify a different parent.
     */
    protected JpBuilder global(String name) {
        if (globalBuilder == null) {
            global = new JpClass(name);
            globalBuilder = new JpBuilder(this, global);
        }
        return globalBuilder;
    }

    /**
     * Returns the builder for the global (base) join point class.
     * All join points inherit from this unless they specify a different parent.
     */
    protected JpBuilder global() {
        return global("joinpoint");
    }

    /**
     * Declares a new join point type.
     */
    protected JpBuilder joinPoint(String name) {
        if (joinPoints.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate join point declaration: " + name);
        }
        var jp = new JpClass(name);
        joinPoints.put(name, jp);
        return new JpBuilder(this, jp);
    }

    // ----- Weaver identity -----

    protected void weaverPrefix(String prefix) {
        this.weaverPrefix = prefix;
    }

    protected void packageName(String pkg) {
        this.packageName = pkg;
    }

    protected void rootJoinPoint(String name) {
        this.rootName = name;
    }

    // ----- User-defined types -----

    protected TypeDefBuilder typeDef(String name) {
        return new TypeDefBuilder(this, name);
    }

    protected EnumDefBuilder enumDef(String name) {
        return new EnumDefBuilder(this, name);
    }

    // ----- Internal callbacks from builders -----

    void deferParent(JpClass child, String parentName) {
        deferredParents.put(child, parentName);
    }

    void addTypeDef(TypeDef td) {
        typeDefs.put(td.name(), td);
    }

    void addEnumDef(EnumDef ed) {
        enumDefs.put(ed.name(), ed);
    }

    // ----- Build -----

    /**
     * The user overrides this to declare the spec.
     */
    public abstract void define();

    /**
     * Builds and validates the {@link WeaverModel}.
     */
    public WeaverModel build() {
        // Run the user's define()
        define();

        // Ensure global exists
        if (global == null) {
            global();
        }

        // Resolve deferred parent references
        for (var entry : deferredParents.entrySet()) {
            var child = entry.getKey();
            var parentName = entry.getValue();
            var parentJp = resolveJpClass(parentName);
            child.setParent(parentJp);
        }

        // Set default parent (global) for join points without parent
        for (var jp : joinPoints.values()) {
            if (jp.getParent().isEmpty()) {
                jp.setParent(global);
            }
        }

        // Construct the model
        var model = new WeaverModel(
                weaverPrefix,
                packageName != null ? packageName : "org.lara.weaver",
                global
        );

        for (var jp : joinPoints.values()) {
            model.addJoinPoint(jp);
        }

        for (var td : typeDefs.values()) {
            model.addTypeDef(td);
        }

        for (var ed : enumDefs.values()) {
            model.addEnumDef(ed);
        }

        // Set root
        if (rootName != null) {
            model.setRoot(resolveJpClass(rootName));
        }

        // Validate
        SpecValidator.validate(model);

        return model;
    }

    /**
     * Builds without validation (for merging purposes).
     */
    public WeaverModel buildRaw() {
        define();

        if (global == null) {
            global();
        }

        for (var entry : deferredParents.entrySet()) {
            var child = entry.getKey();
            var parentName = entry.getValue();
            var parentJp = resolveJpClass(parentName);
            child.setParent(parentJp);
        }

        for (var jp : joinPoints.values()) {
            if (jp.getParent().isEmpty()) {
                jp.setParent(global);
            }
        }

        var model = new WeaverModel(
                weaverPrefix,
                packageName != null ? packageName : "org.lara.weaver",
                global
        );

        for (var jp : joinPoints.values()) {
            model.addJoinPoint(jp);
        }

        for (var td : typeDefs.values()) {
            model.addTypeDef(td);
        }

        for (var ed : enumDefs.values()) {
            model.addEnumDef(ed);
        }

        if (rootName != null) {
            model.setRoot(resolveJpClass(rootName));
        }

        return model;
    }

    private JpClass resolveJpClass(String name) {
        if (global.getName().equals(name)) {
            return global;
        }
        var jp = joinPoints.get(name);
        if (jp == null) {
            throw new IllegalArgumentException("Unknown join point reference: " + name);
        }
        return jp;
    }

    // ----- TypeDef builder -----

    public static final class TypeDefBuilder {
        private final WeaverSpec spec;
        private final String name;
        private final List<Attribute> fields = new ArrayList<>();
        private String tooltip;

        TypeDefBuilder(WeaverSpec spec, String name) {
            this.spec = spec;
            this.name = name;
        }

        public TypeDefBuilder field(String name, JpDataType type) {
            fields.add(new Attribute(name, type));
            return this;
        }

        public TypeDefBuilder field(String name, JpDataType type, String tooltip) {
            fields.add(new Attribute(name, type, List.of(), tooltip));
            return this;
        }

        public TypeDefBuilder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public WeaverSpec end() {
            spec.addTypeDef(new TypeDef(name, fields, tooltip));
            return spec;
        }
    }

    // ----- EnumDef builder -----

    public static final class EnumDefBuilder {
        private final WeaverSpec spec;
        private final String name;
        private final List<EnumValue> values = new ArrayList<>();
        private String tooltip;

        EnumDefBuilder(WeaverSpec spec, String name) {
            this.spec = spec;
            this.name = name;
        }

        public EnumDefBuilder value(String value) {
            values.add(new EnumValue(value));
            return this;
        }

        public EnumDefBuilder value(String value, String display) {
            values.add(new EnumValue(value, display));
            return this;
        }

        public EnumDefBuilder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public WeaverSpec end() {
            spec.addEnumDef(new EnumDef(name, values, tooltip));
            return spec;
        }
    }
}
