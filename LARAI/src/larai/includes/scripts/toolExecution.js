function run(tool, args, jpname, name, verbose, pipe) {

	args = (args == undefined) ? [] : args;
	jpname = (jpname == undefined) ? null : jpname;
	name = (name == undefined) ? null : name;
	verbose = (verbose == undefined) ? default_verbose_level : verbose;
	pipe = (pipe === undefined)? null:pipe;
	
	// if (pipe == undefined) {

		// verbose = 2;
		// pipe = null;
	// } else if (pipe == null) {
		// verbose = 0;
	// } else {
		// verbose = 1;
	// }

	var argsToSource = "";
	try {
		argsToSource = JSON.stringify(args);
	} catch (e) {
		errorln("Exception while converting arguments to source: " + e);
	}

	try {
		report = LARA_SYSTEM.run(tool, args, jpname, name, verbose, pipe,
				argsToSource);
		return report;
	} catch (e) {
		errorln(e);
		throw e;
	}
}

var default_verbose_level = 2;
var default_pipe = 2;

function cmd(command, args, verbose, pipe, exitOnError) {
	args = (args === undefined) ? [] : args;
	verbose = (verbose === undefined) ? default_verbose_level : verbose;
	pipe = (pipe === undefined)? null:pipe;
	// if (pipe == undefined) {

		// verbose = 2;
		// pipe = null;
	// } 
	// else if (pipe == null) {
		// verbose = 0;
	// } else {
		// verbose = 1;
	// }
	
	exitOnError = (exitOnError == undefined) ? false : exitOnError;

	
	var result = LARA_SYSTEM.execute(command, args, verbose, pipe);
	
	if(exitOnError && result !== 0) {
		throw "Command returned exit code " + result;
	}
	
	return result;
}

function cmdRequired(command, args, verbose, pipe) {
	return cmd(command, args, verbose, pipe, true);
}

function setWorkingDir(workDir) {
     LARA_SYSTEM.setWorkingDir(workDir);
}

function report(tool, args, verbose, pipe) {
	// println("REPORT: BEGIN");

	args = (args == undefined) ? [] : args;
	verbose = (verbose == undefined) ? default_verbose_level : verbose;

	if (pipe == undefined) {

		verbose = 2;
		pipe = null;
	} else if (pipe == null) {
		verbose = 0;
	} else {
		verbose = 1;
	}
	
	var argsToSource = "";
	try {
		argsToSource = JSON.stringify(args);
	} catch (e) {
	}
	var reportStr = "";
	
	// try {
		// println("REPORT: EXEC");
		reportStr = '' + LARA_SYSTEM.report(tool, args, argsToSource, verbose);
	// } catch (e) {
		// //errorln(e);
		// var ToolException = Java.type("org.lara.interpreter.exception.ToolExecutionException");
		// throw new e;
	// }
		// println("REPORT: EVAL");
	eval(reportStr);
		// println("REPORT: END");
}

function toJavaArray(classType, array) {
	var argv = java.lang.reflect.Array.newInstance(classType, array.length);
	for ( var i = 0; i < array.length; i++){
		java.lang.reflect.Array.set(argv, i, array[i]);
	}
	return argv;
}

function exportVar(varName, value) {
	if (value == undefined){
		LARA_SYSTEM.export(varName);
	} else{
		LARA_SYSTEM.export(varName, value);
	}
};
