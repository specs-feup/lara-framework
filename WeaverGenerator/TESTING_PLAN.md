# WeaverGenerator Testing Implementation Plan – Staged Execution

## Project overview
WeaverGenerator is the code-generation backbone of the LARA Framework. It ingests Language Specification artifacts (XML-based join point/action/artifact models) and emits strongly-typed weaver scaffolding and adapters. Its responsibilities include:
- Parsing and validating XML specifications (join points, actions, artifacts)
- Building an internal semantic model (types, attributes, inheritance, selections)
- Consistency and schema validation, with actionable diagnostics
- Deterministic code generation for Java (weaver adapters, join point classes, event wiring) and related artifacts
- Incremental generation to `build/` or configured output folders, with up-to-date checks

Target JDK is 17. Dependencies include SpecsUtils, jOptions, and LanguageSpecification (schemas and helpers). This plan mirrors the staged approach used in WeaverInterface, adapted to a generator pipeline with golden/snapshot testing and end-to-end compilation checks.

## Global goals and quality gates
- Coverage
  - 95%+ overall; 100% public method coverage for core generator surfaces (spec validation facade, model builder, code generators, error reporter, output planner)
  - Per-package Jacoco thresholds: 92% default; 96% for spec parsing/validation and code generation packages
- Stability and CI
  - JUnit 5 stack; Mockito (incl. static), AssertJ; deterministic tests using fixed clocks, stable random seeds, normalized line endings
  - Build passes on Java 17; coverage and thresholds enforced automatically after `test` via finalized tasks (no reliance on `check`)
- Determinism
  - All generated outputs are byte-for-byte stable across runs/platforms; tests normalize EOL to '\n' and sort unordered collections at boundaries
- Performance
  - Microbenchmarks for spec parsing, model building, and generation under small/medium/large synthetic specs; CI guard tests ensure non-flaky bounds

## Stage 0 — Foundations and Guardrails (Gradle, deps, coverage)
- Objective: Enable modern testing with enforced coverage and optional performance benches.
- Inputs: `WeaverGenerator/build.gradle`
- Tasks:
  - Add test dependencies: JUnit 5 (5.10.0), AssertJ (3.24.2), Mockito core/junit-jupiter/inline (5.x), JUnit-Pioneer (2.3.0), optional System Lambda (1.2.1); add `testRuntimeOnly` JUnit Platform Launcher (1.10.0)
  - Configure `test { useJUnitPlatform(); maxParallelForks = max(1, Runtime.availableProcessors()/2) }` and finalize with coverage tasks: `test` finalizedBy `jacocoTestReport`; and `jacocoTestReport` finalizedBy `jacocoTestCoverageVerification`
  - Add Jacoco plugin and `jacocoTestCoverageVerification` with thresholds (92% default; 96% for parsing/validation and generation packages)
  - Apply the JMH plugin `me.champeau.jmh` version `0.7.2`, with `jmh { duplicateClassesStrategy = DuplicatesStrategy.WARN }` and a dedicated `sourceSets { jmh { java { srcDir 'src-jmh' } } }`
  - Optional: Property-based testing with jqwik (1.8+) for model invariants (can be staged later)
- Deliverables:
  - Updated `build.gradle` (JUnit5 + AssertJ + Mockito + Jacoco + JMH)
- Acceptance criteria:
  - `gradle test` runs JUnit 5 and automatically triggers `jacocoTestReport` then `jacocoTestCoverageVerification` (coverage gating after tests); HTML/XML reports produced; `gradle jmh` task available; no reliance on `check`

## Stage 1 — Cross-cutting Test Fixtures and Utilities
- Objective: Provide shared scaffolding enabling deterministic, isolated tests.
- Tasks:
  - Spec builder utilities: tiny DSL/helpers to construct in-memory spec fragments and/or write minimal XML spec files into `@TempDir` (joinPointModel.xml, actionModel.xml, artifacts.xml)
  - File-system harness: temp output roots, deterministic path normalizer, newline normalizer, and content diff helper with unified diff on failure
  - Clock and randomness control: fixed `Clock` and stable `Random` seed if the generator touches timestamps or randomized names (should not, but guard anyway)
  - Golden/snapshot support: convention-based expected files living under `test-resources/golden/<scenario>/...` with a normalizer that removes non-semantic noise (headers, timestamps) while preserving structure
  - Smoke test: ensure fixtures can write/read a minimal spec triplet and the generator pipeline can be invoked (without asserting contents yet)
- Deliverables:
  - `test/.../fixtures/SpecXmlBuilder.java`, `FsTestUtils.java`, `DiffUtils.java`, `GoldenIO.java`, `Determinism.java`
  - `test/.../fixtures/FixturesSmokeTest.java`
- Acceptance criteria:
  - Compile passes; `FixturesSmokeTest` runs green on Linux with Java 17

## Stage 2 — XML Schema and Semantic Validation
- Objective: Validate parsing and semantic checks independently from code generation.
- Tasks (tests):
  - Schema validation: invalid XML fails with line/column and meaningful error messages; valid minimal spec passes
  - Join point hierarchy: cycles detected; unknown `extends` rejected; diamond inheritance handled per spec rules
  - Select relations: unknown selectors rejected; alias duplication detected; recursive selections validated
  - Actions and attributes: undefined targets reported; type mismatches surfaced; duplicate names resolved with deterministic precedence or error
  - Artifact definitions: required attributes enforced; default values and codecs validated
  - Error reporting: aggregator collects multiple issues; messages carry context (file, element, id) and are sorted deterministically
- Deliverables:
  - `test/.../spec/SchemaValidationTest.java`
  - `test/.../spec/SemanticValidationTest.java`
  - `test-resources/spec/invalid/*` and `test-resources/spec/valid/*` sample specs
- Acceptance criteria:
  - Tests pass; validator surfaces multiple errors in a single run with stable ordering

## Stage 3 — Model Building and Invariants
- Objective: Ensure internal model correctness before codegen.
- Tasks (tests):
  - Type graph: resolved supertypes, interfaces, and attributes inherited/overridden as expected
  - Selections: transitive closure computed; queries over model return stable, predictable sets
  - Actions: parameter lists fully resolved (names, types, default values), return types mapped
  - Invariants: no dangling references; all ids unique in scope; topological ordering reproducible
  - Optional property-based checks (if jqwik enabled): generate tiny random trees/specs and assert invariants (acyclic, referential integrity)
- Deliverables:
  - `test/.../model/ModelBuilderTest.java`
  - `test/.../model/ModelInvariantsPropertyTest.java` (optional)
- Acceptance criteria:
  - Tests pass; coverage for model classes ≥96%

## Stage 4 — Code Generation (Golden Tests)
- Objective: Verify emitted code using golden files with normalization.
- Tasks (tests):
  - Minimal spec -> generate Java join points, weaver skeletons, event wiring; compare to goldens
  - Medium spec with inheritance, multiple actions/attributes; ensure method signatures, Javadoc, and annotations match expectations
  - Edge cases: no actions, only attributes; deep inheritance; reserved keywords in identifiers (validated or sanitized)
  - Determinism: repeated generation identical; path-insensitive operation; newline-insensitive comparison (normalize to `\n`)
  - Incremental overwrite: generating twice does not duplicate sections; regenerated files preserve regions marked as generated-only vs user sections (if applicable)
- Deliverables:
  - `test/.../codegen/JavaCodegenGoldenTest.java`
  - `test-resources/golden/minimal/**`, `medium/**`, `edge/**` expected outputs
- Acceptance criteria:
  - Golden diffs clean; tests pass across Linux/macOS/Windows line endings

## Stage 5 — End-to-End Generation and Compile Checks
- Objective: Exercise the full pipeline and validate that output compiles.
- Tasks (tests):
  - E2E: write a valid spec triplet to `@TempDir`, run the generator to an output folder
  - Compile generated Java sources in-memory (JavaCompiler API) or via a lightweight Gradle Tooling API invocation within `@TempDir`
  - Optional: execute a tiny smoke usage (reflectively instantiate a generated join point and call a trivial method) if possible without external runtime
  - Verify no external I/O outside `@TempDir`; verify classpath minimality
- Deliverables:
  - `test/.../e2e/GeneratorE2ETest.java`
- Acceptance criteria:
  - Generated sources compile; basic reflective invocation works (if applicable)

## Stage 6 — CLI/Task Integration (if applicable)
- Objective: Validate entrypoint/CLI wiring or Gradle task integration provided by WeaverGenerator.
- Tasks (tests):
  - Option parsing: required flags, defaults, and error reporting on bad inputs
  - Input discovery: resolve spec files from folder or explicit paths; ignore non-XML; follow includes if supported
  - Output planning: respects target folder, cleaning/overwrite flags, and dry-run mode producing plan only
  - Exit codes and messages: non-zero on validation/generation failure; helpful summaries printed
- Deliverables:
  - `test/.../cli/GeneratorCliTest.java`
- Acceptance criteria:
  - CLI behaves deterministically; no writes outside `@TempDir`

## Stage 7 — Performance (Microbenchmarks)
- Objective: Baseline and guard against regressions.
- Benches (JMH):
  - `SpecParsingBench`: varying file counts and sizes
  - `ModelBuildBench`: varying nodes/edges counts
  - `CodegenBench`: varying number of join points/actions
  - JMH configured with plugin `0.7.2`, `duplicateClassesStrategy = WARN`, and sources under `src-jmh`
- Guard tests:
  - Tiny-size versions run as unit tests with strict timeouts to detect gross regressions without flakiness
- Deliverables:
  - `src/jmh/java/.../*.java`
  - `test/.../perf/PerfGuardTest.java`
- Acceptance criteria:
  - Benches compile; `gradle jmh` runs locally; guard test passes quickly in CI

## Stage 8 — Finalization, Docs, and CI Polish
- Objective: Make the suite maintainable and enforced.
- Tasks:
  - Ensure the finalized task chain remains intact: `test` -> `jacocoTestReport` -> `jacocoTestCoverageVerification` (no dependency on `check`)
  - Add a `TESTING.md` summarizing how to run unit, E2E, and perf tests, and troubleshooting (schema path issues, line endings)
  - Stabilize any flaky tests (prefer fixing root causes; use JUnit-Pioneer retries sparingly)
  - Document golden update workflow and review policy (never update snapshots blindly)
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
- Optional: net.jqwik:jqwik:1.8.0 (property-based invariants, Stage 3)

### Test organization
```
WeaverGenerator/
  src/
  test/
    .../fixtures/
    .../spec/
    .../model/
    .../codegen/
    .../cli/
    .../e2e/
    .../perf/
  src-jmh/
  test-resources/
    spec/valid/
    spec/invalid/
    golden/minimal/
    golden/medium/
    golden/edge/
```

### Edge cases and risks
- Line endings and encodings: always normalize to `\n` and UTF-8; avoid platform-dependent headers and timestamps
- Deterministic ordering: sort maps/sets at boundaries (e.g., attribute lists, child join points) to keep output stable
- Path handling: use `SpecsIo` consistently; all tests must operate within `@TempDir`
- Schema availability: ensure XML schemas are reachable on the classpath; if relying on LanguageSpecification resources, configure test dependency properly
- Reserved identifiers: handle Java keywords and invalid identifiers via sanitization or clear validation errors
- Incremental generation: ensure idempotency when re-running against existing outputs (no duplication, consistent overwrites per policy)
- Global state: any singletons or caches must be reset between tests

### How to run (fish shell)
```fish
# From lara-framework/WeaverGenerator
# Run tests; coverage report and coverage verification will run automatically afterward via finalized tasks
gradle test

# Generate JMH benchmarks
gradle jmh
```

### Acceptance definitions per artifact type
- Validator: rejects malformed specs with actionable, contextual errors; accepts valid specs; stable ordering of diagnostics
- Model: structurally sound (no cycles, no dangling refs); inheritance and selections computed correctly; deterministic traversal
- Codegen: golden-equal after normalization; idempotent regeneration; compilable outputs
- CLI/task: correct exit codes and messages; safe I/O scope within designated temp directories during tests

### Golden testing policy
- Goldens live under `test-resources/golden/<scenario>/...`
- Differences shown with unified diff; updates require explicit reviewer approval
- Normalizers must only remove non-semantic noise (e.g., timestamps) and must be tested themselves

### CI considerations
- Split tests into fast (unit/model/validation) and slower (golden/e2e) groups if CI time is tight
- Cache Gradle and test-resources between CI runs where possible
- Avoid network access in tests (schemas must be resolved locally)

---

## Requirements coverage
- High coverage and thresholds: defined and enforced via Jacoco (Done in plan)
- Deterministic outputs and golden tests: defined with normalization and idempotency checks (Planned)
- E2E generation + compile: defined using in-memory compiler or isolated tooling (Planned)
- Performance baselines: JMH benches + guard tests (Planned)
- Docs and CI wiring: testing guide and coverage enforcement (Planned)

> This plan mirrors the rigor of WeaverInterface while addressing generator-specific needs: schema+semantic validation, deterministic golden testing, idempotent incremental runs, and compile-verified outputs.
