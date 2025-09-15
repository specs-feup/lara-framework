# Stage 2 — Potential Bugs and Oddities

This file lists potential bugs or surprising behaviors discovered while implementing Stage 2 tests for WeaverGenerator.

## 1) BaseGenerator.getJoinPoint(null) behavior via LanguageSpecification
- Location: `LanguageSpecification#getJoinPoint(String name)` (module: LanguageSpecification)
- Observation: The method does not null-check the `name` parameter prior to comparing with the base joinpoint name.
- Why this may be a bug: Accessing `equals` on a constant with `name` as `null` is safe due to constant-first comparison, but `joinPoints.get(name)` with `null` is also legal and returns `null`. In tests inside LanguageSpecification module there were expectations about handling null gracefully. WeaverGenerator Stage 2 tests do not rely on this, but be aware when passing `null`.
- Repro: Call `langSpec.getJoinPoint(null)`.

## 2) Error granularity: Unknown `extends` target
- Location: `LangSpecsXmlParser.parse(...)` (module: LanguageSpecification)
- Observation: When a `<joinpoint>` element has `extends="Nope"` (unknown), an exception is thrown later during linking via `langSpecV2.getJoinPoint(extendsType)` returning `null`, and `jp.setExtend(null)` later causes semantic inconsistencies or NPEs depending on usage. Currently an exception bubbles up but message may not be explicit.
- Why this may be a bug: Error message is not contextual (e.g., "Unknown extends target 'Nope' for class 'B'"). More actionable diagnostics would help Stage 2 acceptance criteria.
- Repro: Parse the provided XML in `SchemaValidationTest#unknownExtendsTargetFails`.

## 3) Duplicate joinpoint classes detection and message
- Location: `LanguageSpecification.add(JoinPointClass)` stores into a `LinkedHashMap` with the class name as key.
- Observation: Duplicates silently overwrite previous entries (due to `put` semantics) before later phases use them. Depending on parse order, this may or may not cause immediate failure.
- Why this may be a bug: The test assumes duplicates are rejected; current parser sorts and then `add`s, so duplicates will overwrite and only later steps may fail subtly.
- Suggestion: During parsing, detect duplicates explicitly and throw with a clear message.
- Repro: Parse XML in `SemanticValidationTest#duplicateJoinPointsRejected`.
