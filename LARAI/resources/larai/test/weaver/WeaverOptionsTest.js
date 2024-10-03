//import WeaverOptions from "@specs-feup/lara/api/weaver/WeaverOptions.js";
laraImport("weaver/WeaverOptions");

const data = WeaverOptions.getData();

console.log("Original verbose level: " + data.getVerboseLevel());
data.setVerboseLevel(3);
console.log("New verbose level: " + data.getVerboseLevel());

console.log("Original output folder: " + data.getOutputFolder());
data.setOutputFolder("subfolder");
console.log("New output folder: " + data.getOutputFolder());

console.log("Supported languages: " + WeaverOptions.getSupportedLanguages());
