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
    generateJoinpointAttribute(attribute, outputFile, jp.actions);
  }

  for (const action of jp.actions) {
    if (action.overloads.length > 0) {
      // Action with overloads
      action.overloads.forEach((overload) => {
        fs.writeSync(
          outputFile,
          `${generateDocumentation(overload.tooltip)}  ${
            overload.name
          }(${generateJoinpointActionParameters(overload)}): ${
            overload.returnType
          };\n`
        );
      });
    }

    // Regular action
    generateJoinpointAction(action, outputFile, joinpoints);
  }

  fs.writeSync(outputFile, `}\n\n`);
}

function generateDocumentation(tooltip) {
  if (!tooltip) {
    return "";
  }
  return `  /**\n   * ${tooltip.split("\n").join("\n   * ")}\n   */\n`;
}

function generateJoinpointAttribute(attribute, outputFile, joinpointActions) {
  fs.writeSync(
    outputFile,
    `${generateDocumentation(attribute.tooltip)}  get ${attribute.name}(): ${
      attribute.type
    } { return ${
      attribute.name === "node" ? "" : "wrapJoinPoint"
    }(this._javaObject.get${capitalizeFirstLetter(attribute.name)}()) }\n`
  );

  let setterActions = joinpointActions.filter(
    (action) => action.name === `set${capitalizeFirstLetter(attribute.name)}`
  );

  if (attribute.name === "userField") {
    console.log("HERE");
  }

  if (setterActions.length === 0) {
    return;
  }


  if (setterActions[0].overloads.length > 0) {
    setterActions = setterActions[0].overloads.filter((overload) => {
      const requiredParameters = overload.parameters.reduce(
        (acc, parameter) => {
          if (parameter.defaultValue === undefined) {
            return acc+1;
          }
          return acc;
        },
        0
      );

      return requiredParameters <= 1;
    });
  }

  if (setterActions.length === 0) {
    return;
  }

  const setterParameterType = setterActions
    .reduce((type, action) => {
      if (action.parameters.length) {
        type.push(action.parameters[0].type);
      }
      return type;
    }, [])
    .join(" | ");

  fs.writeSync(
    outputFile,
    `${generateDocumentation(attribute.tooltip)}  set ${
      attribute.name
    }(value: ${setterParameterType}) { this._javaObject.set${capitalizeFirstLetter(
      attribute.name
    )}(unwrapJoinPoint(value)); }\n`
  );
}

/**
 * Handle actions in the joinpoint specification that do not conform to the conventions
 * @param {string} actionName
 * @returns
 */
function parseExceptions(actionName) {
  if (!actionName.startsWith("get")) {
    return actionName;
  }

  const originalActionName =
    actionName.slice(3, 4).toLowerCase() + actionName.slice(4);
  switch (originalActionName) {
    case "instanceOf":
      return originalActionName;
    default:
      return actionName;
  }
}

function generateJoinpointActionParameters(action) {
  return action.parameters
    .map((parameter) => {
      let paramStr = parameter.name;
      if (parameter.default !== undefined) {
        if (parameter.default === '"null"') {
          paramStr += `?: ${parameter.type}`;
        } else {
          paramStr += `: ${parameter.type} = ${JSON.parse(parameter.default)}`;
        }
      } else {
        paramStr += `: ${parameter.type}`;
      }
      return paramStr;
    })
    .join(", ");
}

function generateJoinpointAction(action, outputFile, joinpoints) {
  const parameters = generateJoinpointActionParameters(action);

  const callParameters = action.parameters
    .map((parameter) => `unwrapJoinPoint(${parameter.name})`)
    .join(", ");

  fs.writeSync(
    outputFile,
    `${generateDocumentation(action.tooltip)}  ${action.name}(${parameters}): ${
      action.returnType
    } { return wrapJoinPoint(this._javaObject.${parseExceptions(
      action.name
    )}(${callParameters})); }\n`
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
    fs.writeSync(outputFile, `  ${entry} = "${entry.toLowerCase()}",\n`);
  });
  fs.writeSync(outputFile, `}\n\n`);
}
