# Plan: Replace LanguageSpecification, WeaverGenerator, and JoinPoint.java

## Overview

Replace the three projects with clean-slate implementations in new Gradle modules that coexist with the old ones during migration:

| Old | New | Purpose |
|-----|-----|---------|
| `LanguageSpecification` | `LangSpec2` | Data model + Java DSL for defining join point specs |
| `WeaverGenerator` | `WeaverGen2` | Code generator (Java abstracts, JSON, provider interfaces) |
| `JoinPoint.java` (in WeaverInterface) | `JoinPoint2.java` (in WeaverInterface) | Concrete base class implementing a generated abstract |

### Key Principle: The Spec Is the Single Source of Truth

WeaverInterface itself defines a **base joinpoint spec** using the same Java DSL that weavers use. This spec declares all the attributes and actions that every join point must have (e.g., `code`, `line`, `parent`, `children`, `insertBefore`, `replaceWith`, etc.). When WeaverGen2 runs for a weaver, it receives **both** the base spec (from WeaverInterface) and the weaver-specific spec (e.g., CxxSpec). It generates:

1. An **abstract class** (`ABaseJoinPoint<Self>`) from the base spec -- declaring base attributes/actions
2. `JoinPoint2` hand-implements this abstract in WeaverInterface

This guarantees:
- **Sync**: If the base spec changes, `JoinPoint2` gets a compile error until updated
- **Complete JSON**: The output JSON includes ALL attributes/actions (base + weaver-specific), because everything is in a spec
- **Complete TS wrappers**: TypeScript wrappers automatically reflect the full contract
- **No drift**: The base spec, `JoinPoint2`, and the generated artifacts cannot go out of sync

---

## Architecture: The Provider-Registry Pattern

### The Problem Being Solved

Currently, `ALoop` holds an `AStatement` field and delegates every inherited method to it. This creates a chain of separate wrapper objects (`CxxLoop -> aStmt:CxxStatement -> aNode:CxxJoinPoint`), where `this` inside each delegate is the *delegate*, not the original object. Method overrides in `CxxStatement` are reached, but they operate on the wrong `this`.

### The New Design

**Principle**: Keep the abstract class hierarchy (`ALoop extends AStatement extends AJoinPoint`) with normal Java inheritance. Replace delegation with a *provider registry* that resolves inherited method implementations.

```
  BASE SPEC (WeaverInterface)           WEAVER SPEC (e.g., Clava)
  ──────────────────────────            ────────────────────────
  BaseJoinPointSpec.define()            CxxSpec.define()
    code: String                          statement.isFirst: Boolean
    line: Integer                         loop.kind: String
    parent: this                          loop.body: this
    children: this[]                      ...
    insertBefore(node: this)
    replaceWith(node: this)
    ...

         │                                      │
         └──────────────┬───────────────────────┘
                        │
                   WeaverGen2 (receives both)
                        │
                        ▼

                 GENERATED ABSTRACT                 PROVIDER REGISTRY
                 HIERARCHY                          (user-written impls)
                 ────────────────                   ──────────────────

                 ALoop<Self>                        LoopProvider
                   extends                            implements LoopProviderDef
                 AStatement<Self>                   StatementProvider
                   extends                            implements StatementProviderDef
                 ACxxWeaverJoinPoint<Self>           (user-editable bridge)
                   extends
                 ABaseJoinPoint<Self>                BaseJoinPointProviderDef
                   extends                            implemented by JoinPoint2 + provider
                 JoinPoint2                         (hand-written in WeaverInterface,
                   implements                        implements ABaseJoinPoint's contract)
                   ABaseJoinPoint<Self>
```

**`ABaseJoinPoint<Self>`** is generated from `BaseJoinPointSpec`. It declares all the base
attributes and actions (`getCodeImpl`, `getLineImpl`, `getParentImpl`, `insertBeforeImpl`, etc.)
as abstract methods with `final` wrappers, following the same `method()`/`methodImpl()` pattern.

**`JoinPoint2`** is hand-written and implements `ABaseJoinPoint`. If the base spec adds a new
attribute, `JoinPoint2` gets a compile error until the developer adds the implementation.
This is the synchronization mechanism -- no separate validation step needed.

### How Dispatch Works

Each generated abstract class has two kinds of methods:

**Own methods** (declared at this level) -- remain `abstract`, overridden by concrete classes or providers:
```java
// In ALoop<Self> -- own attribute
public abstract Self getBodyImpl();
public abstract String getKindImpl();
```

**Inherited methods** (from parent levels) -- override with provider dispatch instead of delegation:
```java
// In ALoop<Self> -- inherited from AStatement level
@Override
public Boolean getIsFirstImpl() {
    // Walks hierarchy: loop -> statement -> joinpoint
    // Finds StatementProvider registered for "statement"
    // Calls statementProvider.getIsFirstImpl(this)
    return provider(StatementProviderDef.class).getIsFirstImpl(this);
}
```

The `provider()` method:
1. Asks the weaver's registry for the provider implementing `StatementProviderDef`
2. The provider receives `this` (the actual `CxxLoop` instance) as its argument
3. The provider accesses the underlying node via `jp.getNode()`
4. `this` is ALWAYS the real join point object -- no separate wrapper

### What the User Writes

**1. The base spec (in WeaverInterface -- shared across all weavers):**
```java
public class BaseJoinPointSpec extends WeaverSpec {
    @Override
    public void define() {
        global()
            .attribute("code", STRING)
            .attribute("line", INT)
            .attribute("parent", THIS)
            .attribute("children", array(THIS))
            .action("insertBefore").param("node", THIS).returns(VOID)
            .action("replaceWith").param("node", THIS).returns(THIS);
    }
}
```

**2. The weaver spec (replaces XML -- only weaver-specific stuff):**
```java
public class CxxSpec extends WeaverSpec {
    @Override
    public void define() {
        weaverName("CxxWeaver");
        packageName("pt.up.fe.specs.clava.weaver");

        // Base attributes (code, line, parent, children, insertBefore, etc.)
        // come from BaseJoinPointSpec -- no need to redeclare them here.
        // CxxSpec only adds weaver-specific globals and JP types.

        joinPoint("statement").extending(global())
            .attribute("isFirst", BOOLEAN)
            .attribute("isLast", BOOLEAN);

        joinPoint("loop").extending("statement")
            .attribute("kind", STRING)
            .attribute("body", THIS)
            .action("unroll").param("factor", INT).returns(list(THIS));
    }
}
```

**2. Provider implementations (replace the old concrete classes for *inherited* behavior):**
```java
// Generated interface:
public interface StatementProviderDef<JP extends AStatement<?>> {
    default Boolean getIsFirstImpl(JP jp) {
        throw new UnsupportedOperationException("isFirst");
    }
    default Boolean getIsLastImpl(JP jp) {
        throw new UnsupportedOperationException("isLast");
    }
}

// User writes:
public class CxxStatementProvider implements StatementProviderDef<AStatement<?>> {
    @Override
    public Boolean getIsFirstImpl(AStatement<?> jp) {
        return NodeInsertUtils.isFirst((Stmt) jp.getNode());
    }
    @Override
    public Boolean getIsLastImpl(AStatement<?> jp) {
        return NodeInsertUtils.isLast((Stmt) jp.getNode());
    }
}
```

**3. Concrete join point classes (simpler than before):**
```java
public class CxxLoop extends ALoop<CxxLoop> {
    private final LoopStmt loop;

    public CxxLoop(LoopStmt loop, CxxWeaver weaver) {
        super(weaver);  // NO chain! No `new CxxStatement(loop, weaver)`
        this.loop = loop;
    }

    @Override
    public ClavaNode getNode() { return loop; }

    // Own methods -- override as before
    @Override
    public CxxLoop getBodyImpl() {
        return CxxJoinpoints.create(loop.getBody());
    }

    @Override
    public String getKindImpl() {
        return loop.getKind().toString();
    }

    // Inherited methods -- resolved via registry automatically.
    // CAN still override here if loop needs different behavior:
    //
    // @Override
    // public Boolean getIsFirstImpl() { /* loop-specific logic */ }
}
```

**4. Registration (at weaver startup):**
```java
public class CxxWeaver extends ACxxWeaver {
    @Override
    protected void registerProviders(ProviderRegistry registry) {
        registry.register(new CxxStatementProvider());
        registry.register(new CxxLoopProvider());
        // ... etc
    }
}
```

### Override Resolution Order

When `loop.getIsFirstImpl()` is called on a `CxxLoop` instance:

1. **Java virtual dispatch** first: if `CxxLoop` overrides `getIsFirstImpl()`, that wins.
2. If not, `ALoop.getIsFirstImpl()` runs: `provider(StatementProviderDef.class).getIsFirstImpl(this)`.
3. The registry returns `CxxStatementProvider`.
4. `CxxStatementProvider.getIsFirstImpl(this)` executes, where `this` is the `CxxLoop`.
5. `jp.getNode()` returns the `LoopStmt` (the ACTUAL node, not a wrapper).

This means:
- The user CAN override any inherited method directly in `CxxLoop` (normal Java override).
- If they don't, the provider for the declaring level is used.
- `this` is always the correct object.
- No chain of wrapper objects.

### The `method()` / `methodImpl()` Pattern

Preserved exactly as before. Each attribute/action still generates:
- `getBodyImpl()` -- abstract or default, uses `Self` type
- `getBody()` -- `final`, wraps in try/catch, returns `Object`, handles null → undefined

The only change is that *inherited* `...Impl()` methods dispatch via provider instead of delegating to a chain object.

### CRTP / Self Type

Preserved exactly. Every abstract class is `<Self extends ALoop<Self>>`. `ThisType` in the spec maps to `Self` in generated code. Concrete classes bind: `CxxLoop extends ALoop<CxxLoop>`.

### Provider Interfaces vs Abstract Provider Classes

The generator produces a **provider definition interface** for each join point type that has own attributes or actions:

```java
// Generated for "statement" level
public interface StatementProviderDef<JP extends AStatement<?>> {
    default Boolean getIsFirstImpl(JP jp) { throw new UnsupportedOperationException(); }
    default Boolean getIsLastImpl(JP jp) { throw new UnsupportedOperationException(); }
}
```

Using `default` methods means the user only needs to implement the methods they care about. The interface uses `JP extends AStatement<?>` so implementations get the correct JP type for their level.

### `instanceOf` and `getSuper`

- `instanceOf(String)`: No longer delegates to a chain object. Instead, the generated code walks the static hierarchy:
  ```java
  // In ALoop
  @Override
  public boolean instanceOf(String joinpointClass) {
      return "loop".equals(joinpointClass)
          || "statement".equals(joinpointClass)
          || "joinpoint".equals(joinpointClass);
  }
  ```
  The hierarchy is known at generation time -- no runtime delegation needed.

- `getSuper()`: **Removed**. Its only purpose was exposing the chain. With no chain, it's unnecessary. If hierarchy introspection is needed, use `instanceOf` or the static spec.

---

## Phase 1: `LangSpec2` -- New Data Model and Java DSL

### 1.1 Type System (sealed interface hierarchy)

```java
public sealed interface JpDataType {
    record PrimitiveType(String name) implements JpDataType {}     // "int", "boolean", "String", etc.
    record SelfType() implements JpDataType {}                     // "this"
    record JpRefType(String jpName) implements JpDataType {}       // reference to another JP
    record ArrayType(JpDataType element) implements JpDataType {}   // T[]
    record ParameterizedType(JpDataType base, List<JpDataType> args) implements JpDataType {}
    record WildcardType(BoundKind kind, JpDataType bound) implements JpDataType {}

    enum BoundKind { UNBOUNDED, EXTENDS, SUPER }
}
```

Using sealed interfaces + records gives pattern matching, immutability, and clear type safety. No more `Primitive` vs `PrimitiveClasses` confusion from the old code.

### 1.2 Model Classes

```java
public record Attribute(String name, JpDataType type, List<Parameter> parameters, String tooltip) {}
public record Action(String name, JpDataType returnType, List<Parameter> parameters, String tooltip) {}
public record Parameter(String name, JpDataType type, String defaultValue) {}
public record TypeDef(String name, List<Attribute> fields, String tooltip) {}
public record EnumDef(String name, List<EnumValue> values, String tooltip) {}
public record EnumValue(String value, String display) {}

public record JpClass(
    String name,
    Optional<JpClass> parent,
    List<Attribute> attributes,
    List<Action> actions,
    Optional<String> defaultAttribute,
    String tooltip
) {}

public record WeaverModel(
    String weaverName,
    String basePackage,
    JpClass global,
    JpClass root,
    Map<String, JpClass> joinPoints,
    Map<String, TypeDef> typeDefs,
    Map<String, EnumDef> enumDefs
) {}
```

### 1.3 Java DSL (Builder API)

A fluent builder that constructs a `WeaverModel`:

```java
public abstract class WeaverSpec {
    // Type constants
    protected static final JpDataType STRING = new PrimitiveType("String");
    protected static final JpDataType INT = new PrimitiveType("int");
    protected static final JpDataType BOOLEAN = new PrimitiveType("boolean");
    protected static final JpDataType VOID = new PrimitiveType("void");
    protected static final JpDataType THIS = new SelfType();

    // Type constructors
    protected static JpDataType array(JpDataType element) { ... }
    protected static JpDataType list(JpDataType element) { ... }
    protected static JpDataType map(JpDataType key, JpDataType value) { ... }
    protected static JpDataType jpRef(String name) { ... }

    // Hierarchy builders
    protected JpBuilder global() { ... }
    protected JpBuilder joinPoint(String name) { ... }
    protected void weaverName(String name) { ... }
    protected void packageName(String pkg) { ... }

    // Typedef/enum builders
    protected TypeDefBuilder typeDef(String name) { ... }
    protected EnumDefBuilder enumDef(String name) { ... }

    // Entry point
    public abstract void define();
    public WeaverModel build() { define(); return constructModel(); }
}
```

### 1.4 Validation

- No inheritance cycles
- No duplicate names
- Reserved keyword checking (`this`, Java keywords)
- Type reference resolution (all JP refs must exist)
- `SelfType` only allowed in attribute/action types (not in typedef fields)

### 1.5 JSON Serialization

`WeaverModel.toJson()` -- produces the JSON consumed by the TypeScript wrapper generator. Same structure as today's `LangSpecNode.toJson()` for backward compatibility with the TS generator.

### 1.6 Gradle Module Structure

```
LangSpec2/
  build.gradle              (Java 17, JUnit 5, no XML dependencies)
  src/
    org/lara/langspec2/
      model/                (WeaverModel, JpClass, Attribute, Action, etc.)
      types/                (JpDataType sealed hierarchy)
      dsl/                  (WeaverSpec, builders)
      validation/           (Validators)
      json/                 (JSON serializer)
  test/
    ...                     (Unit tests for model, DSL, validation, JSON)
```

---

## Phase 2: `WeaverGen2` -- New Code Generator

### 2.1 What Gets Generated

For a weaver named "Cxx" with JP hierarchy `loop extends statement extends joinpoint`:

```
generated/
  abstracts/
    joinpoints/
      AJoinPoint.java                  -- Root abstract (global attrs/actions)
      ACxxWeaverJoinPoint.java         -- User-editable bridge (generated ONCE, not overwritten)
      AStatement.java                  -- Statement JP abstract
      ALoop.java                       -- Loop JP abstract (NO delegation field!)
  providers/
    StatementProviderDef.java          -- Provider interface for statement-level impls
    LoopProviderDef.java               -- Provider interface for loop-level impls
  registry/
    ProviderRegistry.java              -- Runtime registry for provider lookup
  weaver/
    ACxxWeaver.java                    -- Abstract weaver class
    CxxWeaver.java                     -- Weaver skeleton (generated ONCE)
  entities/
    NodeInfo.java                      -- TypeDef class
  enums/
    NodeKind.java                      -- Enum class
  CxxWeaver.json                       -- JSON spec for TS generator
  CxxWeaver.dot                        -- DOT hierarchy graph
```

### 2.2 Generated Abstract Class Structure (ALoop example)

```java
public abstract class ALoop<Self extends ALoop<Self>> extends AStatement<Self> {

    // --- Constructor: NO delegation field ---
    public ALoop(CxxWeaver weaver) {
        super(weaver);
    }

    // --- Own attributes: abstract Impl + final wrapper ---

    public abstract Self getBodyImpl();

    public final Object getBody() {
        try {
            Self result = this.getBodyImpl();
            return result != null ? result : getUndefinedValue();
        } catch (Exception e) {
            throw new AttributeException(get_class(), "body", e);
        }
    }

    public abstract String getKindImpl();

    public final Object getKind() {
        try {
            return this.getKindImpl();
        } catch (Exception e) {
            throw new AttributeException(get_class(), "kind", e);
        }
    }

    // --- Own actions: default Impl (throws) + final wrapper ---

    public Self tileImpl(Integer tileSize) {
        throw new UnsupportedOperationException(get_class() + ": Action tile not implemented");
    }

    public final Object tile(Object tileSize) {
        try {
            Self result = this.tileImpl((Integer) tileSize);
            return result != null ? result : getUndefinedValue();
        } catch (Exception e) {
            throw new ActionException(get_class(), "tile", e);
        }
    }

    // --- Inherited attributes: provider dispatch ---

    @Override
    public Boolean getIsFirstImpl() {
        return provider(StatementProviderDef.class).getIsFirstImpl(this);
    }

    @Override
    public Boolean getIsLastImpl() {
        return provider(StatementProviderDef.class).getIsLastImpl(this);
    }

    @Override
    public String getCodeImpl() {
        return provider(BaseJoinPointProviderDef.class).getCodeImpl(this);
    }

    // ... (one method per inherited attribute/action, dispatching to the correct provider level)

    // --- Identity ---

    @Override
    public final String get_class() {
        return "loop";
    }

    @Override
    public final boolean instanceOf(String joinpointClass) {
        return "loop".equals(joinpointClass)
            || "statement".equals(joinpointClass)
            || "joinpoint".equals(joinpointClass);
    }
}
```

Compare this to the current ALoop which has ~450 lines of delegation boilerplate. The new version:
- Has NO `aStmt` field
- Has NO chain constructor
- Has NO `getSuper()` returning a delegate
- Each inherited method is 1 line: `return provider(X.class).method(this);`
- `instanceOf` is a static check (known at generation time)

### 2.3 Generated Provider Definition Interface

```java
// One per JP type that declares own attributes/actions
public interface StatementProviderDef<JP extends AStatement<?>> {
    default Boolean getIsFirstImpl(JP jp) {
        throw new UnsupportedOperationException("statement: isFirst not implemented");
    }
    default Boolean getIsLastImpl(JP jp) {
        throw new UnsupportedOperationException("statement: isLast not implemented");
    }
    // Own actions too
    default Object moveAfterImpl(JP jp, Object target) {
        throw new UnsupportedOperationException("statement: moveAfter not implemented");
    }
}
```

### 2.4 Root Join Point Class (ANode / root)

The root class has no inherited methods, so it looks the same as today:

```java
public abstract class ANode<Self extends ANode<Self>> extends ACxxWeaverJoinPoint<Self> {
    // All own attributes: abstract
    public abstract Self getCloneImpl();
    public abstract String getCodeImpl();
    // ... etc
    // No delegation, no provider dispatch -- everything is own
}
```

Wait -- there's an important choice here. The root JP's attributes can ALSO go through a provider if we want consistency. This would mean `ANode` has abstract methods that `CxxNode` overrides, OR `ANode` dispatches to a `NodeProviderDef`. For maximum consistency, I recommend:

- **Root:** abstract methods directly (user implements in a concrete class)
- **Non-root:** provider dispatch for inherited, abstract for own

This means the root concrete class is essentially the "catch-all" provider. Or alternatively, the root's attributes go through a provider too, and there's no special concrete class for the root -- just a generic `CxxJoinPoint` that wraps any node, with all behavior in providers.

**Recommendation**: Use providers for ALL levels including the root. The concrete join point classes become thin wrappers focused on `getNode()` and type binding. This is cleaner and more consistent.

### 2.5 Generator Pipeline

```
BaseJoinPointSpec (WeaverInterface)  +  WeaverSpec (e.g., CxxSpec)
         │                                      │
         └──────────────┬───────────────────────┘
                        │
                   WeaverGen2.generate(baseSpec, weaverSpec, outputDir)
                        │
                        ▼
                   Merged WeaverModel
                        │
    ┌───────────────────┼───────────────────────────────────┐
    │                   │                                   │
    ▼                   ▼                                   ▼
  For WeaverInterface:             For the weaver (e.g., Clava):
  ┌─────────────────────┐         ┌───────────────────────────────────────┐
  │ ABaseJoinPoint.java │         │ AbstractJpGenerator  → AStatement,   │
  │ BaseJoinPointProv.. │         │                        ALoop, ...    │
  └─────────────────────┘         │ ProviderDefGenerator → Statement-    │
                                  │                        ProviderDef.. │
                                  │ WeaverAbstractGen    → ACxxWeaver    │
                                  │ WeaverSkeletonGen    → CxxWeaver     │
                                  │ UserAbstractGen      → ACxxWeaverJP  │
                                  │ EntityGenerator      → TypeDefs/Enums│
                                  │ JsonGenerator        → CxxWeaver.json│
                                  │ DotGenerator         → CxxWeaver.dot │
                                  └───────────────────────────────────────┘
```

The JSON output includes ALL attributes and actions from both specs, so the TS wrapper
generator sees the complete picture without any special handling.

### 2.6 Testing Strategy

Port the existing test approach from WeaverGenerator:
- **Golden file tests**: Generate code for small/medium/large specs, compare against golden files
- **Compilation tests**: Generate + compile in-memory with javax.tools.JavaCompiler
- **Regression tests**: Specific assertions about generated code patterns
- **Integration test**: Large spec mirroring Clava's real hierarchy, with manifest/hash baselines
- **Idempotency tests**: Running generator twice produces identical output

### 2.7 Gradle Module Structure

```
WeaverGen2/
  build.gradle
  src/
    org/lara/weavergen2/
      generator/
        AbstractJpGenerator.java
        ProviderDefGenerator.java
        WeaverAbstractGenerator.java
        ...
      java/                     (Java code building utilities)
      json/                     (JSON output)
  test/
    ...
  test-resources/
    golden/                     (Golden output files)
    specs/                      (Test WeaverSpec subclasses)
```

---

## Phase 3: New Base Class (`JoinPoint2` in WeaverInterface)

### 3.0 The Base Spec

WeaverInterface defines a spec using the same Java DSL:

```java
// In WeaverInterface
public class BaseJoinPointSpec extends WeaverSpec {
    @Override
    public void define() {
        // These are the attributes/actions every join point in every weaver has.
        // They become the contract that JoinPoint2 must implement.
        global()
            .attribute("joinPointType", STRING)
            .attribute("code", STRING)
            .attribute("line", INT)
            .attribute("column", INT)
            .attribute("parent", THIS)
            .attribute("children", array(THIS))
            .attribute("descendants", array(THIS))
            .attribute("root", THIS)
            .attribute("self", THIS)
            .attribute("dump", STRING)

            .action("insert").param("position", STRING).param("code", STRING).returns(THIS)
            .action("insertBefore").param("node", THIS).returns(VOID)
            .action("insertAfter").param("node", THIS).returns(VOID)
            .action("replaceWith").param("node", THIS).returns(THIS)
            .action("copy").returns(THIS)
            .action("detach").returns(THIS)
            .action("toString").returns(STRING)
            .action("equals").param("other", THIS).returns(BOOLEAN);
    }
}
```

### 3.1 Generated Abstract: `ABaseJoinPoint<Self>`

WeaverGen2 generates this from `BaseJoinPointSpec`:

```java
// GENERATED -- do not edit
public abstract class ABaseJoinPoint<Self extends ABaseJoinPoint<Self>> {

    private final WeaverEngine weaver;
    private transient Map<Class<?>, Object> providerCache;

    protected ABaseJoinPoint(WeaverEngine weaver) {
        this.weaver = weaver;
    }

    // --- Core identity (not from spec, built-in) ---
    public abstract String get_class();
    public abstract Object getNode();
    public abstract boolean instanceOf(String joinpointClass);

    // --- Base attributes: abstract Impl + final wrapper ---

    public abstract String getCodeImpl();

    public final Object getCode() {
        try {
            return this.getCodeImpl();
        } catch (Exception e) {
            throw new AttributeException(get_class(), "code", e);
        }
    }

    public abstract Integer getLineImpl();

    public final Object getLine() {
        try {
            return this.getLineImpl();
        } catch (Exception e) {
            throw new AttributeException(get_class(), "line", e);
        }
    }

    public abstract Self getParentImpl();

    public final Object getParent() {
        try {
            Self result = this.getParentImpl();
            return result != null ? result : getUndefinedValue();
        } catch (Exception e) {
            throw new AttributeException(get_class(), "parent", e);
        }
    }

    public abstract Self[] getChildrenImpl();
    // ... etc for all base attributes

    // --- Base actions: default Impl (throws) + final wrapper ---

    public Self insertBeforeImpl(Self node) {
        throw new UnsupportedOperationException(get_class() + ": Action insertBefore not implemented");
    }

    public final Object insertBefore(Object node) {
        try {
            return this.insertBeforeImpl((Self) node);
        } catch (Exception e) {
            throw new ActionException(get_class(), "insertBefore", e);
        }
    }

    // ... etc for all base actions

    // --- Provider resolution ---
    @SuppressWarnings("unchecked")
    protected <P> P provider(Class<P> providerDef) {
        if (providerCache == null) {
            providerCache = new HashMap<>();
        }
        return (P) providerCache.computeIfAbsent(providerDef,
            k -> weaver.getProviderRegistry().resolve(k));
    }

    // --- Weaver access ---
    public WeaverEngine getWeaverEngine() {
        return weaver;
    }

    public static Object getUndefinedValue() { ... }
    public boolean same(ABaseJoinPoint<?> other) { ... }
}
```

### 3.2 Hand-Written: `JoinPoint2`

`JoinPoint2` extends the generated abstract and provides the actual implementations.
It lives in WeaverInterface and is *not* generated -- it's maintained by the developer.

```java
// In WeaverInterface -- hand-written
public abstract class JoinPoint2<Self extends JoinPoint2<Self>> extends ABaseJoinPoint<Self> {

    protected JoinPoint2(WeaverEngine weaver) {
        super(weaver);
    }

    // --- Implement base spec attributes ---

    @Override
    public String getCodeImpl() {
        return getNode().toString();  // default; weavers override via provider or concrete
    }

    @Override
    public Integer getLineImpl() {
        // default implementation -- weavers can override
        return -1;
    }

    @Override
    public Self getParentImpl() {
        // Tree navigation -- will be overridden by weaver-specific providers
        throw new UnsupportedOperationException(get_class() + ": parent not implemented");
    }

    @Override
    public Self[] getChildrenImpl() {
        throw new UnsupportedOperationException(get_class() + ": children not implemented");
    }

    // ... etc.

    // --- Utility methods (not from spec, common to all JPs) ---

    public Stream<? extends JoinPoint2<?>> getJpChildrenStream() {
        return Arrays.stream(getChildrenImpl());
    }

    public Stream<? extends JoinPoint2<?>> getJpDescendantsStream() {
        return getJpChildrenStream()
            .flatMap(child -> Stream.concat(Stream.of(child), child.getJpDescendantsStream()));
    }
}
```

The key point: if `BaseJoinPointSpec` adds a new attribute `foo`, then `ABaseJoinPoint` gets
a new `abstract Foo getFooImpl()`. `JoinPoint2` immediately fails to compile until the
developer adds the implementation. **The spec drives the contract. The compiler enforces it.**

### 3.3 ProviderRegistry

```java
public class ProviderRegistry {
    private final Map<Class<?>, Object> providers = new HashMap<>();

    public <P> void register(Class<P> providerDef, P provider) {
        providers.put(providerDef, provider);
    }

    @SuppressWarnings("unchecked")
    public <P> P resolve(Class<P> providerDef) {
        P provider = (P) providers.get(providerDef);
        if (provider == null) {
            throw new IllegalStateException("No provider registered for " + providerDef.getName());
        }
        return provider;
    }
}
```

### 3.4 Where Things Live

| Artifact | Module | Generated? |
|----------|--------|------------|
| `BaseJoinPointSpec` | WeaverInterface | No (hand-written spec) |
| `ABaseJoinPoint<Self>` | WeaverInterface | **Yes** (generated from `BaseJoinPointSpec`) |
| `JoinPoint2<Self>` | WeaverInterface | No (hand-written, implements `ABaseJoinPoint`) |
| `ProviderRegistry` | LangSpec2 | No (library class) |
| `BaseJoinPointProviderDef` | WeaverInterface | **Yes** (generated provider interface for base level) |

### 3.5 How WeaverGen2 Merges the Specs

When generating code for a weaver, WeaverGen2 receives two specs:

```java
// In the Clava Gradle build task
WeaverGen2.generate(
    baseSpec:   new BaseJoinPointSpec(),   // from WeaverInterface
    weaverSpec: new CxxSpec(),             // from Clava
    outputDir:  generatedSrcDir
);
```

The generator merges them:
1. `BaseJoinPointSpec` defines the "global" / base-level attributes/actions
2. `CxxSpec` defines weaver-specific JP types, their attributes/actions, and any additional globals
3. The merged model is used for:
   - Generating the abstract hierarchy (base attrs come from `BaseJoinPointSpec`)
   - Generating the JSON (includes ALL attrs/actions from both specs)
   - Generating provider interfaces (one for the base level too: `BaseJoinPointProviderDef`)

---

## Phase 4: Migrate Clava

### 4.1 Create `CxxSpec.java`

Replace the three XML files (`joinPointModel.xml`, `artifacts.xml`, `actionModel.xml`) with a single Java class. Base attributes/actions (code, line, parent, children, etc.) are **not** declared here -- they come from `BaseJoinPointSpec` in WeaverInterface:

```java
public class CxxSpec extends WeaverSpec {
    @Override
    public void define() {
        weaverName("CxxWeaver");
        packageName("pt.up.fe.specs.clava.weaver");

        // Weaver-specific globals (additions beyond the base)
        global()
            .attribute("ast", STRING);   // example: extra global attr for Clava

        joinPoint("decl").extending(global())
            .attribute("name", STRING);

        joinPoint("namedDecl").extending("decl")
            .attribute("qualifiedName", STRING);

        joinPoint("function").extending("namedDecl")
            .attribute("params", list(jpRef("param")))
            .attribute("body", jpRef("body"));

        joinPoint("statement").extending(global())
            .attribute("isFirst", BOOLEAN);

        joinPoint("loop").extending("statement")
            .attribute("kind", STRING)
            .attribute("body", jpRef("body"));

        // ... ~90 join point types from current XML
    }
}
```

### 4.2 Generate New Abstracts

Run WeaverGen2 on CxxSpec. This produces:
- New abstract JP classes (no chain fields)
- Provider definition interfaces
- Updated JSON

### 4.3 Create Provider Implementations

Convert existing concrete classes to providers:

| Old | New |
|-----|-----|
| `ACxxWeaverJoinPoint` (common impls: getCode, getLine, getParent, ...) | `CxxBaseProvider implements BaseJoinPointProviderDef` |
| `CxxStatement extends AStatement` (with overrides) | `CxxStatementProvider implements StatementProviderDef` |
| `CxxLoop extends ALoop` (with own + inherited) | `CxxLoop extends ALoop` (own only) + `CxxLoopProvider implements LoopProviderDef` (if needed) |
| `CxxFunction extends AFunction` (with overrides) | `CxxFunction extends AFunction` (own only) + `CxxFunctionProvider` |

The base-level provider (`CxxBaseProvider`) replaces the role of `ACxxWeaverJoinPoint`.
All the common Clava implementations (getCode via `ClavaNode.getCode()`, getLine via
`ClavaNode.getLocation()`, getParent via tree navigation, etc.) move into this provider.

### 4.4 Simplify Concrete Classes

Concrete classes lose:
- Chain constructors (no more `super(new CxxStatement(...), weaver)`)
- Inherited method overrides that were only there because the chain needed them

### 4.5 Update Gradle Build

Add `LangSpec2` and `WeaverGen2` as dependencies. Add a Gradle task that:
1. Compiles `CxxSpec.java`
2. Runs WeaverGen2
3. Outputs generated code to a `generated-src` directory
4. Adds it to the source set

---

## Phase 5: Cleanup

After Clava is migrated and tests pass:
- Remove `LanguageSpecification` module
- Remove `WeaverGenerator` module
- Remove the old `JoinPoint.java` class (replace usages with `JoinPoint2`)
- Remove the XML spec files from Clava
- Remove the chain-related code from generated abstracts

---

## Risk Mitigation

1. **Performance**: Provider dispatch adds a HashMap lookup per inherited method call. Mitigate with `providerCache` on the JP instance (one lookup per provider type per JP instance lifetime).

2. **Migration scope**: Clava has ~90 join point types and ~50 concrete implementations. The provider migration is mechanical but large. Approach incrementally -- start with leaf JP types that have simple hierarchies.

3. **TypeScript compatibility**: The JSON output must remain compatible with the existing TS wrapper generator in `Clava-JS`. Verify by comparing old and new JSON output.

4. **Backward compat during transition**: Old and new modules coexist. New generated code depends on `JoinPoint2`/`LangSpec2`, old code depends on `JoinPoint`/`LanguageSpecification`. They don't interfere.

---

## Open Questions

1. **Should root JP attributes also use providers?** (Recommended: yes, for consistency. The "global provider" replaces what currently lives in `ACxxWeaverJoinPoint`.)

2. **Gradle plugin or task for generation?** A task is simpler. A plugin would be more reusable across weavers. Start with a task, extract to plugin later if needed.

3. **Should providers be singletons or per-JP-instance?** Singletons (registered once at startup) are simpler and more efficient. They receive the JP as a parameter, so they don't need state.

4. **How to handle actions whose parameters include `Self`/`this` type?** The provider interface will use `AStatement<?>` (wildcard) for the JP parameter, but action parameters typed as `Self` will need `AJoinPoint<?>` in the provider interface. The concrete class's own action overrides still use the proper `Self` type.

5. **Can `BaseJoinPointSpec` be extended by weavers?** A weaver might want to add extra globals beyond what the base spec provides. The merge step in WeaverGen2 should support this: globals from both specs are combined, with the weaver's additions layered on top.

6. **Should `ABaseJoinPoint` be regenerated at Clava build time or stored as a pre-built artifact?** If regenerated, WeaverInterface's build needs WeaverGen2 as a build dependency. If pre-built, it's committed to the repo (simpler but risks drift if someone edits `BaseJoinPointSpec` and forgets to regenerate). Recommendation: regenerate at build time.
