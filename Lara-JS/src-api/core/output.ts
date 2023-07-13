import JavaTypes from "../lara/util/JavaTypes.js";

/**
 * Prints a message to the standard output.
 *
 * @deprecated Use console.log instead
 */
export function print(message?: string) {
  console.log(message);
}

/**
 * Prints a message to the standard output.
 *
 * @deprecated Use console.log instead
 */
export function println(message?: string) {
  console.log(message);
}

/**
 * Prints a message to the standard error.
 *
 * @deprecated Use console.error instead
 */
export function error(message?: string) {
  console.error(message);
}

/**
 * Prints a message to the standard error.
 *
 * @deprecated Use console.error instead
 */
export function errorln(message?: string) {
  console.error(message);
}

export const INDENT_CHAR = "   ";
export const JAVA_OBJECT_ANNOTATION = "[@Java Object] ";

/**
 * @deprecated Use object2string() with console.log() instead
 */
export function printObject(obj: any, space?: string) {
  console.log(object2string(obj, space));
}

/**
 * @deprecated Use object2string() with console.log() instead
 */
export function printlnObject(obj: any, space?: string) {
  console.log(object2string(obj, space));
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

/**
 * @deprecated Use the regular object2string() instead
 */
export function object2stringSimple(obj: any, space = "") {
  object2string(obj, space, true);
}

function getFnParamNames(fn: string) {
  const fstr = fn.toString();
  const match = fstr.match(/\(.*?\)/);
  if (match === null) {
    return [];
  }
  return match[0].replace(/[()]/gi, "").replace(/\s/gi, "").split(",");
}

function getFnName(fn: string) {
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
