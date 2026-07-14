import path from "node:path";
import { fileURLToPath } from "node:url";
import { createWeaverVitestConfig } from "./vitest/weaverVitestConfig.ts";

export default createWeaverVitestConfig({
  jarPath: path.join(
    path.dirname(path.dirname(fileURLToPath(import.meta.url))),
    "./DefaultWeaver/build/install/DefaultWeaver",
  ),
  javaWeaverQualifiedName:
    "org.lara.interpreter.weaver.defaultweaver.DWWeaver",
  weaverFileName: "@specs-feup/lara/code/Weaver.ts",
  weaverName: "DefaultWeaver",
  weaverPrettyName: "Default Weaver",
});
