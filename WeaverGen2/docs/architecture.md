# WeaverGen2 Architecture

WeaverGen2 is split into a small command-line layer, a programmatic generation API, a pipeline that plans artifacts, and low-level emitters that produce source text.

## Layers

- `org.lara.weavergen2.cli`
  - Owns command-line parsing and process-facing messages.
  - The distribution main class is `org.lara.weavergen2.cli.WeaverGen2Cli`.

- `org.lara.weavergen2.api`
  - Public Java API for programmatic generation.
  - `WeaverGenerationRequest` is the input contract.
  - `WeaverGenerationResult` reports generated artifacts, concrete source changes, and diagnostics.
  - `WeaverGenerator` is the service entrypoint.

- `org.lara.weavergen2.pipeline`
  - Coordinates generation through focused collaborators.
  - `SpecResolver` builds LangSpec2 models and creates a `GenerationProfile`.
  - `ArtifactPlanner` creates generated artifacts.
  - `GeneratedArtifactFactory` centralizes package-to-path and artifact identity rules.
  - `FinalWrapperCatalog` isolates inherited final-wrapper discovery.
  - `GenerationPipeline` only orchestrates those components and reports concrete-source validation failures.

- `org.lara.weavergen2.io`
  - Persists planned artifacts to disk.
  - It is the only layer responsible for writing generated artifacts.

- `org.lara.weavergen2.source`
  - Owns concrete joinpoint source handling.
  - `ConcreteSourceIndex` scans source files.
  - `ConcreteSourceParser` extracts package/class/constructor information.
  - `ConcreteSourceValidator` checks duplicates, declarations, and unexpected files.
  - `ConcreteSourceStubFactory` creates missing concrete stubs.
  - `ConcreteJoinPointSources` is the source subsystem facade used by generation.

- `org.lara.weavergen2.emit`
  - Emits individual Java source sections and type bridges.
  - Joinpoint class emission is split into imports, class declaration, constructors, type rendering, public wrappers, typedefs, and enums.
  - `JoinPointMemberEmitter` owns abstract member method emission and wrapper suppression rules.

- `org.lara.weavergen2.generator`
  - Contains only high-level generator facades that sequence emitters, plus DOT/weaver generation.
  - Filesystem-derived concrete-source information is passed through explicit collaborators.

- `org.lara.weavergen2.model`
  - Contains WeaverGen2-side generation models such as `GenerationProfile`.
  - `JoinPointMember`, `MemberSignature`, and `WrapperSignature` are the shared primitives for attribute/action naming and signature comparison.
  - `SpecModelMerger` owns base/weaver model merge and reference rewrite behavior.

- `org.lara.weavergen2.java`
  - Contains Java source-building and type/name utility code used by emitters.

## Pipeline

1. `WeaverGen2Cli` converts command-line arguments into a `WeaverGenerationRequest`.
2. `WeaverGenerator.generate()` asks `GenerationPipeline` to plan generation.
3. `SpecResolver` builds the weaver model and optional base model.
4. If a base spec exists, `SpecModelMerger` produces the merged output model.
5. `ConcreteJoinPointSources` applies the configured `ConcreteSourcePolicy`.
6. `ArtifactPlanner` invokes emitters to produce `GeneratedArtifact` values.
7. `ArtifactWriter` writes artifacts to the target filesystem.
8. The pipeline reports non-conforming concrete source files after generated artifacts are written, preserving the previous developer workflow.

## Concrete Source Policy

`ConcreteSourcePolicy` controls project concrete joinpoint source handling:

- `DISABLED`: do not scan or create concrete sources.
- `VALIDATE_ONLY`: scan and validate existing concrete sources without creating missing files.
- `CREATE_MISSING_AND_VALIDATE`: create missing concrete source stubs and validate the complete set.

The default CLI policy is `CREATE_MISSING_AND_VALIDATE`.

Generated concrete joinpoint stubs intentionally remain concrete classes. They are not expected to implement every generated abstract `*Impl` method.

## Wrapper Conflicts

Public wrapper methods are generated for spec attributes/actions unless they conflict with inherited final wrappers from the base joinpoint contract.

The generator skips only the public wrapper in that case. It still emits the underlying abstract `*Impl` method when the model requires it.

Example: a spec may declare a `root` attribute, but `ALaraJoinPoint` already exposes final `root()`. WeaverGen2 must not emit another public final `root()` in a subclass.

## Testing

Primary local checks:

```bash
gradle test
```

Cross-project integration check:

```bash
cd ../../clava/ClavaWeaver
gradle installDist
```

The large integration compile test filters only the intended concrete-stub abstract-method diagnostics. Other generated-code compilation errors remain failures.
