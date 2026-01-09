import { TestEnvironment } from "jest-environment-node";
import java from "java";

export default class LaraWeaverEnvironment extends TestEnvironment {
  constructor(config, context) {
    super(config, context);
    this.weaverConfig = config?.projectConfig?.testEnvironmentOptions?.weaverConfig;
  }

  weaverConfig;

  async setup() {
    await super.setup();
    this.global.__SHARED_MODULE__ = java;
    this.global.__LARA_IMPORT_FOR_SIDE_EFFECTS__ =
      this.weaverConfig?.importForSideEffects ?? [];
  }

  async teardown() {
    await super.teardown();
  }

  runScript(script) {
    return super.runScript(script);
  }
}
