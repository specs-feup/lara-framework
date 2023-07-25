import lara._JavaTypes;

/**
 * Utility methods related with input/output operations on files.
 * @class
 */
var Io = {};

/**
 * @return {boolean} true if the given object is a Java file, false otherwise.
 */
Io.isJavaFile = function(object) {
	var fileClass = "java.io.File";
	return isJavaClass(object, fileClass);
}

/**
 * @param {String|J#java.io.File} path
 */
Io._newFile = function(path) {
	
	//var fileClass = "java.io.File";
	
	// If path is a file, convert to String first
	//if(File.class.isInstance(path)) {
	//if(isJavaClass(path, fileClass)) {	
	if(Io.isJavaFile(path)) {	
		path = path.toString();
	}
	
	//var File = Java.type(fileClass);
	var File = _JavaTypes.getJavaFile();
	return new File(path.toString());
}

/**
 * @param {String|J#java.io.File} base
 * @param {String|J#java.io.File} path 
 *
 */
Io._newFileWithBase = function(base, path) {
	//var fileClass = "java.io.File";
	
	
	// If path is a file, convert to String first
	//if(File.class.isInstance(path)) {
	//if(isJavaClass(path, fileClass)) {	
	if(Io.isJavaFile(path)) {	
		path = path.toString();
	}

	// If base is not a file, convert to File first
	//if(!File.class.isInstance(base)) {
	if(!Io.isJavaFile(base)) {	
		base = Io._newFile(base);
	}
	
	//var File = Java.type(fileClass);
	var File = _JavaTypes.getJavaFile();
	return new File(base, path);
}

/**
 * Creates a folder.
 */
Io.mkdir = function(fileOrBaseFolder, optionalFile) {
	return SpecsIo.mkdir(Io.getPath(fileOrBaseFolder, optionalFile));
}

/**
 * Global version of the function.
 */
function mkdir(fileOrBaseFolder, optionalFile) {
	return Io.mkdir(fileOrBaseFolder, optionalFile);
	//return SpecsIo.mkdir(path);
}

/**
 * Creates a temporary folder. If no name is given, generates a random one
 */
 /*
Io.mkTempDir = function(foldername) {
	if(foldername === undefined) {
		foldername
	}
	return SpecsIo.mkdir(Io.getPath(fileOrBaseFolder, optionalFile));
}
*/

/**
 * If folderName is undefined, returns the OS temporary folder.
 * If defined, creates a new folder inside the system's temporary folder and returns it.
 *
 */
Io.getTempFolder = function(folderName) {
	
	if(folderName === undefined) {
		return SpecsIo.getTempFolder();
	}
	
	return SpecsIo.getTempFolder(folderName);
}

/**
 * Creates a randomly named folder in the OS temporary folder that is deleted when the virtual machine exits.
 *
 * @return {J#java.io.File} 
 */
Io.newRandomFolder = function() {
	return SpecsIo.newRandomFolder();
}



Io.getPath = function(fileOrBaseFolder, optionalFile) {
	if(optionalFile === undefined) {
		return Io._newFile(fileOrBaseFolder); 		
	}
	
	// Test if optionalFile is absolute
	var optionalFilePath = Io._newFile(optionalFile);
	if(optionalFilePath.isAbsolute()) {
		return optionalFilePath;
	}
	
	return Io._newFileWithBase(fileOrBaseFolder, optionalFile);
}	

/**
 * @returns {String} the absolute path of the given file 
 */
Io.getAbsolutePath = function(fileOrBaseFolder, optionalFile) {
	return SpecsIo.normalizePath(SpecsIo.getCanonicalPath(Io.getPath(fileOrBaseFolder, optionalFile)));
}

/**
 * Gets the paths (files and folders) in the given folder, corresponding to the given base folder and argument patterns.
 * 
 * @param {String|J#java.io.File} baseFolder
 * @param {String...} patterns
 */
Io.getPaths = function(baseFolder) {

	checkDefined(baseFolder, "baseFolder", "Io.getPaths");
	
	var baseFolderFile = Io.getPath(baseFolder);
	
	// For each argument after the baseFolder, treat it as a different file/glob
	var files = [];
	


	// If empty, add all files
	var argsArray = arrayFromArgs(arguments, 1);	
	if(argsArray.length === 0) {
		argsArray.push("*");
	}
	
	//for(argument of arguments) {
	for(argument of argsArray) {
		//var foundFiles = SpecsIo.getFilesWithPattern(baseFolderFile, argument);
		var foundFiles = SpecsIo.getPathsWithPattern(baseFolderFile, argument, false, "FILES_AND_FOLDERS");
		
		for(file of foundFiles) {
			files.push(file);
		}
	}
	
	return files;
}


/**
 * Gets the folders in the given folder, corresponding to the given base folder and argument patterns.
 * 
 * @param {String|J#java.io.File} baseFolder
 * @param {String...} patterns
 */
Io.getFolders = function(baseFolder) {

	var paths = Io.getPaths(baseFolder, arrayFromArgs(arguments, 1));

	var folders = [];
	
	for(var path of paths) {
		if(Io.isFolder(path)) {
			folders.push(path);
		}
	}
	
	return folders;
}


/**
 * The files inside the given folder that respects the given pattern.
 *
 * @param {string|File} baseFolder
 * @param {string|Object[]} pattern
 * @param {boolean} isRecursive
 */
Io.getFiles = function(baseFolder, pattern, isRecursive) {
	// Initialize inputs
	if(baseFolder === undefined) {
		baseFolder = "./";
	}

	if(pattern === undefined) {
		pattern = "*";
	}
	
	if(isRecursive === undefined) {
		isRecursive = false;
	}

	// If pattern is an array, call function recursively
	if(isArray(pattern)) {
		var files = [];

		for(var singlePattern of pattern) {
			var newFiles = Io.getFiles(baseFolder, singlePattern, isRecursive);
			for(var newFile of newFiles) {
				files.push(newFile);
			}
		}
		
		return files;
	}


	var list = SpecsIo.getPathsWithPattern(Io.getPath(baseFolder), pattern.toString(), isRecursive, "FILES");
	
	
	var files = [];
	for(var file of list) {
		files.push(file);
	}
	
	return files;
}

/*
Io.getFilesRecursive = function(baseFolder) {
	var files = [];

	// Get args array
	var argsArray = arrayFromArgs(arguments);

	
	// Get paths of current folder
	//println("Get files recursive arguments");
	//printObject(argsArray);
	var paths = Io.getPaths(argsArray);
	
	for(var path of paths) {
		// If folder, do not add to files, but call .getFiles recursively
		if(Io.isFolder(path)) {
			argsArray[0] = path;
			var pathFiles = Io.getFilesRecursive(argsArray);
			files = files.concat(pathFiles);
			continue;
		}
	
		if(Io.isFile(path)) {
			// Add file to list
			files.push(path);
			continue;
		}
		
		println("Io.getFilesRecursive: could not process path '"+path+"', is neither a file or a folder");
	}
	
	return files;
}
*/
/**
 * Returns a List with a string for each line of the given file
 */
Io.readLines = function(fileOrBaseFolder, optionalFile) {
	return LaraIo.readLines(Io.getPath(fileOrBaseFolder, optionalFile));	
}


/**
 * Global version of the function.
 */
function readLines(fileOrBaseFolder, optionalFile) {
	return Io.readLines(fileOrBaseFolder, optionalFile);
}



/**
 * Deletes the given file.
 */
Io.deleteFile = function(fileOrBaseFolder, optionalFile) {
	var file = Io.getPath(fileOrBaseFolder, optionalFile);
	if(!Io.isFile(file)) {
		// Skipping file
		return;
	}

	return LaraIo.deleteFile(file);
}

/**
 * Global version of the function.
 */
function deleteFile(fileOrBaseFolder, optionalFile) {
	return Io.deleteFile(fileOrBaseFolder, optionalFile);
}



/**
 * Each argument is a file that will be deleted.
 */
Io.deleteFiles = function() {
	for(argument of arguments) {
		deleteFile(argument);
	}
}

/**
 * Deletes a folder and its contents.
 *
 * @return true if both the contents and the folder could be deleted
 */
Io.deleteFolder = function(folderPath) {
	var folder = Io.getPath(folderPath);
	return SpecsIo.deleteFolder(folder);
}

/**
 * Deletes the contents of a folder.
 */
Io.deleteFolderContents = function(folderPath) {
	var folder = Io.getPath(folderPath);
	return SpecsIo.deleteFolderContents(folder);
}

/**
 * @return true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
 */
Io.isFile = function(path) {
	if(typeof path === "string") {
		path = Io.getPath(path);
	}
	
	return path.isFile();
}

/**
 * @return true if and only if the file denoted by this abstract pathname exists and is a folder; false otherwise
 */
Io.isFolder = function(path) {
	if(typeof path === "string") {
		path = Io.getPath(path);
	}
	
	return path.isDirectory();
}

Io.readJson = function(path) {
	return fileToJSON(path);
}

Io.writeJson = function(path, object) {
	JSONtoFile(path, object);
}

Io.copyFile = function(filepath, destination) {
	checkDefined(filepath, "filepath", "Io.copyFile");
	checkTrue(Io.isFile(filepath), "Io.copyFile: given filepath '"+filepath+"' is not a file");
	
	return SpecsIo.copy(Io.getPath(filepath), Io.getPath(destination));
}

Io.copyFolder = function(filepath, destination, verbose) {
	checkDefined(filepath, "filepath", "Io.copyFolder");
	checkDefined(destination, "destination", "Io.copyFolder");
	
	if(verbose === undefined) {
		verbose = false;
	}
	
	return SpecsIo.copyFolder(Io.getPath(filepath), Io.getPath(destination), verbose);
}

/**
 * Returns the given path, without extension.
 *
 * @param {string|#java.io.File} path 
 */
Io.removeExtension = function(path) {
	return SpecsIo.removeExtension(path);
}

/**
 * Returns the extension of the given path.
 *
 * @param {string|#java.io.File} path 
 */
Io.getExtension = function(path) {
	return SpecsIo.getExtension(path);
}

/**
 * @param {string} path The path of the file to write.
 * @param {string} content The contents to write.
 *
 * @return {J#java.io.File} the file to where the contents where written.
 */
Io.writeFile = function(path, content) {
	var file = Io._newFile(path);
	SpecsIo.write(file,content);
	return file;
}

/**
 * @param {string} path The path of the file to read.
 *
 * @return {string} the contents of the file.
 */
Io.readFile = function(path) {
	var file = Io._newFile(path);
	var content = SpecsIo.read(file);
	return content;
}

Io.appendFile = function(path, content) {
	var file = Io._newFile(path);
	SpecsIo.append(file,content);
}

/**
 * Returns the path of 'targetFile', relative to 'baseFile'. 
 * 
 * If the file does not share a common ancestor with baseFile, returns undefined.
 */
Io.getRelativePath = function(targetFile, baseFile) {
	var relativePath = SpecsIo.getRelativePath(Io.getPath(targetFile), Io.getPath(baseFile));
	
	if(Io.getPath(relativePath).isAbsolute()) {
		return undefined;
	}
	
	return relativePath;
}

/**
 * 	The system-dependent path-separator (e.g., : or ;).
 */
Io.getPathSeparator = function() {
	return _JavaTypes.getJavaFile().pathSeparator;
}

/**
 * 	The system-dependent name-separator (e.g., / or \).
 */
Io.getSeparator = function() {
	return _JavaTypes.getJavaFile().separator;
}

Io.md5 = function(fileOrBaseFolder, optionalFile) {
	return SpecsIo.getMd5(Io.getPath(fileOrBaseFolder, optionalFile));
}

Io.getWorkingFolder = function() {
	return SpecsIo.getWorkingDir();
}

/**
 * If value is a string that ends in .json, assume it is a file to a json object and parses it. 
 * If it is a string but does not end in json, assume it is a stringified object.
 * Otherwise, returns the object as it is.
 */ 
Io.jsonObject = function(value) {
	if(isString(value)) {
		if(value.endsWith(".json")) {
			return Io.readJson(Io.getPath(value));
		} else {
			return JSON.parse(value);
		}	
	}
		
	return value;
}

