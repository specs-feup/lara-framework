#!/usr/bin/env node

export type JSON_LanguageSpecification = {
  root: string;
  rootAlias: string;
  importEnums?: string[];
  children: (JSON_JoinpointSpecification | JSON_EnumSpecification)[];
};

type JSON_JoinpointSpecification = {
  type: "joinpoint";
  name: string;
  extends: string;
  defaultAttr?: string;
  tooltip?: string;
  children?: JSON_JoinpointMemberSpecification[];
};

type JSON_JoinpointMemberSpecification = {
  type: "attribute" | "action";
  tooltip?: string;
  children: JSON_ParameterSpecification[];
};

type JSON_ParameterSpecification = {
  name: string;
  type: string;
  defaultValue: string;
}

type JSON_EnumSpecification = {
  type: "enum";
  name: string;
  extends?: string;
  children: { value: string }[];
};

export type ConvertedSpecification = {
  joinpoints: ConvertedJoinpoint[];
  enums: ConvertedEnum[];
  importEnums: string[];
};

export type ConvertedJoinpoint = {
  name: string;
  originalName: string;
  tooltip?: string;
  extends?: string;
  attributes: ConvertedAttribute[];
  actions: ConvertedAction[];
  defaultAttribute?: string;
};

export type ConvertedAttribute = {
  name: string;
  type: string;
  tooltip?: string;
};

export type ConvertedAction = {
  name: string;
  tooltip?: string;
  returnType: string;
  parameters: ConvertedParameter[];
  overloads: ConvertedAction[];
};

export type ConvertedParameter = {
  name: string;
  type: string;
  default: string;
};

export type ConvertedEnum = {
  name: string;
  extends?: string;
  entries: string[];
};

export function convertSpecification(input: JSON_LanguageSpecification, baseJoinPointSpec?: ConvertedSpecification | undefined): ConvertedSpecification {
  const typeNameSet = new Set<string>();
  const joinpointNameSet = new Set<string>();
  const unorderedJoinpoints: JSON_JoinpointSpecification[] = [];
  const enumNameSet = new Set<string>();
  const unorderedEnums: JSON_EnumSpecification[] = [];

  input.children.forEach((child) => {
    typeNameSet.add(child.type);
    if (child.type === "joinpoint") {
      joinpointNameSet.add(child.name);
      unorderedJoinpoints.push(child);
    }
    if (child.type === "enum") {
      enumNameSet.add(child.name);
      unorderedEnums.push(child);
    }
  });

  const joinpoints = orderJoinpoints(unorderedJoinpoints);
  const enums = orderJoinpoints(unorderedEnums);

  const output: ConvertedSpecification = {
    joinpoints: convertJoinpoints(joinpoints, joinpointNameSet, enumNameSet),
    enums: convertEnums(enums),
    importEnums: Array.isArray(input.importEnums) ? [...input.importEnums] : [],
  };

  if (baseJoinPointSpec !== undefined) {
    output.joinpoints[0].extends = baseJoinPointSpec.joinpoints[0].name;
  }

  deduplicateJoinpoints(output.joinpoints, baseJoinPointSpec);

  return output;
}

function orderJoinpoints<T extends JSON_JoinpointSpecification | JSON_EnumSpecification>(unorderedJoinpoints: T[]): T[] {
  const orderedNameSet = new Set<string>();
  const blockedJpSet = new Set<T>();
  const joinpoints: T[] = [];

  unorderedJoinpoints.forEach((jp) => {
    if (jp.extends) {
      if (orderedNameSet.has(jp.extends)) {
        joinpoints.push(jp);
        orderedNameSet.add(jp.name);
      } else {
        blockedJpSet.add(jp);
      }
    } else {
      joinpoints.push(jp);
      orderedNameSet.add(jp.name);
    }
  });
  while (blockedJpSet.size > 0) {
    blockedJpSet.forEach((jp) => {
      if (jp.extends && orderedNameSet.has(jp.extends)) {
        joinpoints.push(jp);
        orderedNameSet.add(jp.name);
        blockedJpSet.delete(jp);
      }
    });
  }

  return joinpoints;
}

function convertJoinpoints(joinpoints: JSON_JoinpointSpecification[], joinpointNameSet: Set<string>, enumNameSet: Set<string>) {
  const convertedJoinpoints: ConvertedJoinpoint[] = [];

  joinpoints.forEach((jp) => {
    convertedJoinpoints.push(
      convertJoinpoint(jp, joinpointNameSet, enumNameSet)
    );
  });

  return convertedJoinpoints;
}

function convertJoinpoint(jp: JSON_JoinpointSpecification, joinpointNameSet: Set<string>, enumNameSet: Set<string>): ConvertedJoinpoint {
  const attributes: ConvertedAttribute[] = [];
  const actions: ConvertedAction[] = [];
  const actionNameSet = new Set<string>();

  jp.children?.forEach((child) => {
    switch (child.type) {
      case "attribute":
        if (child.children.length !== 1) {
          convertJoinpointAction(
            child,
            joinpointNameSet,
            enumNameSet,
            actions,
            actionNameSet
          );
        } else {
          attributes.push(
            convertJoinpointAttribute(child, joinpointNameSet, enumNameSet)
          );
        }
        break;
      case "action":
        convertJoinpointAction(
          child,
          joinpointNameSet,
          enumNameSet,
          actions,
          actionNameSet
        );
        break;
      default:
        console.log("Unknown child type:", (child as { type: string }).type);
    }
  });

  attributes.sort((left, right) => left.name.localeCompare(right.name));
  actions.sort((left, right) => left.name.localeCompare(right.name));

  const jpName = interpretType(jp.name, joinpointNameSet, enumNameSet);
  return {
    name: jpName,
    originalName: jp.name,
    tooltip: convertDeprecationNotice(jp.tooltip),
    extends: jp.extends
      ? interpretType(jp.extends, joinpointNameSet, enumNameSet)
      : undefined,
    attributes: attributes,
    actions: actions,
    defaultAttribute: jp.defaultAttr,
  };
}

function convertDeprecationNotice(notice: string | undefined): string | undefined {
  if (notice?.includes("DEPRECATED")) {
    notice = notice.replace("[DEPRECATED:", "@deprecated");
    notice = notice.replace("DEPRECATED:", "@deprecated");

    const splitTooltip = notice.split("]");

    notice = splitTooltip.slice(1).join("]") + "\n\n" + splitTooltip[0];
  }
  return capitalizeFirstLetter(notice)?.trim();
}

function convertJoinpointAttribute(
  attributeObject: JSON_JoinpointMemberSpecification,
  joinpointNameSet: Set<string>,
  enumNameSet: Set<string>
): ConvertedAttribute {
  const attribute = attributeObject.children[0];

  return {
    name: attribute.name,
    type: interpretType(attribute.type, joinpointNameSet, enumNameSet),
    tooltip: convertDeprecationNotice(attributeObject.tooltip),
  };
}

function convertJoinpointActionParameter(
  parameterObject: JSON_ParameterSpecification,
  joinpointNameSet: Set<string>,
  enumNameSet: Set<string>
): ConvertedParameter {
  const type = interpretType(
    parameterObject.type,
    joinpointNameSet,
    enumNameSet
  );
  let parameterName = parameterObject.name;
  switch (parameterName) {
    case "function":
      parameterName = "func";
      break;
    case "else":
      parameterName = "elseStatement";
  }

  let defaultValue: string | undefined = parameterObject.defaultValue;
  if (defaultValue === "") {
    defaultValue = undefined;
  }

  return {
    name: parameterName,
    type: type,
    default: JSON.stringify(defaultValue),
  };
}

function convertJoinpointAction(
  actionObject: JSON_JoinpointMemberSpecification,
  joinpointNameSet: Set<string>,
  enumNameSet: Set<string>,
  actions: ConvertedAction[],
  actionNameSet: Set<string>,
  overrideName: string | null = null
): void {
  const action = actionObject.children[0];
  const actionName = overrideName ?? action.name;

  const convertedAction = {
    name: actionName,
    tooltip: convertDeprecationNotice(actionObject.tooltip),
    returnType: interpretType(action.type, joinpointNameSet, enumNameSet),
    parameters: actionObject.children.slice(1).map((parameter) => {
      return convertJoinpointActionParameter(
        parameter,
        joinpointNameSet,
        enumNameSet
      );
    }),
    overloads: [],
  };

  if (actionNameSet.has(convertedAction.name)) {
    for (const action of actions) {
      if (action.name === convertedAction.name) {
        if (action.overloads.length === 0) {
          action.overloads.push(structuredClone(action));

          let paramCounter = 1;
          action.parameters.forEach((param) => {
            param.name = `p${paramCounter++}`;

            if (param.default !== undefined) {
              param.default = '"null"';
            }
          });
        }

        action.returnType += " | " + convertedAction.returnType;

        for (let i = 0; i < convertedAction.parameters.length; i++) {
          if (i >= action.parameters.length) {
            action.parameters.push(
              structuredClone(convertedAction.parameters[i])
            );
            action.parameters[i].name = `p${i + 1}`;

            action.parameters[i].default = '"null"';

            continue;
          }

          const parameter = convertedAction.parameters[i];
          const existingParameter = action.parameters[i];
          if (parameter.type !== existingParameter.type) {
            existingParameter.type += " | " + parameter.type;
          }
        }

        if (convertedAction.parameters.length < action.parameters.length) {
          for (
            let i = convertedAction.parameters.length;
            i < action.parameters.length;
            i++
          ) {
            action.parameters[i].default = '"null"';
          }
        }

        action.overloads.push(convertedAction);
        action.returnType = [...new Set(action.returnType.split(" | "))].join(
          " | "
        );
        for (const i in action.parameters) {
          action.parameters[i].type = [
            ...new Set(action.parameters[i].type.split(" | ")),
          ].join(" | ");
        }
        break;
      }
    }
    return;
  }
  actionNameSet.add(actionName);

  actions.push(convertedAction);
}

function convertEnums(enums: JSON_EnumSpecification[]) {
  return enums.map((enumObj) => convertEnum(enumObj));
}

function convertEnum(e: JSON_EnumSpecification): ConvertedEnum {
  return {
    name: e.name,
    extends: e.extends,
    entries: e.children.map((child) => {
      return child.value;
    }),
  };
}

/**
 * Splits `text` on `separator` occurrences that are not nested inside <> or [].
 * @param text
 * @param separator
 * @returns
 */
function splitTopLevel(text: string, separator: string): string[] {
  const parts = [];
  let depth = 0;
  let current = "";
  for (const char of text) {
    if (char === "<" || char === "[") {
      depth++;
    } else if (char === ">" || char === "]") {
      depth--;
    }
    if (char === separator && depth === 0) {
      parts.push(current);
      current = "";
    } else {
      current += char;
    }
  }
  parts.push(current);
  return parts;
}

export function capitalizeFirstLetter(string: string): string;
export function capitalizeFirstLetter(string: undefined): undefined;
export function capitalizeFirstLetter(string: string | undefined): string | undefined;
export function capitalizeFirstLetter(string: string | undefined): string | undefined {
  if (!string) return string;
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function interpretType(typeString: string, joinpointNameSet: Set<string>, enumNameSet: Set<string>): string {
  if (typeString === "?") {
    return "any";
  }

  // Detect array types
  if (typeString.endsWith("[]")) {
    const baseType = typeString.slice(0, -2);
    return `${interpretType(baseType, joinpointNameSet, enumNameSet)}[]`;
  } else if (typeString.startsWith("[")) {
    // Example: [abc | asd] to "abc" | "asd"
    const literals = typeString
      .slice(1, -1)
      .split("|")
      .map((literal) => `"${literal.trim()}"`)
      .join(" | ");
    return literals;
  }

  if ((typeString.startsWith("Map<") || typeString.startsWith("map<")) && typeString.endsWith(">")) {
    const innerTypes = splitTopLevel(typeString.slice(4, -1), ",").map((t) => t.trim());
    if (innerTypes.length === 2) {
      const keyType = interpretType(innerTypes[0], joinpointNameSet, enumNameSet);
      const valueType = interpretType(innerTypes[1], joinpointNameSet, enumNameSet);
      return `Record<${keyType}, ${valueType}>`;
    } else {
      return "Record<string, any>";
    }
  }

  if (joinpointNameSet.has(typeString) || enumNameSet.has(typeString)) {
    const jpType = capitalizeFirstLetter(typeString);

    switch (jpType) {
      case "Function":
        return "FunctionJp";
      case "File":
        return "FileJp";
      case "Record":
        return "RecordJp";
    }

    return jpType;
  }

  switch (typeString) {
    case "Integer":
    case "int":
    case "Long":
    case "long":
    case "Double":
    case "double":
      return "number";
    case "map<?, ?>":
    case "Map<?, ?>":
    case "map":
    case "Map":
      return "Record<string, any>";
    default:
      return typeString.toLowerCase();
  }
}

function deduplicateJoinpoints(joinpoints: ConvertedJoinpoint[], baseJoinPointSpec?: ConvertedSpecification | undefined) {
  for (const joinpoint of joinpoints) {
    // Find the parent joinpoint
    let parentJoinpoint = joinpoints.find(
      (jp) => jp.name === joinpoint.extends
    );
    if (parentJoinpoint === undefined && baseJoinPointSpec !== undefined) {
      parentJoinpoint = baseJoinPointSpec.joinpoints.find(
        (jp) => jp.name === joinpoint.extends
      );
    }

    while (parentJoinpoint) {
      for (let attributeIndex = 0; attributeIndex < joinpoint.attributes.length; attributeIndex++) {
        for (const parentAttribute of parentJoinpoint.attributes) {
          if (
            JSON.stringify(joinpoint.attributes[attributeIndex]) ===
            JSON.stringify(parentAttribute)
          ) {
            joinpoint.attributes.splice(attributeIndex, 1);
          }
        }
      }

      for (let actionIndex = 0; actionIndex < joinpoint.actions.length; actionIndex++) {
        for (const parentAction of parentJoinpoint.actions) {
          if (
            JSON.stringify(joinpoint.actions[actionIndex]) ===
            JSON.stringify(parentAction)
          ) {
            joinpoint.actions.splice(actionIndex, 1);
          }
        }
      }

      parentJoinpoint = joinpoints.find(
        (jp) => jp.name === parentJoinpoint!.extends
      );
    }
  }
}
