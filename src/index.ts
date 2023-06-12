#!/usr/bin/env node
import WeaverLauncher from "./WeaverLauncher.js";

const moduleName = "clava";

const weaver = new WeaverLauncher(moduleName);

await weaver.execute();
