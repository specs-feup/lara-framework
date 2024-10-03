import lara.Strings;

aspectdef StringsTest
	
	println("Escaped HTML: " + Strings.escapeHtml("<h1>Hello</h1>"));
	//var files = Io.getPaths("src/larai", "*.java");
	//println("Files: " + files.join());
	
	// Replacer
	println("Replacer 1: " + Strings.replacer("WWRWW", "WRW", "W"));
	println("Replacer 2: " + Strings.replacer("W R W", /\s/g, ""));
end
