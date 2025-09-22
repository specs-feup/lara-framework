## Stage 6 Discovered Issues

Stage 6 (CLI/Task Integration) tests added basic coverage for help output, error handling on missing spec directory, and successful run messaging. While executing these tests, the following potential improvements / issues were noted:

1. Exit Code on Failure Not Verified
   - Current CLI (see `WeaverGenerator.main`) calls `System.exit(1)` only after printing an error. Tests invoke `main` directly, which makes asserting exit codes difficult without a security manager or System Lambda capturing `System.exit` calls. We currently rely on thrown exceptions for some error paths (e.g., invalid spec dir message produced indirectly). A more test-friendly design would avoid `System.exit` and instead throw a domain-specific exception or return an exit code value.

2. Ambiguous Exception for Missing Spec Directory
   - When an invalid spec directory is passed via `-x`, a generic `RuntimeException` with message `Language Specification directory is invalid` is thrown (indirectly). A dedicated exception type (e.g., `InvalidSpecificationDirectoryException`) would enable clearer assertions.

3. Deprecated Option Warning (`-c` prefix)
   - The warning for deprecated option `-C` (concrete classes) outputs a message but there is no test covering this path; also the help text and option builder define lowercase `c` while the warning refers to capital `C`.

4. Help Text Accessibility
   - The help option prints usage text but we do not assert presence of all documented options. Could add a test verifying that key options (-x, -o, -p, -w) appear to guard against regression.

### Recommendations
- Refactor `WeaverGenerator.main` into a smaller facade returning an int status code; keep `public static void main` as a thin wrapper that calls `System.exit(run(args))` for CLI while tests can call `run(args)`.
- Introduce domain exceptions for spec path / generation errors.
- Add test for deprecated option usage and ensure casing is consistent.
- Standardize all user-facing success messages to stdout (currently some tests capture no output; potential buffering or logging to other streams).

No blocking bugs were found that prevent existing Stage 6 tests from passing; listed items are improvements for robustness and testability.
