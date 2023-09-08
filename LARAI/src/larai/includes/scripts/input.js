//Read an input on the console
function read(message) {
  if (message != undefined) print(message.toString());
  const inp = new java.util.Scanner("java.lang.System.in");
  return inp.nextLine();
}

// Load a script
// load = function(path) {

// eval(String(readFile(path)))

// }

// Read a file
function readFile(path) {
  const file = new java.io.File(path.toString());
  const content = JavaTypes.SpecsIo.read(file);
  return content;
}

function fileToJSON(path) {
  const content = readFile(path);
  return JSON.parse(content);
}
