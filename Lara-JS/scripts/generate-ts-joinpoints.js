import fs from "fs";
import { capitalizeFirstLetter } from "./convert-joinpoint-specification.js";

export function generateJoinpoints(joinpoints, outputFile) {
  for (const jp of joinpoints) {
    generateJoinpoint(jp, outputFile, joinpoints);
  }
}

function generateJoinpoint(jp, outputFile, joinpoints) {
  fs.writeSync(
    outputFile,
    `${generateDocumentation(jp.tooltip)}export class ${jp.name}${
      jp.extends ? ` extends ${jp.extends}` : ""
    } {\n`
  );
  if (jp.name === "LaraJoinPoint") {
    fs.writeSync(
      outputFile,
      `  _javaObject!: any;
  constructor(obj: any) {
    this._javaObject = obj;
  }\n`
    );
  }

  for (const attribute of jp.attributes) {
    generateJoinpointAttribute(attribute, outputFile);
  }

  // TODO: remove this set as it is here because I can't deal with method overloading
  let actionNameSet = new Set();
  for (const action of jp.actions) {
    if (!actionNameSet.has(action.name)) {
      generateJoinpointAction(action, outputFile, joinpoints);
      actionNameSet.add(action.name);
    }
  }

  fs.writeSync(outputFile, `}\n\n`);
}

function generateDocumentation(tooltip) {
  if (!tooltip) {
    return "";
  }
  return `  /**\n   * ${tooltip.split("\n").join("\n   * ")}\n   */\n`;
}

function generateJoinpointAttribute(attribute, outputFile) {
  fs.writeSync(
    outputFile,
    `${generateDocumentation(attribute.tooltip)}  get ${attribute.name}(): ${
      attribute.type
    } { return ${
      "wrapJoinPoint(this._javaObject.get" +
      capitalizeFirstLetter(attribute.name)
    }()) }\n`
  );
}

function generateJoinpointAction(action, outputFile, joinpoints) {
  const parameters = action.parameters
    .map((parameter) => {
      let paramStr = `${parameter.name}: ${parameter.type}`;
      if (parameter.default !== undefined) {
        paramStr += ` = ${JSON.parse(parameter.default)}`;
      }
      return paramStr;
    })
    .join(", ");

  const callParameters = action.parameters
    .map((parameter) => `unwrapJoinPoint(${parameter.name})`)
    .join(", ");

  fs.writeSync(
    outputFile,
    `${generateDocumentation(action.tooltip)}  ${action.name}(${parameters}): ${
      action.returnType
    } { return wrapJoinPoint(this._javaObject.${
      action.name
    }(${callParameters})); }\n`
  );
}

export function generateEnums(enums, outputFile) {
  for (const e of enums) {
    generateEnum(e, outputFile);
  }
}

function generateEnum(e, outputFile) {
  fs.writeSync(outputFile, `export enum ${e.name} {\n`);
  e.entries.forEach((entry) => {
    fs.writeSync(outputFile, `  ${entry},\n`);
  });
  fs.writeSync(outputFile, `}\n\n`);
}
