laraImport("lara.Strings");

console.log("Escaped HTML: " + Strings.escapeHtml("<h1>Hello</h1>"));

// Replacer
console.log("Replacer 1: " + Strings.replacer("WWRWW", "WRW", "W"));
console.log("Replacer 2: " + Strings.replacer("W R W", /\s/g, ""));
