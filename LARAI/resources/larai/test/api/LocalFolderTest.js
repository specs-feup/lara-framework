import lara.util.LocalFolder;

aspectdef LocalFolderTest
	
	var localFolder = new LocalFolder("./");

	println("FileList:" + localFolder.getFileList());
	println("FileList ANT:" + localFolder.getFileList("ant"));
	
	//println("Escaped HTML: " + Strings.escapeHtml("<h1>Hello</h1>"));
	//var files = Io.getPaths("src/larai", "*.java");
	//println("Files: " + files.join());
end
