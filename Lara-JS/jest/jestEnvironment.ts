import type {
  EnvironmentContext,
  JestEnvironmentConfig,
} from "@jest/environment";
import type WeaverConfiguration from "@specs-feup/lara/code/WeaverConfiguration.ts";
import java from "java";
import { TestEnvironment } from "jest-environment-node";

export default class LaraWeaverEnvironment extends TestEnvironment {
  private weaverConfig: WeaverConfiguration;

  constructor(config: JestEnvironmentConfig, context: EnvironmentContext) {
    super(config, context);
    this.weaverConfig = config.projectConfig.testEnvironmentOptions
      .weaverConfig as WeaverConfiguration;
  }

  async setup() {
    await super.setup();
    this.global.__SHARED_MODULE__ = java;
    this.global.__LARA_IMPORT_FOR_SIDE_EFFECTS__ =
      this.weaverConfig?.importForSideEffects ?? [];
  }

  async teardown() {
    await super.teardown();
  }
}
