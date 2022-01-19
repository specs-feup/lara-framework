//Read an input on the console
function read(message) {
	if (message != undefined)
		print(message.toString());
	var inp = new java.util.Scanner('java.lang.System.in');
	return inp.nextLine();
}

// Load a script
// load = function(path) {

	// eval(String(readFile(path)))

// }

// Read a file
function readFile(path) {
	var file = new java.io.File(path.toString());
	var content = SpecsIo.read(file);
	return content;
}

function fileToJSON(path) {
	var content = readFile(path);
	return JSON.parse(content);
}
