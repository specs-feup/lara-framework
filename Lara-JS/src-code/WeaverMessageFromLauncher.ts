import { Arguments } from "yargs";
import WeaverConfiguration from "./WeaverConfiguration.js";

export default interface WeaverMessageFromLauncher {
  config: WeaverConfiguration;
  args: Arguments;
}
