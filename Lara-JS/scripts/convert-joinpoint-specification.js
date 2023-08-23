#!/usr/bin/env node

export function convertSpecification(input, baseJoinPointSpec = undefined) {
  let typeNameSet = new Set();
  let joinpointNameSet = new Set();
  let unorderedJoinpoints = [];
  let enumNameSet = new Set();
  let unorderedEnums = [];

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

  let joinpoints = orderJoinpoints(unorderedJoinpoints);
  let enums = orderJoinpoints(unorderedEnums);

  let output = {
    joinpoints: convertJoinpoints(joinpoints, joinpointNameSet, enumNameSet),
    enums: convertEnums(enums),
  };

  if (baseJoinPointSpec !== undefined) {
    output.joinpoints[0].extends = baseJoinPointSpec.joinpoints[0].name;
  }

  deduplicateJoinpoints(output.joinpoints, baseJoinPointSpec);

  return output;
}

function orderJoinpoints(unorderedJoinpoints) {
  let orderedNameSet = new Set();
  let blockedJpSet = new Set();
  let joinpoints = [];

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
      if (orderedNameSet.has(jp.extends)) {
        joinpoints.push(jp);
        orderedNameSet.add(jp.name);
        blockedJpSet.delete(jp);
      }
    });
  }

  return joinpoints;
}

function convertJoinpoints(joinpoints, joinpointNameSet, enumNameSet) {
  let convertedJoinpoints = [];

  joinpoints.forEach((jp) => {
    convertedJoinpoints.push(
      convertJoinpoint(jp, joinpointNameSet, enumNameSet)
    );
  });

  return convertedJoinpoints;
}

function convertJoinpoint(jp, joinpointNameSet, enumNameSet) {
  let attributes = [];
  let actions = [];
  const actionNameSet = new Set();

  jp.children.forEach((child) => {
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
      case "select":
        // Do nothing
        break;
      default:
        console.log("Unknown child type:", child.type);
    }
  });

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
  };
}

/**
 *
 * @param {string} notice
 * @returns
 */
function convertDeprecationNotice(notice) {
  if (notice?.includes("DEPRECATED")) {
    notice = notice.replace("[DEPRECATED:", "@deprecated");
    notice = notice.replace("DEPRECATED:", "@deprecated");

    let splitTooltip = notice.split("]");

    notice = splitTooltip.slice(1).join("]") + "\n\n" + splitTooltip[0];
  }
  return capitalizeFirstLetter(notice)?.trim();
}

function convertJoinpointAttribute(
  attributeObject,
  joinpointNameSet,
  enumNameSet
) {
  const attribute = attributeObject.children[0];

  return {
    name: attribute.name,
    type: interpretType(attribute.type, joinpointNameSet, enumNameSet),
    tooltip: convertDeprecationNotice(attributeObject.tooltip),
  };
}

function convertJoinpointActionParameter(
  parameterObject,
  joinpointNameSet,
  enumNameSet
) {
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

  let defaultValue = parameterObject.defaultValue;
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
  actionObject,
  joinpointNameSet,
  enumNameSet,
  actions,
  actionNameSet,
  overrideName = null
) {
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
        break;
      }
    }
    return;
  }
  actionNameSet.add(actionName);

  actions.push(convertedAction);
}

function convertEnums(enums) {
  let convertedEnums = [];

  enums.forEach((enumObj) => {
    convertedEnums.push(convertEnum(enumObj));
  });

  return convertedEnums;
}

function convertEnum(e) {
  return {
    name: e.name,
    extends: e.extends,
    entries: e.children.map((child) => {
      return child.value;
    }),
  };
}

export function capitalizeFirstLetter(string) {
  if (!string) return string;
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function interpretType(typeString, joinpointNameSet, enumNameSet) {
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
    case "Double":
      return "number";
      break;
    case "Map":
      return "Record<string, any>";
    default:
      return typeString.toLowerCase();
      break;
  }
}

function deduplicateJoinpoints(joinpoints, baseJoinPointSpec = undefined) {
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
      for (const attributeIndex in joinpoint.attributes) {
        for (const parentAttribute of parentJoinpoint.attributes) {
          if (
            JSON.stringify(joinpoint.attributes[attributeIndex]) ===
            JSON.stringify(parentAttribute)
          ) {
            joinpoint.attributes.splice(attributeIndex, 1);
          }
        }
      }

      for (const actionIndex in joinpoint.actions) {
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
        (jp) => jp.name === parentJoinpoint.extends
      );
    }
  }
}
