import JavaTypes from "./util/JavaTypes.js";
import{
	checkDefined,
	arrayFromArgs, 
	isArray,
	isString,
	isJavaClass,
	checkTrue
} from "./core/LaraCore.js"

import{
	JSONtoFile,
	fileToJSON,
	println
} from "../core/output.js"




/**
 * Utility methods related with input/output operations on files.
 * @class
 */
export default class Io {

	/**
 	* @return {boolean} true if the given object is a Java file, false otherwise.
 	*/
	static isJavaFile(object: any) {
		const fileClass = "java.io.File";
		return isJavaClass(object, fileClass);
	}

	/**
	 * @param {String|J#java.io.File} path
	 */
	static _newFile(path: any) {
	
		if(Io.isJavaFile(path)) {	
			path = path.toString();
		}
	
		const file: any = JavaTypes.JavaFile;
		return new file(path.toString());
	}

	/**
	 * @param {String|J#java.io.File} base
	 * @param {String|J#java.io.File} path 
	 *
	 */
	static _newFileWithBase(base: any, path: string | File) {

		// If path is a file, convert to String first
		if(Io.isJavaFile(path)) {	
			path = path.toString();
		}

		// If base is not a file, convert to File first
		if(!Io.isJavaFile(base)) {	
			base = Io._newFile(base);
		}
	
		const File = JavaTypes.JavaFile;
		return new File(base, path);
	}

	/**
 	* Creates a folder.
 	*/
	static mkdir (fileOrBaseFolder: any, optionalFile?: File | undefined) {
		return JavaTypes.SpecsIo.mkdir(Io.getPath(fileOrBaseFolder, optionalFile));
	}

	/**
 	* Global version of the function.
 	*/
	static fmkdir(fileOrBaseFolder: any, optionalFile?: File | undefined) {
		return Io.mkdir(fileOrBaseFolder, optionalFile);
	}

	/**
	 * Creates a temporary folder. If no name is given, generates a random one
	 */
	/*
	Io.mkTempDir = function(foldername) {
		if(foldername === undefined) {
			foldername
		}
		return JavaTypes.SpecsIo.mkdir(Io.getPath(fileOrBaseFolder, optionalFile));
	}
	*/

	/**
 	* If folderName is undefined, returns the OS temporary folder.
 	* If defined, creates a new folder inside the system's temporary folder and returns it.
 	*
 	*/
	static getTempFolder(folderName: string|undefined) {
		if(folderName === undefined) {
			return JavaTypes.SpecsIo.getTempFolder();
		}
		return JavaTypes.SpecsIo.getTempFolder(folderName);
	}

	/**
	 * Creates a randomly named folder in the OS temporary folder that is deleted when the virtual machine exits.
	 *
	 * @return {J#java.io.File} 
	 */
	static newRandomFolder() {
		return JavaTypes.SpecsIo.newRandomFolder();
	}



	static getPath (fileOrBaseFolder: any, optionalFile?: File | undefined) {
		
		if(optionalFile === undefined) {
			return Io._newFile(fileOrBaseFolder); 		
		}
	
		// Test if optionalFile is absolute
		const optionalFilePath = Io._newFile(optionalFile);
		if(optionalFilePath.isAbsolute()) {
			return optionalFilePath;
		}
	
		return Io._newFileWithBase(fileOrBaseFolder, optionalFile);
	}	

	/**
 	* @returns {String} the absolute path of the given file 
 	*/
	static getAbsolutePath(fileOrBaseFolder: any, optionalFile?: File | undefined) {
		return JavaTypes.SpecsIo.normalizePath(JavaTypes.SpecsIo.getCanonicalPath(Io.getPath(fileOrBaseFolder, optionalFile)));
	}

	/**
	 * Gets the paths (files and folders) in the given folder, corresponding to the given base folder and argument patterns.
	 * 
	 * @param {String|J#java.io.File} baseFolder
	 * @param {String...} patterns
	 */
	static getPaths(baseFolder: any, ...args: String[]) {
		checkDefined(baseFolder, "baseFolder", "Io.getPaths");
	
		const baseFolderFile = Io.getPath(baseFolder);
	
		// For each argument after the baseFolder, treat it as a different file/glob
		const files: any= [];
	
		// If empty, add all files
		const argsArray = args;	
		if(argsArray.length === 0) {
			argsArray.push("*");
		}
	
		for(let argument of argsArray) {
			var foundFiles = JavaTypes.SpecsIo.getPathsWithPattern(baseFolderFile, argument, false, "FILES_AND_FOLDERS");
			for(let file of foundFiles) {
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
	static getFolders(baseFolder: any, ...args: String[]) {

		var paths = Io.getPaths(baseFolder, ...args); 

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
	static getFiles (baseFolder: string | File, pattern: string | Array<any>, isRecursive: boolean) {
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
			const files: Array<File> = [];

			for(var singlePattern of pattern) {
				var newFiles = Io.getFiles(baseFolder, singlePattern, isRecursive);
				for(var newFile of newFiles) {
					files.push(newFile);
				}
			}
		
			return files;
		}


		const list = JavaTypes.SpecsIo.getPathsWithPattern(Io.getPath(baseFolder), pattern.toString(), isRecursive, "FILES");
		const files: Array<File> = [];

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
	static readLines(fileOrBaseFolder: any, optionalFile?: any) {
		return JavaTypes.LaraIo.readLines(Io.getPath(fileOrBaseFolder, optionalFile));	
	}


	
	
	
	/**
	 * Deletes the given file.
	 */
	static deleteFile(fileOrBaseFolder: any, optionalFile?: any) {
		const file = Io.getPath(fileOrBaseFolder, optionalFile);
		if(!Io.isFile(file)) {
			return;
		}
	
		return JavaTypes.LaraIo.deleteFile(file);
	}




	/**
 	* Each argument is a file that will be deleted.
 	*/
	static deleteFiles(...args: any){
		for(let argument of args) {
			Io.deleteFile(argument);
		}
	}

	/**
 	* Deletes a folder and its contents.
 	*
 	* @return true if both the contents and the folder could be deleted
 	*/
	static deleteFolder(folderPath: string) {
		const folder = Io.getPath(folderPath);
		return JavaTypes.SpecsIo.deleteFolder(folder);
	}

	/**
 	* Deletes the contents of a folder.
 	*/
	static deleteFolderContents(folderPath: string) {
		const folder = Io.getPath(folderPath);
		return JavaTypes.SpecsIo.deleteFolderContents(folder);
	}

	/**
 	* @return true if and only if the file denoted by this abstract pathname exists and is a normal file; false otherwise
 	*/
	static isFile(path: any) {
		if(typeof path === "string") {
			path = Io.getPath(path);
		}
		return path.isFile();
	}

	/**
 	* @return true if and only if the file denoted by this abstract pathname exists and is a folder; false otherwise
 	*/
	static isFolder(path: any) {
		if(typeof path === "string") {
			path = Io.getPath(path);
		}
		return path.isDirectory();
	}

	static readJson(path:string) {
		return fileToJSON(path);             
	}

	static writeJson (path: string, object: any) {
		JSONtoFile(path, object);
	}

	static copyFile(filepath: string, destination: string) {
		checkDefined(filepath, "filepath", "Io.copyFile");
		checkTrue(Io.isFile(filepath), "Io.copyFile: given filepath '"+filepath+"' is not a file");
	
		return JavaTypes.SpecsIo.copy(Io.getPath(filepath), Io.getPath(destination));
}

	static copyFolder(filepath: string, destination: string, verbose: any) {
		checkDefined(filepath, "filepath", "Io.copyFolder");
		checkDefined(destination, "destination", "Io.copyFolder");
	
		if(verbose === undefined) {
			verbose = false;
		}
	
		return JavaTypes.SpecsIo.copyFolder(Io.getPath(filepath), Io.getPath(destination), verbose);
	}

	/**
 	* Returns the given path, without extension.
 	*
 	* @param {string|#java.io.File} path 
 	*/
	static removeExtension(path: string) {
		return JavaTypes.SpecsIo.removeExtension(path);
	}

	/**
	 * Returns the extension of the given path.
	 *
	 * @param {string|#java.io.File} path 
	 */
	static getExtension(path: string) {
		return JavaTypes.SpecsIo.getExtension(path);
	}

	/**
	 * @param {string} path The path of the file to write.
	 * @param {string} content The contents to write.
	 *
	 * @return {J#java.io.File} the file to where the contents where written.
	 */
	static writeFile(path: string, content: string) {
		const file: File = Io._newFile(path);
		JavaTypes.SpecsIo.write(file,content);
		return file;
	}

	/**
 	* @param {string} path The path of the file to read.
 	*
 	* @return {string} the contents of the file.
 	*/
	static readFile(path: string) {
		const file = Io._newFile(path);
		const content = JavaTypes.SpecsIo.read(file);
		return content;
	}

	static appendFile(path: string, content: any) {
		const file = Io._newFile(path);
		JavaTypes.SpecsIo.append(file,content);
	}

	/**
 	* Returns the path of 'targetFile', relative to 'baseFile'. 
 	* 
 	* If the file does not share a common ancestor with baseFile, returns undefined.
 	*/
	static getRelativePath(targetFile: any, baseFile: any) {
		const relativePath = JavaTypes.SpecsIo.getRelativePath(Io.getPath(targetFile), Io.getPath(baseFile));
	
		if(Io.getPath(relativePath).isAbsolute()) {
			return undefined;
		}
	
		return relativePath;
	}

	/**
 	* 	The system-dependent path-separator (e.g., : or ;).
 	*/
	static getPathSeparator() {
		return JavaTypes.JavaFile.pathSeparator;
	}

	/**
 	* 	The system-dependent name-separator (e.g., / or \).
 	*/
	static getSeparator() {
		return JavaTypes.JavaFile.separator;
	}

	static md5(fileOrBaseFolder: any, optionalFile?: File) {
		return JavaTypes.SpecsIo.getMd5(Io.getPath(fileOrBaseFolder, optionalFile));
	}

	static getWorkingFolder() {
		return JavaTypes.SpecsIo.getWorkingDir();
	}

	/**
 	* If value is a string that ends in .json, assume it is a file to a json object and parses it. 
 	* If it is a string but does not end in json, assume it is a stringified object.
 	* Otherwise, returns the object as it is.
 	*/ 
	static jsonObject(value: any) {
		
		if(isString(value)) {
			if(value.endsWith(".json")) {
				return Io.readJson(Io.getPath(value));
			} else {
				return JSON.parse(value);
			}	
		}
		
		return value;
	}
};
