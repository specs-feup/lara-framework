import { type Arguments } from "yargs";
import type WeaverConfiguration from "./WeaverConfiguration.ts";

export default interface WeaverMessageFromLauncher {
  config: WeaverConfiguration;
  args: Arguments;
}
