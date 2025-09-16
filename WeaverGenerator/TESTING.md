# WeaverGenerator – Testing Guide

This document summarizes how to run unit, E2E, and performance tests for WeaverGenerator, how coverage is enforced, and how to work with golden files. It complements the staged testing plan in `TESTING_PLAN.md`.

## Quick start

- Java: 17+
- Build tool: Gradle (system installation)
- Directory: run all commands from `lara-framework/WeaverGenerator`

### Run the test suite (with coverage gating)
```bash
# Executes tests with JUnit 5
# Then automatically runs Jacoco report and coverage verification via finalized tasks
gradle test
```

### Open the coverage report
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## Coverage enforcement

The Gradle build is configured so that:
- `test` is finalized by `jacocoTestReport`.
- `jacocoTestReport` is finalized by `jacocoTestCoverageVerification`.
- No reliance on `check` for coverage enforcement: running `gradle test` alone will still run coverage and fail the build if thresholds are not met.

Current thresholds, see `build.gradle` under `jacocoTestCoverageVerification`:
- Global bundle: 92%+
- Parsing/validation and codegen packages: 96%+

## Test layout and conventions

Expected layout:
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

- Unit tests use JUnit 5 + AssertJ + Mockito (incl. inline/static).
- Determinism helpers (fixed clocks, stable seeds, newline normalization) should be used to avoid platform differences.
- All tests must be self-contained and write only inside `@TempDir`.

## Golden files workflow

Golden tests compare generated code to expected files stored under `test-resources/golden/<scenario>/...`.

- Keep goldens minimal and readable. Normalize line endings to `\n`, remove timestamps and other non-semantic data in the test harness before comparison.
- When a legitimate change in generation occurs:
  1. Run the failing golden test and inspect the unified diff printed by the test utilities.
  2. Manually update the corresponding golden file(s) under `test-resources/golden/...`.
  3. Review the changes to ensure only intended, semantic differences are captured.
  4. Re-run tests to confirm the diffs are clean.
- Never auto-accept golden updates blindly. Goldens act as a spec for the emitted code.

## End-to-end (E2E) checks

E2E tests should:
- Write minimal valid spec triplets (joinPointModel.xml, actionModel.xml, artifacts.xml) into a temporary directory.
- Run the generator to an output folder under that temp directory.
- Optionally compile emitted Java sources in-memory using the JavaCompiler API to ensure they are self-consistent.

These tests should not access the network and should not write outside the test temp directory.

## CLI/Task tests

If the generator exposes a CLI or Gradle task behavior:
- Test option parsing (required flags, defaults, invalid inputs) deterministically.
- Verify discovery of spec files, output planning, and dry-run behavior.
- Ensure non-zero exit codes on validation/generation failures.

## Performance (JMH)

Microbenchmarks are configured via the JMH Gradle plugin:
```bash
gradle jmh
```

Place benchmark sources under `src-jmh/`. For CI guard tests, include tiny, non-flaky checks under `test/.../perf/PerfGuardTest.java` with strict timeouts.

## Troubleshooting

- Java/JUnit detection issues: ensure Java 17+ is active and Gradle sees it (`java -version`).
- Coverage verification runs automatically after `test`. If your test run ends without a coverage report, check that you ran `gradle test` (not `gradle tasks` or similar).
- Line endings: always normalize to `\n` in tests. Goldens must use `\n` on disk.
- Classpath: tests should depend only on the modules defined in `build.gradle`. Avoid relying on global Gradle caches or external processes.
- Schemas: if XML schemas are needed, ensure they are resolved from classpath resources (no network access).

## Version matrix (libraries)
- JUnit Jupiter: 5.10.0
- AssertJ: 3.24.2
- Mockito: 5.x (core + junit-jupiter + inline)
- JUnit Pioneer: 2.3.0
- System Lambda: 1.2.1 (optional)
- JMH: plugin 0.7.2

## Policy notes
- Determinism first: sort collections at boundaries and use fixed clocks/seeds where relevant.
- Idempotency: code generation should be repeatable and not duplicate sections on re-run.
- Golden changes require reviewer approval and must be justified in commit messages.

---

For deeper background and stage-by-stage details, see `TESTING_PLAN.md`.
