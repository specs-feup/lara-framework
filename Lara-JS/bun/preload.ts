import { bootstrap } from "./testBootstrap.js";
import path from "path";
import { fileURLToPath } from "url";

// Path to the DefaultWeaver JARs (relative to Lara-JS directory)
const laraJsDir = path.dirname(path.dirname(fileURLToPath(import.meta.url)));
const jarPath = path.join(
    laraJsDir,
    "../DefaultWeaver/build/install/DefaultWeaver"
);

const weaverConfig = {
    weaverName: "DefaultWeaver",
    weaverPrettyName: "Default Weaver",
    weaverFileName: "@specs-feup/lara/code/Weaver.js",
    jarPath: jarPath,
    javaWeaverQualifiedName:
        "org.lara.interpreter.weaver.defaultweaver.DefaultWeaver",
};

bootstrap(weaverConfig);
