#!/usr/bin/env node

export function convertSpecification(input) {
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

  deduplicateJoinpoints(output.joinpoints);

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

  jp.children.forEach((child) => {
    switch (child.type) {
      case "attribute":
        if (child.children.length !== 1) {
          actions.push(
            convertJoinpointAction(
              child,
              joinpointNameSet,
              enumNameSet,
              "get" + capitalizeFirstLetter(child.children[0].name)
            )
          );
        } else {
          attributes.push(
            convertJoinpointAttribute(child, joinpointNameSet, enumNameSet)
          );
        }
        break;
      case "action":
        actions.push(
          convertJoinpointAction(child, joinpointNameSet, enumNameSet)
        );
        break;
      case "select":
        // Do nothing
        break;
      default:
        console.log("Unknown child type:", child.type);
    }
  });

  const jpName = capitalizeFirstLetter(jp.name);
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

  return {
    name: parameterName,
    type: type,
    default: JSON.stringify(parameterObject.defaultValue),
  };
}

function convertJoinpointAction(
  actionObject,
  joinpointNameSet,
  enumNameSet,
  overrideName = null
) {
  const action = actionObject.children[0];
  const actionName = overrideName ?? action.name;

  return {
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
  };
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
    return capitalizeFirstLetter(typeString);
  }

  switch (typeString) {
    case "Integer":
    case "int":
    case "Long":
    case "Double":
      return "number";
      break;
    case "Map":
      return "Map<string, any>";
    default:
      return typeString.toLowerCase();
      break;
  }
}

function deduplicateJoinpoints(joinpoints) {
  for (const joinpoint of joinpoints) {
    // Find the parent joinpoint
    let parentJoinpoint = joinpoints.find(
      (jp) => jp.name === joinpoint.extends
    );

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
