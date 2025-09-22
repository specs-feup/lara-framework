# Stage 7 Findings and Suspected Bugs

## 1) Option enum getLongOption returns short option (likely bug)

- Location: `WeaverGenerator/src/org/lara/interpreter/weaver/generator/commandline/WeaverGeneratorOptions.java`
- Code: method `public String getLongOption()` in `GeneratorOption` enum returns `option` instead of `longOption`.
- Why it's a bug: The intention of `getLongOption()` is to return the long form (e.g., `--help` extracts `help`). Returning the short option will make `newOption(...).longOpt(shortOpt.longOption)` set the same value as the short option, potentially duplicating or misreporting long option names.
- How to find: Open the file and inspect the enum; compare fields `option` vs `longOption`. Also observe `newOption(...)` uses `shortOpt.longOption` (the field), not `getLongOption()`. This inconsistency is confusing and can cause misuse. Consider fixing by returning `longOption`.

## 2) Default node type may be surprising

- Location: `generator/utils/GenConstants.java` constant `defaultNodeType = Object.class.getSimpleName()`
- Why it might be an issue: It sets default node type to `"Object"` (simple name), while other code paths expect canonical names (e.g., `Class.getCanonicalName()`), leading to potential resolution issues if ever used to generate imports or types. Not a fault now because code treats as raw string, but worth revisiting.
- How to find: Search usages of `getNodeType()`, see `JavaAbstractsGenerator.setJavaTypes()` creating `new JavaType(getNodeType())`. If later tooling assumes fully-qualified type, this might be insufficient.

## 3) Cleanup responsibility in benchmarks and tests

- Observation: For temp directories, multiple places do best-effort deletes. If run on Windows, deletion order matters. In our code we sort paths by depth descending; if any file remains locked (e.g., by compiler), cleanup may fail silently. Not a product bug, but a potential flake source if patterns are reused elsewhere.

## 4) XML header in ModelBuildBench was malformed (fixed in benches)

- Symptom: JMH warmup failed with a fatal XML parse error: “The value following ‘version’ in the XML declaration must be a quoted string.” when building the in-memory artifacts.xml in `ModelBuildBench`.
- Root cause: The XML declaration string had improperly escaped quotes, producing an invalid header for the artifacts spec.
- Status: Fixed by writing a proper XML prolog in `ModelBuildBench` setup. After the fix, `ModelBuildBench` runs successfully for params {0, 10, 100}.
- How to reproduce pre-fix: Run `gradle jmh -Pjmh.include=ModelBuildBench` (would crash during warmup previously).

## 5) JMH default settings lead to very long runs; benches now annotate fast config

- Observation: A plain `gradle jmh` run (with JMH defaults) took ~1h+ initially. For CI/dev, the benches are annotated with `@Warmup(iterations=1, time=1s)`, `@Measurement(iterations=1, time=1s)`, and `@Fork(1)` to keep runs short.
- Recommended: For exploratory benchmarking, override via Gradle properties (e.g., `-Pjmh.warmupIterations=5 -Pjmh.measurementIterations=5 -Pjmh.forks=2`) or by removing the fast annotations locally.
- Note: Results are saved under `build/results/jmh/results.txt`.

## 6) Logging noise during bench/test runs (not a functional bug)

- Messages like “Make sure to make this project import the following projects …” and “java class for the weaver 'BenchWeaver' was not created because the file already exist on the path!” appear frequently during JMH and tests.
- Impact: None on correctness; just noisy. Consider gating these with a less verbose log level for cleaner bench outputs.
