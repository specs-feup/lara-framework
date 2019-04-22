import lara.Io;

/**
 * Utility methods related with compilation. 
 *
 * @class
 */
var Compilation = {};

/**
 * @return {String[]} paths to sources that the current program depends on.
 */
Compilation.getExtraSources = function() {
	notImplemented("Compilation.getExtraSources");
	//return [];
}

/**
 * @return {String[]} paths to includes that the current program depends on.
 */
Compilation.getExtraIncludes = function() {
	notImplemented("Compilation.getExtraIncludes");
	//return [];
}

/**
 * Adds a path to an include that the current program depends on.
 *
 * @param {string} path - Path to include
 */
Compilation.addExtraInclude = function(path) {
	notImplemented("Compilation.addExtraInclude");
}

/**
 * Adds a path based on a git repository to an include that the current program depends on.
 *
 * @param {string} gitRepository - URL to git repository
 * @param {string?} path - Path to include from the root of the git repository
 */
Compilation.addExtraIncludeFromGit = function(gitRepository, path) {
	notImplemented("Compilation.addExtraIncludeFromGit");
}

/**
 * Adds a path to a source that the current program depends on.
 *
 * @param {string} path - Path to source
 */
Compilation.addExtraSource = function(path) {
	notImplemented("Compilation.addExtraSource");
}

/**
 * Adds a path based on a git repository to a source that the current program depends on.
 *
 * @param {string} gitRepository - URL to git repository
 * @param {string?} path - Path to source from the root of the git repository
 */
Compilation.addExtraSourceFromGit = function(gitRepository, path) {
	notImplemented("Compilation.addExtraSourceFromGit");
}


Compilation.getExtraSourceFiles = function() {
	var extraSourcesArray = [];
	
	for(var extraSource of Compilation.getExtraSources()) {
		debug("Compilation.getExtraSourceFiles(): Adding external source '" + extraSource + "'");
		if(Io.isFile(extraSource)) {
			extraSourcesArray.push(extraSource);
			//println("Extra source 1:" + extraSource);
		} else if(Io.isFolder(extraSource)) {
			for(var sourcePath of Io.getPaths(extraSource)) {
				extraSourcesArray.push(sourcePath);
				//println("Extra source 2:" + sourcePath);
			}
		} else {
			println("Compilation.getExtraSourceFiles: Extra source ' " + extraSource +  " ' does not exist");
		}
	}	
	//println("Extra sources:" + extraSourcesArray);
	return extraSourcesArray;
}