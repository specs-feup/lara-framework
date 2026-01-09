import WeaverOptions from "@specs-feup/lara/api/weaver/WeaverOptions.js";

const data = WeaverOptions.getData();

console.log("Original output folder: " + data.getOutputFolder());
data.setOutputFolder("subfolder");
console.log("New output folder: " + data.getOutputFolder());

console.log("Supported languages: " + WeaverOptions.getSupportedLanguages());
