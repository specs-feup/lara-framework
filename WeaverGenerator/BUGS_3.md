## Stage 3 Suspected Issues / Oddities

### 1. Attribute model schema ordering prevents using `<typedef>` / `<object>` after `<enum>`
While implementing the `arrayAndCustomTypes` test, multiple attempts to include custom type declarations failed schema validation:

```
<artifacts>
    <enum name="Color"> ... </enum>
    <typedef name="Vec2"> ... </typedef>  <!-- or <object name="Vec2"> -->
    <artifact class="A"> ... </artifact>
</artifacts>
```

Parser error (representative):
```
cvc-complex-type.2.4.a: Invalid content was found starting with element 'object'. One of '{enum}' is expected.
```

After replacing `<object>` with `<typedef>`, the error persisted (now complaining about `<artifact>`). This strongly suggests the XSD enforces a strict sequence such as:

```
<artifacts> enum* artifact* </artifacts>
```

Meaning: no typedef/object sections allowed, or their element names differ from what the DSL parser expects. However, `LangSpecsXmlParser` explicitly processes:
* `attributeModelNode.getElementsByName("object")`
* `attributeModelNode.getElementsByName("typedef")`
* `attributeModelNode.getElementsByName("enum")`

So the Java side supports these constructs, but the schema appears not to (at least in the order used). This mismatch blocked testing mixed enum + typedef scenarios. The test was simplified to avoid custom typedefs altogether.

Location hints:
* Parser: `LanguageSpecification/src/pt/up/fe/specs/lara/langspec/LangSpecsXmlParser.java` (loops around lines ~40–120)
* Schema resource: likely under `LanguageSpecification/resources` (not inspected in detail yet). Search for XSD defining `<artifacts>` content model.

Why this is likely a bug:
* DSL parsing code expects and supports `<object>` and `<typedef>` definitions.
* Schema rejects `<object>` (and seemingly `<typedef>`) when placed after `<enum>` elements, preventing legitimate model compositions.

Suggested next steps:
1. Inspect the XSD for the `artifacts.xml` to confirm allowed child element order.
2. Align schema with parser expectations (allow a flexible ordering: `(enum|object|typedef|artifact)*`).
3. Add tests covering a mix of enum + typedef + artifact once schema is updated.

### 2. Lack of explicit validation for cycles (informational)
Cycle detection currently relies on parsing order and will throw only if a referenced parent does not exist. A purposely cyclical definition cannot be expressed due to forward reference limitations, but there is no explicit cycle guard. This is acceptable for now but could merit an invariant check if future tooling allows reordering or multi-pass parsing.

---
Document created during Stage 3 test implementation.