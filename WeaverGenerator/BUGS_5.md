## Stage 5 Discovered Issues

During Stage 5 (End-to-End generation + compile) no new breaking issues surfaced beyond those already listed in `BUGS_4.md` because the E2E test purposely restricts itself to the `minimal` specification so compilation succeeds.

However, the following latent problems remain blockers for expanding compile tests to `edge` and `medium` scenarios:

1. Invalid Java identifiers (hyphen) in join point class names (see BUGS_4.md #2) cause compilation failure if `edge` spec is included.
2. Duplicate accessor emission and `getClass()` collision (BUGS_4.md #3 and #4) also prevent compiling `edge` spec.
3. `{Location}` attribute type parsing error (BUGS_4.md #1) prevents code generation for `medium` spec, thus E2E cannot cover a richer scenario.

### Suggested Next Steps
- Implement identifier sanitization (map non-java identifier chars to `_` and avoid collisions with keywords and `Object` methods).
- Introduce a reserved-name remapping strategy for attributes named `class`, `default`, etc. (e.g., append `Attr`).
- Extend type parsing to recognize object references written as `{TypeName}` or fail fast with clear diagnostic.
- After fixes, broaden `GeneratorE2ETest` to compile `edge` (post-sanitization) and `medium` (post-type parsing fix).

### Newly Observed Issue During Compilation Attempt
- Generated concrete weaver (e.g., `MinimalWeaver`) does not implement abstract method `getRootJp()` required by `WeaverEngine`, leading to compilation error:
	`error: MinimalWeaver is not abstract and does not override abstract method getRootJp()`.
	This indicates the generator either relies on a different base abstract class (e.g., previously `LaraWeaverEngine`) or lost an implementation step during refactoring. Needs generator update to emit a proper `getRootJp()` implementation delegating to the root join point instance.

No additional issues specific to Stage 5 were observed at this time.
