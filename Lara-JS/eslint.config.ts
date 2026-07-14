import js from "@eslint/js";
import eslintConfigPrettier from "eslint-config-prettier";
import tsdoc from "eslint-plugin-tsdoc";
import { defineConfig } from "eslint/config";
import typescriptEslint from "typescript-eslint";
import vitest from "@vitest/eslint-plugin";

export function createEslintConfig(tsconfigRootDir: string) {
  return defineConfig([
    js.configs.recommended,
    eslintConfigPrettier,
    ...typescriptEslint.configs.recommended,
    {
      plugins: {
        "@typescript-eslint": typescriptEslint.plugin,
        tsdoc,
      },

      languageOptions: {
        parser: typescriptEslint.parser,
        ecmaVersion: 5,
        sourceType: "script",

        parserOptions: {
          project: ["./tsconfig.json", "./tsconfig.*.json"],
          tsconfigRootDir,
        },
      },

      rules: {
        "tsdoc/syntax": "warn",
      },
    },
    {
      ...vitest.configs.recommended,
      files: ["**/*.spec.ts", "**/*.test.ts"],

      plugins: {
        vitest,
      },

      languageOptions: {
        globals: {
          ...vitest.environments.env.globals,
        },
      },
    },
  ]);
}

export default createEslintConfig(import.meta.dirname);
