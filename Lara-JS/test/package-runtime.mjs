import assert from "node:assert/strict";
import { existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import resolveWeaverScript from "../dist/code/resolveWeaverScript.js";

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

console.log("Lara package runtime paths passed");
