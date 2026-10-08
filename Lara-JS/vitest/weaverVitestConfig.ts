import { fileURLToPath } from "node:url";
import { extname } from "node:path";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";
import { configDefaults, defineConfig } from "vitest/config";

const moduleExtension = extname(fileURLToPath(import.meta.url));

function getEnvironmentPath(url: URL): string {
  const path = fileURLToPath(url).replaceAll("\\", "/");

  // Vitest only treats environment names starting with "." or "/" as file
  // paths; a Windows drive path ("C:/...") parses as a URL scheme. The
  // leading "/" makes Vitest's pathe-based resolve recover the drive root.
  return process.platform === "win32" ? `/${path}` : path;
}

export function createWeaverVitestConfig(weaver: WeaverConfiguration) {
  return defineConfig({
    test: {
      exclude: [...configDefaults.exclude, "**/dist/**"],
      coverage: {
        include: ["**/*.{t,j}s"],
        provider: "v8",
        reporter: ["text", "lcov"],
      },
      environment: getEnvironmentPath(
        new URL(`./weaverEnvironment${moduleExtension}`, import.meta.url),
      ),
      environmentOptions: { weaver },
      experimental: {
        viteModuleRunner: false,
      },
      fileParallelism: false,
      globals: true,
      isolate: false,
      maxWorkers: 1,
      pool: "forks",
    },
  });
}
