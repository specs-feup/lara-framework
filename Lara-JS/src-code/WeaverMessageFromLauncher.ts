import { type Arguments } from "yargs";
import type WeaverConfiguration from "./WeaverConfiguration.js";

export default interface WeaverMessageFromLauncher {
  config: WeaverConfiguration;
  args: Arguments;
}
