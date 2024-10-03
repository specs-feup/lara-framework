import lara.Io;

aspectdef IoTest

	var testFolder = Io.mkdir("__ioTest__");
	var testFile1 = Io.writeFile(Io.getPath(testFolder, "test1.txt"), "test1");
	var testFile2 = Io.writeFile(Io.getPath(testFolder, "test2.txt"), "test2");
	var testFile3 = Io.writeFile(Io.getPath(testFolder, "test3.doc"), "test3");	

	var files = Io.getPaths(testFolder, "*.txt");
	println("Files: " + files.map(getName).sort().join());
	
	var filesNoArg = Io.getPaths(testFolder);
	println("Files no arg: " + filesNoArg.map(getName).sort().join());
	
	Io.deleteFolder(testFolder);
	
	
	// Path separator
	var pathSeparator = Io.getPathSeparator();
	println("Path separator: " + (pathSeparator === ":" || pathSeparator === ";"));
	
	// Name separator
	var separator = Io.getSeparator();
	println("Name separator: " + (separator === "\\" || separator === "/"));
	
end

function getName(file) {
	return file.getName();
}