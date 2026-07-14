import { Weaver } from "@specs-feup/lara/code/Weaver.ts";
import type WeaverMessageFromLauncher from "@specs-feup/lara/code/WeaverMessageFromLauncher.ts";
import type WeaverConfiguration from "@specs-feup/lara/code/WeaverConfiguration.ts";
import java from "java";
import { afterAll, beforeAll } from "vitest";

export function setupWeaver(config: WeaverConfiguration): void {
  let started = false;

  beforeAll(async () => {
    const message: WeaverMessageFromLauncher = {
      args: { _: [], $0: "" },
      config,
    };

    await Weaver.setupWeaver(message.args, message.config);
    Weaver.start();
    started = true;

    for (const specifier of config.importForSideEffects ?? []) {
      await import(specifier);
    }
  });

  afterAll(() => {
    if (!started) {
      return;
    }

    Weaver.shutdown();
    java.stop();
  });
}
