import path from "path";
import { fileURLToPath } from "url";

export const weaverConfig = {
  weaverName: "DefaultWeaver",
  weaverPrettyName: "Default Weaver",
  weaverFileName: "@specs-feup/lara/code/Weaver.js",
  jarPath: path.join(
    path.dirname(path.dirname(path.dirname(fileURLToPath(import.meta.url)))),
    "./DefaultWeaver/build/install/DefaultWeaver"
  ),
  javaWeaverQualifiedName: "org.lara.interpreter.weaver.defaultweaver.DefaultWeaver",
};


const config = {
  preset: "ts-jest/presets/default-esm",
  testEnvironment: "@specs-feup/lara/jest/jestEnvironment.js",
  globalSetup: "@specs-feup/lara/jest/jestGlobalSetup.js",
  globalTeardown: "@specs-feup/lara/jest/jestGlobalTeardown.js",
  setupFiles: ["@specs-feup/lara/jest/setupFiles/sharedJavaModule.js"],
  setupFilesAfterEnv: ["@specs-feup/lara/jest/setupFiles/importSideEffects.js"],
  moduleNameMapper: {
    "@specs-feup/lara/api/(.+).js": "@specs-feup/lara/src-api/$1",
    "(.+)\\.js": "$1",
  },
  testEnvironmentOptions: {
    weaverConfig,
  },
};

export default config;
