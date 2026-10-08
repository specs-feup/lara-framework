import assert from "node:assert/strict";
import { spawnSync } from "node:child_process";
import { createRequire } from "node:module";
import { mkdir, mkdtemp, readFile, rm, symlink, writeFile } from "node:fs/promises";
import os from "node:os";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";

const packageDirectory = fileURLToPath(new URL("..", import.meta.url));
const temporaryDirectory = await mkdtemp(path.join(os.tmpdir(), "lara-packed-vitest-"));
const consumerModules = path.join(temporaryDirectory, "node_modules");
const installedPackage = path.join(consumerModules, "@specs-feup", "lara");
const consumerConfig = path.join(temporaryDirectory, "vitest.config.mjs");
const smokeTest = path.join(temporaryDirectory, "packed-config.test.mjs");

function run(command, args, options = {}) {
  const result = spawnSync(command, args, {
    encoding: "utf8",
    timeout: 60_000,
    ...options,
  });
  assert.equal(result.error, undefined, result.error?.message);
  assert.equal(result.status, 0, result.stderr || result.stdout);
  return result.stdout;
}

try {
  await mkdir(installedPackage, { recursive: true });
  const packOutput = run(
    "npm",
    ["pack", "--json", "--ignore-scripts", "--pack-destination", temporaryDirectory],
    { cwd: packageDirectory },
  );
  const [{ filename }] = JSON.parse(packOutput);
  const tarball = path.join(temporaryDirectory, filename);
  run("tar", ["-xzf", tarball, "--strip-components=1", "-C", installedPackage]);

  const packageJson = JSON.parse(
    await readFile(path.join(installedPackage, "package.json"), "utf8"),
  );
  const require = createRequire(import.meta.url);
  const vitestPackage = require.resolve("vitest/package.json");
  const workspaceNodeModules = path.dirname(path.dirname(vitestPackage));

  for (const dependencyName of [...Object.keys(packageJson.dependencies), "vitest"]) {
    const sourceDependency = path.join(workspaceNodeModules, dependencyName);
    await mkdir(path.dirname(path.join(consumerModules, dependencyName)), { recursive: true });
    await symlink(sourceDependency, path.join(consumerModules, dependencyName), "dir");
  }

  await writeFile(
    consumerConfig,
    `import assert from "node:assert/strict";
import { existsSync } from "node:fs";
import { createWeaverVitestConfig } from "@specs-feup/lara/vitest/weaverVitestConfig.ts";
import { fileURLToPath } from "node:url";

const config = createWeaverVitestConfig({
  javaWeaverQualifiedName: "example.weaver.TestWeaver",
  jarPath: ".",
  weaverName: "TestWeaver",
  weaverPrettyName: "Test Weaver",
});
const environmentPath = config.test.environment;
const emittedEnvironmentPath = fileURLToPath(
  import.meta.resolve("@specs-feup/lara/vitest/weaverEnvironment.js"),
);
const vitestEnvironmentPath = emittedEnvironmentPath.replaceAll("\\\\", "/");
assert.ok(environmentPath.endsWith("/dist/vitest/weaverEnvironment.js"));
assert.equal(environmentPath, process.platform === "win32" ? "/" + vitestEnvironmentPath : vitestEnvironmentPath);
assert.ok(existsSync(emittedEnvironmentPath), emittedEnvironmentPath);
config.test.environment = "node";
export default config;
`,
  );
  await writeFile(
    smokeTest,
    `import { expect, it } from "vitest";

it("boots with the packed Lara shared Vitest config", () => {
  expect(2 + 2).toBe(4);
});
`,
  );

  const environmentImport = spawnSync(
    process.execPath,
    [
      "--input-type=module",
      "-e",
      "await import(process.argv[1]); process.exit(0);",
      pathToFileURL(path.join(installedPackage, "dist/vitest/weaverEnvironment.js")).href,
    ],
    { cwd: temporaryDirectory, encoding: "utf8", timeout: 15_000 },
  );
  assert.equal(environmentImport.error, undefined, environmentImport.error?.message);
  assert.equal(environmentImport.status, 0, environmentImport.stderr || environmentImport.stdout);

  const vitestBin = path.join(path.dirname(vitestPackage), "vitest.mjs");
  const vitestRun = run(
    process.execPath,
    [vitestBin, "run", "--reporter=dot", "--config", consumerConfig],
    { cwd: temporaryDirectory },
  );
  assert.match(vitestRun, /1 passed/);
  console.log("Packed Lara Vitest config booted and ran one smoke test");
} finally {
  await rm(temporaryDirectory, { recursive: true, force: true });
}
