import { Console } from "node:console";
import java from "java";
import type { Environment } from "vitest/environments";
import { Weaver } from "../code/Weaver.ts";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";

export interface WeaverEnvironmentOptions extends Record<string, unknown> {
  javaOptionsEnvironmentVariable?: string;
  weaver: WeaverConfiguration;
}

const exitProcess = process.exit.bind(process);

const environment: Environment = {
  name: "weaver",
  viteEnvironment: "ssr",

  async setup(global, rawOptions) {
    const options = rawOptions as unknown as WeaverEnvironmentOptions;
    let weaverStarted = false;

    applyJavaOptions(options.javaOptionsEnvironmentVariable);

    // Vitest sends all results (including worker coverage) before it terminates
    // the fork. Stopping node-java any earlier also closes Vitest's IPC handle.
    process.once("SIGTERM", () => {
      let exitCode = 0;

      try {
        if (weaverStarted) {
          Weaver.shutdown();
        }
        java.stop();
      } catch (error) {
        console.error("Failed to stop the Weaver Java runtime cleanly.", error);
        exitCode = 1;
      } finally {
        exitProcess(exitCode);
      }
    });

    const message = {
      args: { _: [], $0: "" },
      config: options.weaver,
    };

    await Weaver.setupWeaver(message.args, message.config);
    Weaver.start();
    weaverStarted = true;

    global.console.Console = Console;

    return {
      teardown() {
        delete global.console.Console;
      },
    };
  },
};

function applyJavaOptions(environmentVariable: string | undefined): void {
  if (environmentVariable === undefined) {
    return;
  }

  const rawJavaOptions = process.env[environmentVariable];
  if (rawJavaOptions === undefined || rawJavaOptions.trim() === "") {
    return;
  }

  for (const javaOption of parseJavaOptions(rawJavaOptions)) {
    if (!java.options.includes(javaOption)) {
      java.options.push(javaOption);
    }
  }
}

function parseJavaOptions(rawJavaOptions: string): string[] {
  try {
    const parsed: unknown = JSON.parse(rawJavaOptions);

    if (
      Array.isArray(parsed) &&
      parsed.every((javaOption) => typeof javaOption === "string")
    ) {
      return parsed;
    }
  } catch {
    // Fall back to whitespace splitting for ad-hoc local use.
  }

  return rawJavaOptions.split(/\s+/).filter((javaOption) => javaOption !== "");
}

export default environment;
