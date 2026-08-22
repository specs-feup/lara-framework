import path from "node:path";
import { fileURLToPath } from "node:url";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";
import type { WeaverEnvironmentOptions } from "./weaverEnvironment.ts";
import { defineConfig } from "vitest/config";

export interface WeaverVitestConfigOptions {
  javaOptionsEnvironmentVariable?: string;
}

export function createWeaverVitestConfig(
  weaver: WeaverConfiguration,
  options: WeaverVitestConfigOptions = {},
) {
  const environmentOptions: WeaverEnvironmentOptions = {
    javaOptionsEnvironmentVariable: options.javaOptionsEnvironmentVariable,
    weaver,
  };

  return defineConfig((userConfig) => {
    const root = userConfig.root ?? process.cwd();
    const environmentPath = path
      .relative(root, fileURLToPath(new URL("./weaverEnvironment.ts", import.meta.url)))
      .split(path.sep)
      .join("/");

    return {
      test: {
        coverage: {
          include: ["**/*[^.d].(t|j)s"],
          provider: "v8",
          reporter: ["text", "lcov"],
        },
        environment: `./${environmentPath}`,
        environmentOptions,
        experimental: {
          viteModuleRunner: false,
        },
        fileParallelism: false,
        globals: true,
        isolate: false,
        maxWorkers: 1,
        pool: "forks",
      },
    };
  });
}
