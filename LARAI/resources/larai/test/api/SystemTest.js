import lara.System;

aspectdef SystemTest
	
	println("Logical cores working: " + (System.getNumLogicalCores() > 0));
	println("Testing System.nanos()");
	System.nanos();
	//var files = Io.getPaths("src/larai", "*.java");
	//println("Files: " + files.join());
end
