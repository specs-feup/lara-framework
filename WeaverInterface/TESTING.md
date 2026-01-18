# WeaverInterface Testing Guide

This document explains how to run the unit, integration, and performance tests for the WeaverInterface module, and common troubleshooting tips.

## Prerequisites
- Java 17+ installed and active (`java -version` should report 17 or newer)
- Gradle 8.9+ available on PATH
- This project uses composite builds. The following sibling projects must be present alongside this module:
  - specs-java-libs/jOptions
  - specs-java-libs/SpecsUtils
  - specs-java-libs/tdrcLibrary
  - lara-framework/LanguageSpecification
  - lara-framework/LaraUtils

These are already referenced via `settings.gradle` using `includeBuild(...)`.

## Test stacks
- JUnit 5 (5.10.0)
- AssertJ (3.24.2)
- Mockito (core/junit-jupiter/inline 5.x)
- JUnit-Pioneer (2.3.0)
- System Lambda (1.2.1)
- Jacoco for coverage
- JMH plugin for microbenchmarks

## How to run (fish shell)
```fish
# From lara-framework/WeaverInterface
# Fast run without generating coverage report on every run
gradle test -x jacocoTestReport -x jacocoTestCoverageVerification

# Full run with coverage report and verification
gradle test

# Run JMH microbenchmarks (requires JDK 17+)
gradle jmh
```

Coverage reports are generated under `build/reports/jacoco/test/html/index.html`.

## Notes on composite builds
Running `gradle test` in this module will automatically build required projects from the sibling repos via `includeBuild`. You should not need to manually build `SpecsUtils`, `jOptions`, etc., as long as they exist at the paths declared in `settings.gradle`.

## Troubleshooting
- Using Java 21: The build runs on Java 17+, but you might see a JVM warning about dynamic agents. It is harmless for the tests. If you need to hide it, set `-XX:+EnableDynamicAgentLoading`.
- If Gradle complains about missing sibling projects, confirm the relative paths in `settings.gradle` match your workspace structure.
- Slow/large tests: Most tests are unit-level. If your machine is resource constrained, reduce parallelism:
  ```fish
  gradle test --max-workers 1
  ```
- File system issues: All tests use `@TempDir` and should not write outside the project. If you see permission errors, ensure your TMPDIR is writable.

## What’s enforced
- Coverage thresholds: 90% overall bundle; 95% for `org.lara.interpreter.weaver.events` and `org.lara.interpreter.weaver.ast`.
- Coverage verification runs and is wired to `check`.

## Performance guards
JMH sources live under `src-jmh`. A small guard test (`perf/PerfGuardTest.java`) ensures tiny-sized benchmarks complete quickly. To run full benches, use `gradle jmh`.

## CI tips
- Prefer `gradle build` to catch coverage regressions.
- Use `--info` or `--stacktrace` for deeper logs.
