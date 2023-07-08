/* eslint-disable */
import JavaTypes from "../lara/util/JavaTypes.js";

export let outputStream = JavaTypes.getJavaSystem().out;
export let errorStream = JavaTypes.getJavaSystem().err;

/**
 * This is a core file that is loaded when setting up the LARA environment,
 * and this function needs to be available so that LARA can setup the
 * streams if necessary.
 *
 * For instance, this is used when enabling the option to write the output
 * of JS to a file (this option is widely used on the tests on the Java side).
 *
 * @param stream
 *
 */
export function setPrintStream(stream: any) {
  // TODO: debug-level message saying that the printstream is being set
  outputStream = stream;
  errorStream = stream;
}

export function printTo(message: string | null | undefined, stream: any) {
  if (message === null || message === undefined) {
    stream.print(message);
  } else {
    stream.print(message.toString());
  }
}

export function printToln(message: string | null | undefined, stream: any) {
  if (message === null) {
    message = "null";
  }

  if (message === undefined) {
    message = "undefined";
  }

  stream.println(message.toString());
}

export function print(message?: string | undefined) {
  if (arguments.length == 0) {
    return;
  }

  printTo(message, outputStream);
}

//Print a message and ends it with a new line
export function println(message?: string | undefined) {
  if (arguments.length == 0) {
    outputStream.println();
    return;
  }
  printToln(message, outputStream);
}

//Print an error message
export function error(message?: string | undefined) {
  if (arguments.length == 0) {
    return;
  }
  printTo(message, errorStream);
}

//Print an error message and ends it with a new line
export function errorln(message?: string | undefined) {
  if (arguments.length == 0) {
    errorStream.println();
    return;
  }
  printToln(message, errorStream);
}

export var INDENT_CHAR = "   ";
export var JAVA_OBJECT_ANNOTATION = "[@Java Object] ";

export function printObject(obj: any, space?: string | undefined) {
  var str = object2string(obj, space);
  print(str);
}

export function printlnObject(obj: any, space?: string | undefined) {
  var str = object2string(obj, space);
  print(str);
  println("");
}

//export function object2string(obj, space, ommitFunctions){
export function object2string(obj: any, space?: string | undefined): string {
  // ommitFunctions not working, printing more than intended

  if (space === undefined) space = "";

  /*
	if(ommitFunctions === undefined) {
		ommitFunctions = false;
	}
*/
  if (obj === null)
    //since typeof null is "object"
    return space + "null";

  var type = typeof obj;
  if (type === "object") {
    // @ts-ignore
    if (Java.isJavaObject(obj)) {
      //			print(space+obj.toString());
      return space + JAVA_OBJECT_ANNOTATION + obj.toString();
    } else if (Array.isArray(obj)) {
      var ar = space + "[\n";
      var content = [];
      for (var prop in obj) {
        var prop2String = object2string(obj[prop], space + INDENT_CHAR);
        //var prop2String = object2string(obj[prop],space+INDENT_CHAR, ommitFunctions);
        content.push(prop2String);
      }
      ar += content.join(",\n");
      ar += "\n" + space + "]";
      return ar;
    } else {
      var ob = space + "{\n";
      var content = [];
      for (var prop in obj) {
        var prop2String = space + INDENT_CHAR + prop + ":\n";
        prop2String += object2string(
          obj[prop],
          space + INDENT_CHAR + INDENT_CHAR
        );
        //prop2String += object2string(obj[prop],space+INDENT_CHAR+INDENT_CHAR, ommitFunctions);
        content.push(prop2String);
      }
      ob += content.join(",\n");
      ob += "\n" + space + "}";
      return ob;
    }
    //}else if(type === "function" && (!ommitFunctions)){
  } else if (type === "function") {
    var name = obj.name; // getFnName(obj);
    var params = getFnParamNames(obj);
    return space + "function " + name + "(" + params.join(",") + ")";
  } else {
    return space + obj;
  }
}

export function object2stringSimple(
  obj: any | null,
  space?: string | undefined
) {
  if (space === undefined) space = "";

  if (obj === null)
    //since typeof null is "object"
    return space + "null";

  var type = typeof obj;
  if (type === "object") {
    // @ts-ignore
    if (Java.isJavaObject(obj)) {
      //			print(space+obj.toString());
      return space + JAVA_OBJECT_ANNOTATION + obj.toString();
    } else if (Array.isArray(obj)) {
      var ar = space + "[\n";
      var content = [];
      for (var prop in obj) {
        var prop2String = object2stringSimple(obj[prop], space + INDENT_CHAR);
        content.push(prop2String);
      }
      ar += content.join(",\n");
      ar += "\n" + space + "]";
      return ar;
    } else {
      var ob = space + "{\n";
      var content = [];
      for (var prop in obj) {
        // Ignore functions
        if (typeof obj[prop] === "function") {
          continue;
        }

        var prop2String = space + INDENT_CHAR + prop + ":\n";
        prop2String += object2stringSimple(
          obj[prop],
          space + INDENT_CHAR + INDENT_CHAR
        );
        content.push(prop2String);
      }
      ob += content.join(",\n");
      ob += "\n" + space + "}";
      return ob;
    }
  } else if (type === "function") {
    var name = obj.name; // getFnName(obj);
    var params = getFnParamNames(obj);
    return space + "function " + name + "(" + params.join(",") + ")";
  } else {
    return space + obj;
  }
}

export function getFnParamNames(fn: string) {
  var fstr = fn.toString();
  const match = fstr.match(/\(.*?\)/);
  if (match === null) {
    return [];
  }
  return match[0].replace(/[()]/gi, "").replace(/\s/gi, "").split(",");
}

export function getFnName(fn: string) {
  var fstr = fn.toString();
  const match = fstr.match(/function (.*)\)/);
  if (match === null) {
    return "";
  }
  return match[0];
}

//Insert save to file functions (and others) here!
export function writeFile(path: string, content: string) {
  var file = new (JavaTypes.getJavaFile())(path.toString());
  JavaTypes.getJavaSpecsIo().write(file, content);
  return file;
}

export function JSONtoFile(path: string, object: any) {
  var content = JSON.stringify(object, undefined, "\t");
  writeFile(path, content);
}

export function fileToJSON(path: string) {
  var content = readFile(path);
  return JSON.parse(content);
}

export function readFile(path: string) {
  var file = new (JavaTypes.getJavaFile())(path.toString());
  var content = JavaTypes.getJavaSpecsIo().read(file);
  return content;
}

// TODO: In order for console.log() to also log to .txt files this needs to be implemented

/**
 * Implementation of console.log according to Mozilla: https://developer.mozilla.org/en-US/docs/Web/API/Console/log
 */
/*
console.log = function () {
  lara_console_helper(outputStream, ...arguments);
};

console.err = function () {
  lara_console_helper(errorStream, ...arguments);
};
*/
/**
 * Implementation of console.log according to Mozilla: https://developer.mozilla.org/en-US/docs/Web/API/Console/log
 */
/*
let lara_console_helper = function (stream : any) {
  const args = arrayFromArgs(arguments, 1);

  // Return if no args
  if (args.length === 0) {
    return;
  }

  // When there is only one argument
  var msg = args[0];
  if (args.length === 1) {
    printToStream(stream, msg.toString());
    return;
  }

  // If first argument is a string, interpret remaining args as substitution strings
  if (typeof msg === "string" || msg instanceof String) {
    var subst = [];
    for (var i = 1; i < args.length; i++) {
      subst.push(args[i]);
    }

    printfToStream(stream, msg.toString(), subst);

    return;
  }

  // Concatenate all arguments
  for (var i = 1; i < args.length; i++) {
    msg = msg + args[i].toString();
  }

  printToStream(stream, msg);
};
*/
