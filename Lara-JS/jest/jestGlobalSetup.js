import { Weaver } from "../code/Weaver.js";

export default async function () {
  // TODO: Change this path to a neutral weaver
  await Weaver.setupJavaEnvironment("../../ClavaWeaver.jar");
}
