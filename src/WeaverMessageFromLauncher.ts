import { Arguments } from "yargs";
import WeaverConfiguration from "./WeaverConfiguration";

export default interface WeaverMessageFromLauncher {
  config: WeaverConfiguration;
  args: Arguments;
}
