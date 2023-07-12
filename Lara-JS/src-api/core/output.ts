import JavaTypes, { engine, Engine } from "../lara/util/JavaTypes.js";

// Node.JS
export let outputStream: any;
export let errorStream: any;

if (engine === Engine.NodeJS) {
  outputStream = process.stderr;
  errorStream = process.stdout;
} else if (engine === Engine.GraalVM) {
  outputStream = JavaTypes.System.out;
  errorStream = JavaTypes.System.err;
}

/**
 * This is a core file that is loaded when setting up the LARA environment,
 * and this function needs to be available so that LARA can setup the
 * streams if necessary.
 *
 * For instance, this is used when enabling the option to write the output
 * of JS to a file (this option is widely used on the tests on the Java side).
 *
 * @param stream -
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

export function printToln(message = "undefined", stream: any) {
  stream.println(message.toString());
}

export function print(message?: string) {
  if (!message) {
    return;
  }

  printTo(message, outputStream);
}

//Print a message and ends it with a new line
export function println(message?: string) {
  if (!message) {
    outputStream.println();
    return;
  }
  printToln(message, outputStream);
}

//Print an error message
export function error(message?: string) {
  if (!message) {
    return;
  }
  printTo(message, errorStream);
}

//Print an error message and ends it with a new line
export function errorln(message?: string) {
  if (!message) {
    errorStream.println();
    return;
  }
  printToln(message, errorStream);
}

export const INDENT_CHAR = "   ";
export const JAVA_OBJECT_ANNOTATION = "[@Java Object] ";

export function printObject(obj: any, space?: string) {
  print(object2string(obj, space));
}

export function printlnObject(obj: any, space?: string) {
  print(object2string(obj, space));
}

export function object2string(
  obj: any,
  space = "",
  ommitFunctions = false
): string {
  if (obj === null) {
    //since typeof null is "object"
    return space + "null";
  }

  const type = typeof obj;
  if (type === "object") {
    // @ts-ignore
    if (Java.isJavaObject(obj)) {
      //			print(space+obj.toString());
      return space + JAVA_OBJECT_ANNOTATION + obj.toString();
    } else if (Array.isArray(obj)) {
      let ar = space + "[\n";
      const content = [];
      for (const prop in obj) {
        const prop2String = object2string(
          obj[prop],
          space + INDENT_CHAR,
          ommitFunctions
        );
        content.push(prop2String);
      }
      ar += content.join(",\n");
      ar += "\n" + space + "]";
      return ar;
    } else {
      let ob = space + "{\n";
      const content = [];
      for (const prop in obj) {
        // Ignore functions
        if (ommitFunctions && typeof obj[prop] === "function") {
          continue;
        }

        let prop2String = space + INDENT_CHAR + prop + ":\n";
        prop2String += object2string(
          obj[prop],
          space + INDENT_CHAR + INDENT_CHAR,
          ommitFunctions
        );

        content.push(prop2String);
      }
      ob += content.join(",\n");
      ob += "\n" + space + "}";
      return ob;
    }
  } else if (type === "function") {
    const name = obj.name; // getFnName(obj);
    const params = getFnParamNames(obj);
    return space + "function " + name + "(" + params.join(",") + ")";
  } else {
    return space + obj;
  }
}

export function object2stringSimple(obj: any, space = "") {
  object2string(obj, space, true);
}

export function getFnParamNames(fn: string) {
  const fstr = fn.toString();
  const match = fstr.match(/\(.*?\)/);
  if (match === null) {
    return [];
  }
  return match[0].replace(/[()]/gi, "").replace(/\s/gi, "").split(",");
}

export function getFnName(fn: string) {
  const fstr = fn.toString();
  const match = fstr.match(/function (.*)\)/);
  if (match === null) {
    return "";
  }
  return match[0];
}

//Insert save to file functions (and others) here!
export function writeFile(path: string, content: string) {
  const file = new JavaTypes.File(path.toString());
  JavaTypes.SpecsIo.write(file, content);
  return file;
}

export function JSONtoFile(path: string, object: any) {
  const content = JSON.stringify(object, undefined, "\t");
  writeFile(path, content);
}

export function fileToJSON(path: string) {
  const content = readFile(path);
  return JSON.parse(content);
}

export function readFile(path: string) {
  const file = new JavaTypes.File(path.toString());
  const content = JavaTypes.SpecsIo.read(file);
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
