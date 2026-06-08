import type { Config } from "@jest/types";
import { Weaver } from "@specs-feup/lara/code/Weaver.ts";
import type WeaverMessageFromLauncher from "@specs-feup/lara/code/WeaverMessageFromLauncher.ts";
import type WeaverConfiguration from "../code/WeaverConfiguration.ts";

async function oneTimeSetup(
  globalConfig: Config.GlobalConfig,
  projectConfig: Config.ProjectConfig,
) {
  const weaverMessageFromLauncher: WeaverMessageFromLauncher = {
    args: {
      _: [],
      $0: "",
    },
    config: projectConfig.testEnvironmentOptions
      .weaverConfig as WeaverConfiguration,
  };

  await Weaver.setupWeaver(
    weaverMessageFromLauncher.args,
    weaverMessageFromLauncher.config,
  );
}

let setupDone = false;

export default async function (
  globalConfig: Config.GlobalConfig,
  projectConfig: Config.ProjectConfig,
) {
  if (!setupDone) {
    await oneTimeSetup(globalConfig, projectConfig);
    setupDone = true;
  }
  Weaver.start();
}
