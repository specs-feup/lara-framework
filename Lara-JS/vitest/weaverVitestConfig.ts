import { fileURLToPath } from "node:url";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";
import { defineConfig } from "vitest/config";

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
      coverage: {
        include: ["**/*.{t,j}s"],
        provider: "v8",
        reporter: ["text", "lcov"],
      },
      environment: getEnvironmentPath(
        new URL("./weaverEnvironment.ts", import.meta.url),
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
