# Stage 8 – Observations and Possible Bugs

This document collects issues or oddities noticed while implementing Stage 8 (performance benches and guard test).

## 1) JMH source set package path expectation
- Symptom: After creating benchmark classes under `src/jmh/java/...` with `package org.lara.interpreter.weaver.perf;`, the IDE reported:
  - “The declared package "org.lara.interpreter.weaver.perf" does not match the expected package "jmh.java.org.lara.interpreter.weaver.perf"”.
- Why it looks like a bug: Standard Gradle JMH setups expect `src/jmh/java` as a normal Java source set whose package names are independent from the directory name. The error suggests an IDE/sourceSet misconfiguration making the folder name `jmh/java` part of the expected package.
- Where: Gradle/JMH configuration in `WeaverInterface/build.gradle` and VS Code/IDE Java settings for source sets.
- Suggested fix:
  1) Verify the IDE recognizes `src/jmh/java` as a source root (JMH plugin should configure it). If not, mark it manually as a source root in IDE settings.
  2) In Gradle, ensure the JMH plugin adds the `jmh` source set properly (it usually does). Optionally, add:
     ```gradle
     jmh {
         jmhVersion = '1.37'
     }
     ```
     and reload the Gradle project.
  3) A quick workaround is to ignore the IDE warning; `gradle jmh` should still compile and run benches. If Gradle build also fails, adjust Java/Gradle toolchain or plugin version.

## 2) JoinPoint tree methods default to NotImplementedException (as designed)
- Observation: `JoinPoint#getJpChildrenStream`, `#getJpParent`, etc., by default throw `NotImplementedException`. Bench and guard tests implement minimal trees to avoid using unimplemented defaults. This is expected but worth noting for anyone trying to reuse `JoinPoint` directly in benches.

## 3) SourcesGatherer behavior with invalid paths
- Observation: `SourcesGatherer#addSource` logs and ignores non-existent paths (via `SpecsLogs.info("Ignoring source: "+source)`). This is fine for perf benches; just make sure noisy logs don’t pollute output when running many iterations.

If any of the above cause build failures on your setup, I can adjust Gradle config accordingly (e.g., explicitly configure `sourceSets { jmh { java.srcDir('src/jmh/java') } }`).

```text
File pointers:
- WeaverInterface/build.gradle (JMH plugin configuration)
- WeaverInterface/src/jmh/java/org/lara/interpreter/weaver/perf/*.java (benchmarks)
- WeaverInterface/src/org/lara/interpreter/weaver/utils/SourcesGatherer.java
- WeaverInterface/src/org/lara/interpreter/weaver/interf/JoinPoint.java
```