# CRTP Implementation for Polymorphic ThisType

## Overview

This document describes the CRTP (Curiously Recurring Template Pattern) implementation for true polymorphic `ThisType` support in the WeaverGenerator.

## Problem Statement

Previously, when a join point hierarchy used `ThisType`, all methods returned the type of the class where the method is defined, not the actual runtime type. For example:

```java
// Previous behavior
class AFunction {
    public AFunction usesThis() { ... }  // Returns AFunction
}

class AMethod extends AFunction {
    // usesThis() still returns AFunction, not AMethod!
}
```

With CRTP, we now get:

```java
// Current CRTP behavior
class AFunction<Self extends AFunction<Self>> {
    public Self usesThis() { ... }  // Returns Self
}

class AMethod extends AFunction<AMethod> {
    // usesThis() now returns AMethod!
}
```

## CRTP Pattern Explanation

The CRTP pattern uses a recursive type parameter to achieve polymorphic "this" type behavior:

**ALL classes** get the CRTP type parameter for consistency and future extensibility:
- Declaration: `class ANode<Self extends ANode<Self>>`
- Declaration: `class ALoop<Self extends ALoop<Self>> extends AStmt<Self>`
- ThisType always resolves to: `Self`
- Example: `public Self selfTransform() { ... }`

This ensures:
1. Consistency across the entire hierarchy
2. Any class can be extended in the future without breaking changes
3. True polymorphic "this" type behavior everywhere

## Implementation Status: COMPLETE

### Key Components

1. **CrtpJavaClass** ([CrtpJavaClass.java](src/org/lara/interpreter/weaver/generator/generator/java/utils/CrtpJavaClass.java))
   - Extends `JavaClass` to support CRTP type parameters while keeping filenames clean
   - Overrides `generateCode()` to inject type parameters into class declaration
   - ALL classes get the CRTP type parameter `<Self extends ClassName<Self>>`
   - Supports controlling whether to add type arguments to superclass

2. **Generator modifications**:
   - [AbstractJoinPointClassGenerator.java](src/org/lara/interpreter/weaver/generator/generator/java/helpers/AbstractJoinPointClassGenerator.java) - Uses CrtpJavaClass for join point generation
   - [SuperAbstractJoinPointGenerator.java](src/org/lara/interpreter/weaver/generator/generator/java/helpers/SuperAbstractJoinPointGenerator.java) - Uses CrtpJavaClass for AJoinPoint generation
   - [UserAbstractJPClassGenerator.java](src/org/lara/interpreter/weaver/generator/generator/java/helpers/UserAbstractJPClassGenerator.java) - Uses CrtpJavaClass for user class generation

### Solution: CrtpJavaClass

The JavaClass library limitation was solved by creating `CrtpJavaClass`, a subclass of `JavaClass` that:

1. Keeps the class name clean for file naming (e.g., `ANode`)
2. Overrides `generateCode()` to add type parameters in the class declaration
3. Tracks whether this is a leaf or non-leaf class
4. Automatically adds the appropriate type arguments to the superclass

```java
// All classes get the CRTP type parameter:
CrtpJavaClass javaC = new CrtpJavaClass("ANode", package);
// Generates: public abstract class ANode<Self extends ANode<Self>> extends AParent<Self>

CrtpJavaClass javaC = new CrtpJavaClass("ALoop", package);
// Generates: public abstract class ALoop<Self extends ALoop<Self>> extends AStmt<Self>
```

## Generated Class Hierarchy

The CRTP pattern is applied consistently throughout the join point hierarchy:

```java
// Base class (extends non-generic JoinPoint)
public abstract class AJoinPoint<Self extends AJoinPoint<Self>> extends JoinPoint {
    public Self selfTransform(...) { ... }
}

// User-editable class (passes through Self)
public abstract class AThistypeWeaverJoinPoint<Self extends AThistypeWeaverJoinPoint<Self>> 
    extends AJoinPoint<Self> { ... }

// Non-leaf join point (extended by other join points)
public abstract class ANode<Self extends ANode<Self>> 
    extends AThistypeWeaverJoinPoint<Self> {
    public abstract Self getClone();
    public abstract List<Self> getChildren();
}

// Leaf join point (also has CRTP parameter for consistency and future extensibility)
public abstract class ALoop<Self extends ALoop<Self>> extends AStmt<Self> {
    public abstract Self getBody();  // Returns Self
}
```

## Leaf vs Non-Leaf Detection

The `LanguageSpecification.isSuper(JoinPointClass)` method is used to determine if a join point is a leaf or non-leaf:

- **Non-leaf**: If any other join point extends this one, `isSuper()` returns `true`
- **Leaf**: If no join point extends this one, `isSuper()` returns `false`

This is still computed for the purpose of adding `final` modifiers to methods in leaf classes,
but both leaf and non-leaf classes get the CRTP type parameter.

## Testing

The thistype test fixture at `test-resources/spec/valid/thistype/` provides join point definitions that use `ThisType`:

- `joinPointModel.xml` - Join point hierarchy with various extends relationships
- `artifacts.xml` - Attributes using `this` as return type
- `actionModel.xml` - Actions using `this` as return type and parameter type

Golden files at `test-resources/golden/thistype/` show the expected CRTP output format.

## Related Files

- [CrtpJavaClass.java](src/org/lara/interpreter/weaver/generator/generator/java/utils/CrtpJavaClass.java) - JavaClass extension for CRTP
- [AbstractJoinPointClassGenerator.java](src/org/lara/interpreter/weaver/generator/generator/java/helpers/AbstractJoinPointClassGenerator.java) - Per-join-point class generator
- [SuperAbstractJoinPointGenerator.java](src/org/lara/interpreter/weaver/generator/generator/java/helpers/SuperAbstractJoinPointGenerator.java) - Global AJoinPoint class generator
- [UserAbstractJPClassGenerator.java](src/org/lara/interpreter/weaver/generator/generator/java/helpers/UserAbstractJPClassGenerator.java) - User-editable class generator
- [ConvertUtils.java](src/org/lara/interpreter/weaver/generator/generator/java/utils/ConvertUtils.java) - Type conversion including ThisType handling
- [GeneratorUtils.java](src/org/lara/interpreter/weaver/generator/generator/java/utils/GeneratorUtils.java) - Utility methods for generation

## Known Issues

### DefaultWeaver Backward Compatibility

The DefaultWeaver has user-editable join point classes that were created before the CRTP implementation. When regenerating the abstract classes with CRTP support, these user classes fail to compile because they don't match the new generic signatures.

For example, `AWorkspace` now extends `ADefaultWeaverJoinPoint<Self>` but the old hand-written classes extend it without type parameters. This requires updating the user-editable classes to match the new CRTP pattern, which is a separate migration task.
