## Stage 4 Discovered Issues

During implementation of Stage 4 (golden tests) the following potential bugs / problematic behaviors were observed:

### 1. Code generation failure with attribute type `{Location}`
**Spec:** `test-resources/spec/valid/medium/artifacts.xml` defines attribute:
```xml
<attribute name="params" type="{Location}"/>
```
Generation using:
```shell
gradle run --args="-x test-resources/spec/valid/medium -o build/tmp/gen-medium -p medium.pkg -w MediumWeaver"
```
fails with stack trace starting at:
```
java.lang.RuntimeException: Bad format for array definition. Bad characters: [Location]
 at org.specs.generators.java.types.JavaTypeFactory.splitTypeFromArrayDimension(JavaTypeFactory.java:300)
 at org.lara.interpreter.weaver.generator.generator.java.utils.ConvertUtils.getAttributeConvertedType(ConvertUtils.java:103)
```
**Why this appears to be a bug:** `{Location}` is intended to represent a literal / inline object or enum-like structure (it is declared as `<object name="Location">`). The type string uses braces to reference a literal enum elsewhere in the system. The generator attempts array parsing and misinterprets the braces, leading to an error even though the spec parses semantically. The generator should either:
1. Accept `{Location}` and emit an appropriate Java type (possibly an enum or generated object), or
2. Fail earlier with a clearer message if literal-enclosed names are unsupported in this context.

**Suggested location to investigate:**
`ConvertUtils.getAttributeConvertedType()` and `JavaTypeFactory.splitTypeFromArrayDimension()` for handling of types that begin with `{`.

### 2. Invalid Java identifiers produced for join point with hyphen
**Spec:** `test-resources/spec/valid/edge/joinPointModel.xml` defines a join point `class="reserved-keyword"`.

Generated file: `build/tmp/gen-edge/edge/pkg/abstracts/joinpoints/AReserved-keyword.java` contains class declaration:
```java
public abstract class AReserved-keyword extends ALevel2 {
```
This is not a valid Java identifier (hyphen). Compilation would fail.

**Why this appears to be a bug:** The code generator should sanitize or reject invalid Java identifiers derived from join point names. Currently it emits them verbatim.

**Suggested investigation:** Where class names are constructed (likely in `AbstractJoinPointClassGenerator.generate` and related helper utilities). A sanitization step should map invalid characters (e.g., `-`) to `_` or camelCase and track original name separately for runtime JP type strings.

### 3. Duplicate method emission / shadowing in generated class
In `AReserved-keyword.java` there are duplicate method bodies for `getAImpl()` (appears twice). Example snippet:
```java
    @Override
    public Integer getAImpl() {
        return this.aLevel2.getAImpl();
    }
    ...
    @Override
    public Integer getAImpl() {
        return this.aLevel2.getAImpl();
    }
```
**Why this appears to be a bug:** Duplicate method declarations will cause a compile error (duplicate method signature). Indicates the inheritance composition logic or attribute propagation is adding the same attribute accessor twice when overriding.

**Suggested investigation:** Attribute propagation/generation logic in `AbstractJoinPointClassGenerator.addFieldsAndConstructors` and `GeneratorUtils.generateAttribute()` for repeated insertion when a hierarchy has repeated attribute names and multiple levels.

### 4. Generated accessor conflicts with `Object.getClass()`
In `AReserved-keyword.java` for attribute named `class`, the generator emits:
```java
public final Object getClass() {
```
This collides with `java.lang.Object#getClass()` (different return type), causing a compile-time error: *attempting to assign weaker access privileges; return type mismatch* or simply a signature conflict. Conventionally an attribute named `class` should be mapped to a safe method name like `getClassAttr()` while preserving logical attribute name for queries.

**Suggested investigation:** Method name derivation for attributes (likely `GeneratorUtils.generateAttribute`). Need a reserved-name list including Java keywords and core `Object` methods.

### Summary
| Issue | Impact | Priority Suggestion |
|-------|--------|---------------------|
| `{Location}` type parsing failure | Medium spec cannot be generated | High |
| Hyphen in class name | Generated code uncompilable | High |
| Duplicate accessor methods | Compilation failure | High |
| `getClass()` collision | Compilation failure | High |
| Absolute spec paths embedded in generated `buildLanguageSpecification()` | Non-portable goldens and path-dependent output | Medium |

These should be addressed before finalizing golden tests for code generation and compile (Stages 4–5), otherwise tests that compile generated sources will fail.
