# Stage 7: Potential Bugs and Oddities

This file documents issues or suspicious behaviors observed while implementing the Stage 7 integration tests.

## 1) Lack of JSON serialization API for LanguageSpecification
- Location: lara-framework/LanguageSpecification (project), classes `LanguageSpecification`, `JoinPointClass`
- Observation: The testing plan mentions “a join point spec JSON generator launcher,” and Stage 7 requests a safe JSON spec generation check. However, there is currently no public API in `LanguageSpecification` to serialize specifications to JSON (no `toJson()` or similar). Only a DSL `toString()` is provided.
- Why this might be a bug: The plan suggests JSON generation should be available on the interpreter side, but WeaverInterface and LanguageSpecification do not expose a JSON converter here. This causes ambiguity for tests. We used `toString()` DSL assertions as a surrogate.
- How to reproduce: Search for JSON-related methods in the project (e.g., grep for “json”, “toJson”). None are found in these modules.
- Suggested fix: Provide a lightweight JSON serializer (e.g., using Jackson or a minimal hand-rolled converter) for `LanguageSpecification` and/or add a small launcher in WeaverInterface to generate the JSON string without file I/O.

## 2) JoinPoint insertFar default implementations throw by design, but fixtures needed overrides
- Location: `WeaverInterface/src/org/lara/interpreter/weaver/interf/JoinPoint.java` (default `insertFarImpl` throws), and our test fixture `test/.../fixtures/TestJoinPoint.java`.
- Observation: `JoinPoint.insertFar(...)` methods wrap calls in try/catch and trigger BEGIN/END events around `insertFarImpl`. Since the default `insertFarImpl` throws `UnsupportedOperationException`, any direct use in integration tests would result in an exception rather than END events.
- Why noteworthy: For integration testing of event flow, a minimal no-op implementation was needed. We added overrides to `TestJoinPoint` to avoid exceptions and allow END events to be dispatched. Not a bug per se, but important for test scaffolding.
- How to reproduce: Call `new TestJoinPoint("x").insertFar("before", "code")` without our overrides—it would throw if using the base class default.
- Suggested fix: None needed in production; fixtures already implement it. Consider documenting this requirement for test doubles.
