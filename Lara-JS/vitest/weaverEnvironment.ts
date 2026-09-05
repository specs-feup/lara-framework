import java from "java";
import type { Environment } from "vitest/environments";
import { Weaver } from "../code/Weaver.ts";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";

export interface WeaverEnvironmentOptions extends Record<string, unknown> {
  weaver: WeaverConfiguration;
}

const exitProcess = process.exit.bind(process);

const environment: Environment = {
  name: "weaver",
  viteEnvironment: "ssr",

  async setup(global, rawOptions) {
    const options = rawOptions as unknown as WeaverEnvironmentOptions;
    let weaverStarted = false;

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

    return {
      teardown() {},
    };
  },
};

export default environment;
