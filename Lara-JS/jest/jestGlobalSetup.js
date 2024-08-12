import { Weaver } from "lara-js/code/Weaver.js";

async function oneTimeSetup(globalConfig, projectConfig) {
  const weaverMessageFromLauncher = {
    args: {
      _: [],
      $0: "",
    },
    config: projectConfig.testEnvironmentOptions.weaverConfig,
  };

  await Weaver.setupWeaver(
    weaverMessageFromLauncher.args,
    weaverMessageFromLauncher.config
  );
}

let setupDone = false;

export default async function (globalConfig, projectConfig) {
  if (!setupDone) {
    await oneTimeSetup(globalConfig, projectConfig);
    setupDone = true;
  }
  Weaver.start();
}
