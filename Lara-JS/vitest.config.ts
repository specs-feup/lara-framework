import path from "node:path";
import { fileURLToPath } from "node:url";
import { createWeaverVitestConfig } from "./vitest/weaverVitestConfig.ts";

const moduleDirectory = path.dirname(fileURLToPath(import.meta.url));
const packageDirectory =
  path.basename(moduleDirectory) === "dist" ? path.dirname(moduleDirectory) : moduleDirectory;

export default createWeaverVitestConfig({
  jarPath: path.join(path.dirname(packageDirectory), "DefaultWeaver/build/install/DefaultWeaver"),
  javaWeaverQualifiedName: "org.lara.interpreter.weaver.defaultweaver.DWWeaver",
  weaverFileName: "@specs-feup/lara/code/Weaver.ts",
  weaverName: "DefaultWeaver",
  weaverPrettyName: "Default Weaver",
});
