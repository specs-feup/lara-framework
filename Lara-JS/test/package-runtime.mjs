import assert from "node:assert/strict";
import { existsSync } from "node:fs";
import { readdir } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import resolveWeaverScript from "../dist/code/resolveWeaverScript.js";
import { createWeaverVitestConfig as createSourceVitestConfig } from "../vitest/weaverVitestConfig.ts";
import { createWeaverVitestConfig as createEmittedVitestConfig } from "../dist/vitest/weaverVitestConfig.js";
import sourceRepositoryVitestConfig from "../vitest.config.ts";
import emittedRepositoryVitestConfig from "../dist/vitest.config.js";

const sourceLauncherUrl = new URL("../code/WeaverLauncher.ts", import.meta.url).href;
const emittedLauncherUrl = new URL("../dist/code/WeaverLauncher.js", import.meta.url).href;
const sourceWeaverPath = resolveWeaverScript(sourceLauncherUrl);
const emittedWeaverPath = resolveWeaverScript(emittedLauncherUrl);

assert.equal(
  sourceWeaverPath,
  fileURLToPath(new URL("../code/Weaver.ts", import.meta.url)),
  "source launcher keeps using the source TypeScript weaver",
);
assert.equal(
  emittedWeaverPath,
  fileURLToPath(new URL("../dist/code/Weaver.js", import.meta.url)),
  "emitted launcher uses the emitted JavaScript weaver",
);
assert.ok(existsSync(sourceWeaverPath));
assert.ok(existsSync(emittedWeaverPath));

const explicitWeaverPath = resolveWeaverScript(
  emittedLauncherUrl,
  "@specs-feup/lara/code/Weaver.ts",
);
assert.equal(
  explicitWeaverPath,
  fileURLToPath(import.meta.resolve("@specs-feup/lara/code/Weaver.ts")),
  "explicit .ts package specifier resolves through the emitted export",
);

const weaverConfiguration = {
  javaWeaverQualifiedName: "example.weaver.TestWeaver",
  jarPath: ".",
  weaverName: "TestWeaver",
  weaverPrettyName: "Test Weaver",
};

const sourceEnvironment = createSourceVitestConfig(weaverConfiguration).test.environment;
const emittedEnvironment = createEmittedVitestConfig(weaverConfiguration).test.environment;
const expectedEnvironmentPath = (url) => {
  const environmentPath = fileURLToPath(url).replaceAll("\\", "/");
  return process.platform === "win32" ? `/${environmentPath}` : environmentPath;
};

assert.equal(
  sourceEnvironment,
  expectedEnvironmentPath(new URL("../vitest/weaverEnvironment.ts", import.meta.url)),
);
assert.equal(
  emittedEnvironment,
  expectedEnvironmentPath(new URL("../dist/vitest/weaverEnvironment.js", import.meta.url)),
);
assert.ok(existsSync(fileURLToPath(new URL("../vitest/weaverEnvironment.ts", import.meta.url))));
assert.ok(
  existsSync(fileURLToPath(new URL("../dist/vitest/weaverEnvironment.js", import.meta.url))),
);
assert.equal(
  sourceRepositoryVitestConfig.test.environmentOptions.weaver.jarPath,
  emittedRepositoryVitestConfig.test.environmentOptions.weaver.jarPath,
);

async function findEmittedTestFiles(directory) {
  const entries = await readdir(directory, { withFileTypes: true });
  const nested = await Promise.all(
    entries.map(async (entry) => {
      const entryPath = path.join(directory, entry.name);
      if (entry.isDirectory()) return findEmittedTestFiles(entryPath);
      return /\.test\.(?:js|d\.ts)$/.test(entry.name) ? [entryPath] : [];
    }),
  );
  return nested.flat();
}

assert.deepEqual(
  await findEmittedTestFiles(fileURLToPath(new URL("../dist", import.meta.url))),
  [],
);

console.log("Lara package runtime paths passed");
