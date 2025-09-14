# WeaverInterface Testing Implementation Plan – Staged Execution

## Project overview
WeaverInterface provides the core interfaces, utilities, and adapters used by LARA weavers and interpreters. It defines:
- Join point and weaver contracts (`org.lara.interpreter.weaver.interf.*`)
- Event model and dispatch for actions/attributes (`weaver.events.*`, `weaver.interf.events.*`)
- Option mapping between CLI/store definitions and runtime (`weaver.options.*`, `joptions.*`)
- AST node bridging (`weaver.ast.*`)
- Source gathering helpers (`weaver.utils.SourcesGatherer`)
- Interpreter-side exceptions and a join point spec JSON generator launcher

Target JDK is 17. Dependencies include SpecsUtils, jOptions, Lara language specification modules, and treenode utilities. This plan defines staged work so multiple AI agents can implement tests in parallel with clear hand-offs.

## Global goals and quality gates
- Coverage
  - 95%+ overall; 100% public method coverage for core types (JoinPoint, WeaverEngine, EventTrigger, TreeNodeAstMethods, SourcesGatherer, WeaverOptionBuilder, NamedEnum)
  - Per-package Jacoco thresholds: 90% default; 95% for `weaver.events` and `weaver.ast`
- Stability and CI
  - JUnit 5 stack; Mockito (incl. static), AssertJ; deterministic tests with @TempDir
  - Build passes on Java 17; thresholds enforced in Gradle
- Performance
  - Microbenchmarks for event dispatch, source gathering, and descendant traversal; non-flaky guard tests

## Stage 0 — Foundations and Guardrails (Gradle, deps, coverage)
- Status: COMPLETED
- Objective: Enable modern testing with enforced coverage and optional performance benches.
- Inputs: `WeaverInterface/build.gradle`
- Tasks:
  - Add test dependencies: JUnit 5 (5.10.0), AssertJ (3.24.2), Mockito core/junit-jupiter/inline (5.x), JUnit-Pioneer (2.3.0), optional System Lambda (1.2.1)
  - `test { useJUnitPlatform() }`; configure test logging (show exceptions, standardStreams on failure)
  - Add Jacoco plugin and `jacocoTestCoverageVerification` with thresholds (90% default; 95% for events/ast)
  - Apply the JMH plugin `me.champeau.jmh` (chosen) with basic configuration; performance benches will be added in Stage 8
- Deliverables:
  - Updated `build.gradle` (JUnit5 + AssertJ + Mockito + Jacoco + JMH)
- Acceptance criteria:
  - `gradle test` runs JUnit 5; `jacocoTestReport` produces HTML; coverage verification task wired to `check`; `gradle jmh` task available

## Stage 1 — Cross-cutting Test Fixtures and Utilities
- Status: COMPLETED
- Objective: Provide shared test scaffolding enabling isolated, repeatable tests.
- Inputs: Source APIs; SpecsUtils/jOptions
- Tasks:
  - Implement TestWeaverEngine: concrete `WeaverEngine` with minimal `run`, `getOptions`, `buildLangSpecs`, `getRootJp`, `getAstMethods` (using a tiny `ATreeNode` tree via a minimal subclass)
  - Implement TestJoinPoint: concrete `JoinPoint` with parent/children, `insertImpl` behaviors (record invocations)
  - Implement TestGear: extends `AGear`, recording `onAction` and `onAttribute` events
  - Helpers: DataStore builders (CURRENT_FOLDER_PATH, USE_RELATIVE_PATHS), FS utilities for temp files/dirs, output capture when needed
  - Add a `FixturesSmokeTest` that instantiates the fixtures and wires `EventTrigger`
- Deliverables:
  - `test/.../fixtures/TestWeaverEngine.java`, `TestJoinPoint.java`, `TestGear.java`
  - `test/.../fixtures/TestDataStores.java`, `FsTestUtils.java`
  - `test/.../fixtures/FixturesSmokeTest.java`
- Acceptance criteria:
  - Compile passes; `FixturesSmokeTest` runs green

## Stage 2 — Core Interfaces: WeaverEngine and JoinPoint
- Objective: Validate core contracts and thread-local behavior.
- Dependencies: Stage 1 fixtures
- Tasks (tests):
  - WeaverEngine
    - `getName` default; `getNameAndBuild` with and without `SpecsSystem.getBuildNumber()` (static mocking)
    - StoreDefinition contains keys from `getOptions()`
    - Temporary weaver folder creation/caching/unicity; `hasTemporaryWeaverFolder`
    - Thread-local: `setWeaver`, idempotent set, conflict on different instance, `removeWeaver`
    - `writeCode` throws NotImplementedException by default
    - `getDefaultAttribute` returns default for supported JP; throws for unsupported
  - JoinPoint
    - `instanceOf("joinpoint")` always true; exact type; super chain using `getSuper`
    - `toString` format; `dump` tree text for small hierarchy
    - Descendant APIs: `getJpChildren`, `getJpDescendants`, stream variants
    - `getUndefinedValue` is not null and stable
- Deliverables:
  - `test/.../weaver/interf/WeaverEngineTest.java`
  - `test/.../weaver/interf/JoinPointTest.java`
- Acceptance criteria:
  - Tests pass; cover public methods and edge cases in these classes

## Stage 3 — Events Subsystem
- Objective: Ensure action/attribute event dispatch correctness.
- Dependencies: Stage 1 fixtures
- Tasks (tests):
  - `EventTrigger`
    - `hasListeners` false/true; register receivers individually and list
    - `triggerAction`/`triggerAttribute` BEGIN and END: verify stage, target, names, args, Optional result
  - `Stage`: `getName` lowercase; `toCode` format
  - Event data classes: getters; `ActionEvent.toString` contains stage/action/jp/args/result; `BaseEvent.toString` includes stage
- Deliverables:
  - `test/.../weaver/events/EventTriggerTest.java`
  - `test/.../weaver/interf/events/StageTest.java`
  - `test/.../weaver/interf/events/data/ActionEventTest.java`
  - `test/.../weaver/interf/events/data/AttributeEventTest.java`
- Acceptance criteria:
  - Tests pass; event payloads verified end-to-end through `TestGear`

## Stage 4 — AST Bridging
- Objective: Validate AST adapters and traversal semantics.
- Dependencies: Stage 1 fixtures
- Tasks (tests):
  - `AAstMethods`: `getDescendants` recursively collects all children in expected order; `getRoot` delegates to engine root
  - `DummyAstMethods`: each abstracted method throws NotImplementedException
  - `TreeNodeAstMethods`: children array, numChildren, parent, scopeChildren via provided function; `toJavaJoinPointImpl` and join point name mapping
- Deliverables:
  - `test/.../weaver/ast/AAstMethodsTest.java`
  - `test/.../weaver/ast/DummyAstMethodsTest.java`
  - `test/.../weaver/ast/TreeNodeAstMethodsTest.java`
- Acceptance criteria:
  - Tests pass; `weaver.ast` package meets 95% coverage threshold

## Stage 5 — Options & jOptions Integration
- Objective: Verify option mapping and path handling.
- Dependencies: Stage 1 helpers
- Tasks (tests):
  - `WeaverOptionBuilder`: short/long/description/args/argName/dataKey mapping; `build(DataKey<?>)` shortcut; `enum2List` mapping
  - `OptionArguments`: enum presence/values (sanity)
  - `FileList`: construction variants, `encode`/`decode` round trip using `SpecsIo` separators, iterator and `toString`; equals/hashCode behavior
  - `OptionalFile`: newInstance null/empty/non-empty; `encode`, `getCodec` round-trip, `toString` marks usage
  - `LaraIKeyFactory`:
    - `fileList`/`customGetterFileList` respect `isFolder`/`isFile`, no creation by default
    - `file` with create=true in folder mode creates dir; relative vs absolute behavior using `JOptionKeys.CURRENT_FOLDER_PATH` and `USE_RELATIVE_PATHS`
    - `optionalFile` custom getter processes underlying file when present
    - `customGetterLaraArgs` trims whitespace
  - `LaraiKeys`: `STORE_DEFINITION` contains all keys; expectations for defaults (e.g., `JAR_PATHS` empty `FileList`)
- Deliverables:
  - `test/.../weaver/options/WeaverOptionBuilderTest.java`
  - `test/.../weaver/options/OptionArgumentsTest.java`
  - `test/.../interpreter/joptions/keys/FileListTest.java`
  - `test/.../interpreter/joptions/keys/OptionalFileTest.java`
  - `test/.../interpreter/joptions/config/LaraIKeyFactoryTest.java`
  - `test/.../interpreter/joptions/config/LaraiKeysTest.java`
- Acceptance criteria:
  - Tests pass; path-related tests use @TempDir; no GUI components instantiated

## Stage 6 — Utilities
- Objective: Validate file/source gathering logic.
- Tasks (tests):
  - `SourcesGatherer` builds mapping from input roots to discovered files; filters by extensions; recurses into subdirectories; ignores invalid paths without exceptions
- Deliverables:
  - `test/.../weaver/utils/SourcesGathererTest.java`
- Acceptance criteria:
  - Tests pass; cover empty inputs, mixed files/folders, and multiple root sources

## Stage 7 — Integration (E2E) Scenarios
- Objective: Exercise realistic flows across subsystems.
- Dependencies: Stages 1–6
- Scenarios:
  1) Event flow through `JoinPoint.insert`/`insertFar` with `TestGear` registered; verify BEGIN/END pairs and payloads; toggle gear active off
  2) Options wiring: `TestWeaverEngine.getOptions()` -> `getStoreDefinition()` contains keys; set DataStore values and verify round-trip
  3) AST bridge: `TreeNodeAstMethods` from `TestWeaverEngine`; verify root, children, descendants, and name mapping
  4) JSON spec generation (safe): Build `LanguageSpecification` from `JoinPoint.getLaraJoinPoint()` -> node -> JSON string; assert non-empty and presence of expected top-level fields (no file writes)
- Deliverables:
  - `test/.../integration/WeaverInterfaceE2ETest.java`
- Acceptance criteria:
  - All scenarios pass; no external repositories or file writes outside @TempDir

## Stage 8 — Performance (Microbenchmarks)
- Objective: Baseline common hotspots; add non-flaky guard tests.
- Dependencies: Stage 1 fixtures
- Benches (JMH plugin in use):
  - `EventTriggerBench`: vary gears (1,5,10) and payload sizes; ops/s for `triggerAction` BEGIN/END
  - `SourcesGathererBench`: synthetic trees (e.g., 1k/10k files), varying extension list size
  - `DescendantsComputationBench`: N-ary tree with varied branching/depth; measure `getDescendants`
- Deliverables:
  - `src/jmh/java/.../*.java`
  - A minimal “guard” unit test that runs tiny-sized variants to ensure completion within a small time bound in CI
- Acceptance criteria:
  - Benches compile; `gradle jmh` runs locally; guard test passes quickly in CI

## Stage 9 — Finalization, Docs, and CI Polish
- Objective: Make the suite maintainable and enforced.
- Tasks:
  - Ensure Jacoco thresholds hooked to `check`
  - Add a `TESTING.md` (or extend this plan) documenting how to run unit, E2E, and perf tests, plus troubleshooting
  - Stabilize flaky tests (use JUnit-Pioneer retries sparingly; prefer fixing root causes)
- Deliverables:
  - Updated `build.gradle`, docs in repo, CI status green
- Acceptance criteria:
  - `gradle check` passes; coverage thresholds met; documentation present

---

## Additional specifications (reference for all stages)

### Test stack
- org.junit.jupiter:junit-jupiter:5.10.0
- org.assertj:assertj-core:3.24.2
- org.mockito:mockito-core:5.5.0; mockito-junit-jupiter:5.5.0; mockito-inline:5.2.0
- org.junit-pioneer:junit-pioneer:2.3.0
- com.github.stefanbirkner:system-lambda:1.2.1 (optional)
- org.openjdk.jmh:jmh-core/jmh-generator-annprocess:1.37 (optional for perf)

### Test organization
```
WeaverInterface/
  src/
  test/
    org/lara/interpreter/weaver/ast/
    org/lara/interpreter/weaver/events/
    org/lara/interpreter/weaver/interf/
    org/lara/interpreter/weaver/options/
    org/lara/interpreter/joptions/
    org/lara/interpreter/weaver/utils/
    org/lara/interpreter/
    integration/
  src/jmh/java/  (or src/perf/java/)
  test-resources/
```

### Edge cases and risks
- Paths: Always use `SpecsIo` and `@TempDir` to avoid platform issues
- Global state: WeaverEngine thread-local; clean with `removeWeaver()` in `@AfterEach`
- Static calls: Use mockito-inline for `SpecsSystem.getBuildNumber()`; ensure no leakage between tests
- GUI classes: Do not instantiate Swing panels; test only decoders and custom getters
- Launcher: Do not run `main()` that writes outside test dirs

### How to run (fish shell)
```fish
# From lara-framework/WeaverInterface
gradle test -x jacocoTestReport -x jacocoTestCoverageVerification

# With coverage report
gradle test

# If JMH plugin is used
gradle jmh
```

### Class inventory (for quick lookup)
- interpreter
  - LaraJoinPointJsonGeneratorLauncher
- interpreter.exception
  - ActionException, AttributeException, LaraIException
- interpreter.joptions
  - config.interpreter: LaraIKeyFactory, LaraiKeys
  - keys: FileList, OptionalFile
- interpreter.weaver.ast
  - AstMethods, AAstMethods, DummyAstMethods, TreeNodeAstMethods
- interpreter.weaver.events
  - EventTrigger
- interpreter.weaver.interf
  - WeaverEngine, JoinPoint, AGear, NamedEnum
  - events: LaraIEvent, Stage, data: BaseEvent, ActionEvent, AttributeEvent
- interpreter.weaver.options
  - OptionArguments, WeaverOption, WeaverOptionBuilder
- interpreter.weaver.utils
  - SourcesGatherer

Last updated: 2025-09-12
