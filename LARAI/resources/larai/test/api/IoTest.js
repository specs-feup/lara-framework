laraImport("lara.Io");

function getName(file) {
    return file.getName();
}

const testFolder = Io.mkdir("__ioTest__");
const testFile1 = Io.writeFile(Io.getPath(testFolder, "test1.txt"), "test1");
const testFile2 = Io.writeFile(Io.getPath(testFolder, "test2.txt"), "test2");
const testFile3 = Io.writeFile(Io.getPath(testFolder, "test3.doc"), "test3");	

const files = Io.getPaths(testFolder, "*.txt");
console.log("Files: " + files.map(getName).sort().join());

const filesNoArg = Io.getPaths(testFolder);
console.log("Files no arg: " + filesNoArg.map(getName).sort().join());

Io.deleteFolder(testFolder);


// Path separator
const pathSeparator = Io.getPathSeparator();
console.log("Path separator: " + (pathSeparator === ":" || pathSeparator === ";"));

// Name separator
const separator = Io.getSeparator();
console.log("Name separator: " + (separator === "\\" || separator === "/"));
