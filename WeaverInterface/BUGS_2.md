# Phase 2 – Discovered Issues and Notes

This document records potential bugs and odd behaviors found while implementing Stage 2 tests for WeaverInterface.

## 1) JoinPoint.insertFar rethrows with wrong action name
- Location: `org.lara.interpreter.weaver.interf.JoinPoint`
  - Methods: `insertFar(String, String)` and `insertFar(String, T joinPoint)`
- Issue: In the `catch` blocks, both methods rethrow `new ActionException(get_class(), "insert", e)` (action name is "insert").
- Why this seems wrong: These methods are `insertFar` variants, so the action name should be "insertFar" for accurate error reporting and event correlation.
- How to find: Open `JoinPoint.java` and search for `insertFar(`; inspect the `catch` blocks.
- Suggested fix: Change the string literal from `"insert"` to `"insertFar"` in both methods.

## 2) Stale/incorrect method references in messages for insertFarImpl
- Location: `org.lara.interpreter.weaver.interf.JoinPoint`
  - Method stubs: `insertFarImpl(String, T JoinPoint)` and `insertFarImpl(String, String)`
- Issue: The exception messages reference `Action insert(String,joinpoint,boolean)` and `Action insert far(String,String)`.
- Why this seems wrong: The API surface for the public methods does not use a boolean overload anymore and the action names are `insertFar`, not `insert`/`insert far`.
- How to find: Open `JoinPoint.java`, look at the default implementations of the `insertFarImpl` methods.
- Suggested fix: Update exception messages to e.g. `"Action insertFar(String, joinpoint) not implemented"` and `"Action insertFar(String, String) not implemented"`. Also remove reference to the boolean parameter.

## 3) Javadoc mismatch referencing non-existent boolean overload
- Location: `org.lara.interpreter.weaver.interf.JoinPoint`
  - Javadoc `@see` for `insertFarImpl(String, T)` refers to `JoinPoint#insert(String, JoinPoint, boolean)`.
- Issue: There is no public API `insert(String, JoinPoint, boolean)`; this appears to be an outdated reference.
- Suggested fix: Update Javadoc to reference the existing `insertFar(String, T)`.

## 4) Test fixture gap: LanguageSpecification missing registration of declared join point
- Location: `test/org/lara/interpreter/weaver/fixtures/TestWeaverEngine.java`
- Issue: `buildLangSpecs()` constructed a `LanguageSpecification` with `new LanguageSpecification(jp, null)` but did not register the declared join point (`root`) into `joinPoints` via `spec.add(jp)`. As a result, `WeaverEngine.getDefaultAttribute("root")` looked up a null `JoinPointClass` and threw an exception, even though `root` exists in this test spec.
- Status: Adjusted the fixture to call `spec.add(jp); spec.setRoot(jp);` so lookups work. This is a fixture fix; not a product bug.
- How to find: Look at the `buildLangSpecs()` method in `TestWeaverEngine`.

## 5) Thread-local weaver get() behavior throws when unset (FYI)
- Location: `pt.up.fe.specs.util.utilities.SpecsThreadLocal`
- Behavior: `get()` throws `NullPointerException` with a diagnostic message when no value is set.
- Note: Tests were written accordingly (asserting the thrown exception when calling `WeaverEngine.getThreadLocalWeaver()` without setting a value). This seems by design, not a bug, but it’s a non-obvious behavior worth noting for future tests.

---
If you want, I can open small PRs to address 1–3 with targeted fixes and unit test assertions for the corrected messages/names.
