import fs from "fs";
import { capitalizeFirstLetter, type ConvertedAction, type ConvertedAttribute, type ConvertedEnum, type ConvertedJoinpoint } from "./convert-joinpoint-specification.ts";

export function generateJoinpoints(joinpoints: ConvertedJoinpoint[], outputFile: number) {
  for (const jp of joinpoints) {
    generateJoinpoint(jp, outputFile, joinpoints);
  }
}

function generateJoinpoint(jp: ConvertedJoinpoint, outputFile: number, joinpoints: ConvertedJoinpoint[]) {
  fs.writeSync(
    outputFile,
    `${generateDocumentation(jp.tooltip)}export class ${jp.name}${
      jp.extends ? ` extends ${jp.extends}` : ""
    } {\n`
  );
  if (jp.name === "LaraJoinPoint") {
    fs.writeSync(
      outputFile,
      "  /**\n   * @internal\n   */\n" +
        "  static readonly _defaultAttributeInfo: {readonly map?: any, readonly name: string | null, readonly type?: any, readonly jpMapper?: any} = {\n" +
        `    name: ${jp.defaultAttribute ? '"' + jp.defaultAttribute + '"' : "null"},\n` +
        "  };\n"
    );
    fs.writeSync(
      outputFile,
      "  /**\n   * @internal\n   */\n" +
        `  _javaObject!: any;
  constructor(obj: any) {
    this._javaObject = obj;
  }\n`
    );
  } else {
    fs.writeSync(
      outputFile,
      "  /**\n   * @internal\n   */\n" +
        "  static readonly _defaultAttributeInfo: {readonly map?: DefaultAttributeMap, readonly name: string | null, readonly type?: PrivateMapper, readonly jpMapper?: typeof JoinpointMapper} = {\n" +
        `    name: ${jp.defaultAttribute ? '"' + jp.defaultAttribute + '"' : "null"},\n` +
        "  };\n"
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

function generateDocumentation(tooltip: string | undefined): string {
  if (!tooltip) {
    return "";
  }
  return `  /**\n   * ${tooltip.split("\n").join("\n   * ")}\n   */\n`;
}

function escapeJavaReservedKeywords(name: string): string {
  const reservedKeywords = new Set([
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
    "class", "const", "continue", "default", "do", "double", "else", "enum",
    "extends", "final", "finally", "float", "for", "goto", "if", "implements",
    "import", "instanceof", "int", "interface", "long", "native", "new",
    "package", "private", "protected", "public", "return", "short",
    "static", "strictfp", "super", "switch", "synchronized", "this",
    "throw", "throws", "transient", "try", "void", "volatile",
    // Also include literals and special identifiers
    "true", "false", "null"
  ]);
  if (reservedKeywords.has(name)) {
    return `_${name}`;
  }
  return name;
}

function generateJoinpointAttribute(attribute: ConvertedAttribute, outputFile: number, joinpointActions: ConvertedAction[]) {
  if (attribute.name === "data") {
    fs.writeSync(
      outputFile,
      `${generateDocumentation(attribute.tooltip)}  get ${attribute.name}(): any { const data = (this._javaObject.${attribute.name}() as string | undefined); return data ? JSON.parse(data) : data; }\n`
    );
  } else {
    fs.writeSync(
      outputFile,
      `${generateDocumentation(attribute.tooltip)}  get ${attribute.name}(): ${
        attribute.type
      } { return ${
        attribute.name === "node" ? "" : "wrapJoinPoint"
      }(this._javaObject.${escapeJavaReservedKeywords(attribute.name)}()) }\n`
    );
  }

  let setterActions = joinpointActions.filter(
    (action) => action.name === `set${capitalizeFirstLetter(attribute.name)}`
  );

  if (setterActions.length === 0) {
    return;
  }

  if (setterActions[0].overloads.length > 0) {
    setterActions = setterActions[0].overloads.filter((overload) => {
      const requiredParameters = overload.parameters.reduce(
        (acc, parameter) => {
          if (parameter.default === undefined) {
            return acc + 1;
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
    .reduce((type: string[], action) => {
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
    )}(${attribute.name === "data" ? "JSON.stringify" : "unwrapJoinPoint"}(value)); }\n`
  );
}

function generateJoinpointActionParameters(action: ConvertedAction): string {
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

function generateJoinpointAction(action: ConvertedAction, outputFile: number, joinpoints: ConvertedJoinpoint[]) {
  const parameters = generateJoinpointActionParameters(action);

  const callParameters = action.parameters
    .map(
      (parameter) =>
        `${action.name === "setData" ? "JSON.stringify" : "unwrapJoinPoint"}(${parameter.name})`
    )
    .join(", ");

  fs.writeSync(
    outputFile,
    `${generateDocumentation(action.tooltip)}  ${action.name}(${parameters}): ${
      action.returnType
    } { return wrapJoinPoint(this._javaObject.${
      escapeJavaReservedKeywords(action.name)
    }(${callParameters})); }\n`
  );
}

export function generateEnums(enums: ConvertedEnum[], outputFile: number) {
  for (const e of enums) {
    generateEnum(e, outputFile);
  }
}

function generateEnum(e: ConvertedEnum, outputFile: number) {
  fs.writeSync(outputFile, `/**
 * This is supposed to be an enum, but Node.js v25 does bot support TS' enums, only erasable-syntax.
 * Revert to an enum when Node.js supports it, or when we move to a different engine that supports it.
 * This and the "type" declaration below.
 */\n`);
  fs.writeSync(outputFile, `export const ${e.name} = {\n`);
  e.entries.forEach((entry) => {
    fs.writeSync(outputFile, `  ${entry.name}: "${entry.value}",\n`);
  });
  fs.writeSync(outputFile, `} as const;\n`);
  fs.writeSync(
    outputFile,
    `export type ${e.name} = typeof ${e.name}[keyof typeof ${e.name}];\n\n`
  );
}
