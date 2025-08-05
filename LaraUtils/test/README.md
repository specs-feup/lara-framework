# LaraUtils Test Suite

This directory contains comprehensive unit tests for all Java classes in the LaraUtils project.

## Test Structure

### Test Classes

1. **UndefinedValueTest** - Tests for the singleton JavaScript undefined value representation
2. **JavaDiffHelperTest** - Tests for file and string diff utility methods
3. **LaraResourceProviderTest** - Tests for the resource provider interface and its default methods
4. **LaraSystemToolsTest** - Tests for system command execution utilities
5. **LARAExceptionBuilderTest** - Tests for LARA exception building and formatting
6. **BaseExceptionTest** - Tests for the abstract base exception class

### Test Resources

- `test-resources/` - Contains sample files for testing file operations
  - `original.txt` - Sample original file for diff testing
  - `revised.txt` - Sample revised file for diff testing

## Running Tests

### Using Gradle

Run all tests:
```bash
./gradle test
```

Run specific test class:
```bash
./gradle test --tests "UndefinedValueTest"
```

Run tests with verbose output:
```bash
./gradle test --info
```

### Test Reports

After running tests, reports are available at:
- HTML Report: `build/reports/tests/test/index.html`
- XML Report: `build/test-results/test/`

## Test Coverage

The test suite aims for:
- **Line Coverage**: 95%+ for all classes
- **Branch Coverage**: 90%+ for all conditional logic  
- **Method Coverage**: 100% for all public methods

## Test Categories

### Unit Tests
- Test individual methods in isolation
- Mock external dependencies where needed
- Verify correct behavior with various inputs
- Test edge cases and error conditions

### Integration Tests
- Test interaction between components
- Test with actual file system operations (using temp directories)
- Verify exception propagation and handling

## Dependencies

The test suite uses:
- **JUnit 5** (Jupiter) - Testing framework
- **AssertJ** - Fluent assertions
- **Mockito** - Mocking framework
- **Temporary directories** - For file operation tests

## Notes

- Tests are designed to be platform-independent where possible
- File operations use temporary directories to avoid side effects
- System command tests use simple, universally available commands
- Exception tests use concrete test implementations of abstract classes
