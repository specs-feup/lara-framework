import { defineConfig } from "jest";
import path from "path";
import { fileURLToPath } from "url";

export const weaverConfig = {
  weaverName: "DefaultWeaver",
  weaverPrettyName: "Default Weaver",
  weaverFileName: "@specs-feup/lara/code/Weaver.ts",
  jarPath: path.join(
    path.dirname(path.dirname(path.dirname(fileURLToPath(import.meta.url)))),
    "./DefaultWeaver/build/install/DefaultWeaver",
  ),
  javaWeaverQualifiedName: "org.lara.interpreter.weaver.defaultweaver.DWWeaver",
};

export default defineConfig({
  preset: "ts-jest/presets/default-esm",
  testEnvironment: "@specs-feup/lara/jest/jestEnvironment.ts",
  globalSetup: "@specs-feup/lara/jest/jestGlobalSetup.ts",
  globalTeardown: "@specs-feup/lara/jest/jestGlobalTeardown.ts",
  setupFiles: ["@specs-feup/lara/jest/setupFiles/sharedJavaModule.ts"],
  setupFilesAfterEnv: ["@specs-feup/lara/jest/setupFiles/importSideEffects.ts"],
  moduleNameMapper: {
    "(.+)\\.js": "$1",
  },
  testEnvironmentOptions: {
    weaverConfig,
  },
});
