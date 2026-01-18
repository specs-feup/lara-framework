/* global globalThis */

// Take a look at https://github.com/specs-feup/lara-framework/issues/93 for context.

// This file is executed after the test environment is set up, so we can access
// global variables set in the test environment.

const importForSideEffects = globalThis.__LARA_IMPORT_FOR_SIDE_EFFECTS__ ?? [];

for (const specifier of importForSideEffects) {
  await import(specifier);
}
