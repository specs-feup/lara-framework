# Stage 4 – Observations and Potential Issues

This file documents behaviors that looked surprising during Stage 4 (AST bridging) test implementation, with pointers to code locations and rationale.

## 1) ATreeNode.addChild chaining pitfall (surprising, but by design)
- Location: specs-java-libs/SpecsUtils/src/pt/up/fe/specs/util/treenode/ATreeNode.java
  - addChild(K child): around method returning `K` (the child) instead of the parent
- Why it’s surprising: Method returns the inserted child, not `this`. Chaining like `parent.addChild(c1).addChild(c2)` attaches `c2` as a child of `c1` (the returned node), not `parent`. This can easily create the wrong tree if the developer expects fluent builder semantics.
- How to reproduce:
  1. Create nodes `parent`, `c1`, `c2`.
  2. Call `parent.addChild(c1).addChild(c2);`
  3. `c2` becomes a child of `c1`, not `parent`.
- Suggestion: Consider JavaDoc emphasizing the return semantics; optionally consider a separate fluent API that returns the parent for chaining.

## 2) ATreeNode.getChildren exposes internal mutable list (documented FIXME)
- Location: specs-java-libs/SpecsUtils/src/pt/up/fe/specs/util/treenode/ATreeNode.java
  - getChildren(): returns the internal list; code comment says: "FIXME: Should be 'Collections.unmodifiableList(this.children);' but the current implementation breaks when you do."
- Why it’s risky: External callers can mutate the children list directly without adjusting parent links, potentially corrupting the tree invariants. It also makes defensive copying assumptions in callers invalid.
- How to reproduce: Retrieve list from `getChildren()` and call `add/remove` on it.
- Suggestion: Follow up on the FIXME if feasible, or document the mutability contract clearly in JavaDoc and utility docs.

## 3) WeaverEngine.getAstMethods() default returns DummyAstMethods
- Location: lara-framework/WeaverInterface/src/org/lara/interpreter/weaver/interf/WeaverEngine.java
  - getAstMethods(): returns `new DummyAstMethods(this)`
- Why it can surprise: Unless the concrete weaver overrides `getAstMethods()`, any AST access from JS/TS will throw `NotImplementedException` at runtime via `DummyAstMethods`. This is likely intentional but worth ensuring all production weavers override it.
- Suggestion: Keep as-is but ensure override in real weavers; possible log or assert in developer builds.

These are not breaking bugs for Stage 4 but are important edge cases and maintenance notes for future work.