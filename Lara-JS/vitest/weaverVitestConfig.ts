import { fileURLToPath } from "node:url";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";
import { defineConfig } from "vitest/config";

export function createWeaverVitestConfig(weaver: WeaverConfiguration) {
  return defineConfig({
    test: {
      coverage: {
        include: ["**/*[^.d].(t|j)s"],
        provider: "v8",
        reporter: ["text", "lcov"],
      },
      environment: fileURLToPath(new URL("./weaverEnvironment.ts", import.meta.url)),
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
