import { defineConfig } from "oxlint";

export default defineConfig({
  plugins: [
    "eslint",
    "typescript",
    "unicorn",
    "oxc",
    "import",
    "node",
    "vitest",
  ],
  jsPlugins: ["eslint-plugin-tsdoc"],
  categories: {
    correctness: "error",
    perf: "warn",
  },
  rules: {
    "import/no-cycle": "error",
    "no-array-constructor": "error",
    "no-empty": "error",
    "no-fallthrough": "error",
    "no-prototype-builtins": "error",
    "no-redeclare": "error",
    "no-regex-spaces": "error",
    "tsdoc/syntax": "warn",
    "typescript/ban-ts-comment": "error",
    "typescript/no-empty-object-type": "error",
    "typescript/no-explicit-any": "error",
    "typescript/no-namespace": "error",
    "typescript/no-require-imports": "error",
    "typescript/no-unnecessary-type-constraint": "error",
    "typescript/no-unsafe-function-type": "error",
  },
  options: {
    typeAware: true,
  },
});
