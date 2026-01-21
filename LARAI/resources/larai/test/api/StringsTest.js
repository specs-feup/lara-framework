import Strings from "@specs-feup/lara/api/lara/Strings.js";

// Replacer
console.log("Replacer 1: " + Strings.replacer("WWRWW", "WRW", "W"));
console.log("Replacer 2: " + Strings.replacer("W R W", /\s/g, ""));
