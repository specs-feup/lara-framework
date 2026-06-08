/**
 * Enumeration of AST traversal types.
 *
 * This is supposed to be an enum, but Node.js v25 does bot support TS' enums, only erasable-syntax.
 * Revert to an enum when Node.js supports it, or when we move to a different engine that supports it.
 * This and the "type" declaration below.
 */
export const TraversalType = {
  PREORDER: "preorder",
  POSTORDER: "postorder",
} as const;
export type TraversalType = typeof TraversalType[keyof typeof TraversalType];
