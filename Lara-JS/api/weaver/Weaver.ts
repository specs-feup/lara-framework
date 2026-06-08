import {
  LaraJoinPoint,
  getJoinpointMappers,
} from "../LaraJoinPoint.ts";
import Io from "../lara/Io.ts";
import JavaInterop from "../lara/JavaInterop.ts";
import DataStore from "../lara/util/DataStore.ts";
import JavaTypes, { type JavaClasses } from "../lara/util/JavaTypes.ts";
import WeaverOptions from "./WeaverOptions.ts";

/**
 * @internal Lara Common Language dirty hack. IMPROPER USAGE WILL BREAK THE WHOLE WEAVER!
 */
let usingLaraCommonLanguage = false;

/**
 * @internal Lara Common Language dirty hack. IMPROPER USAGE WILL BREAK THE WHOLE WEAVER!
 */
export function setUsingLaraCommonLanguage(value: boolean = false) {
  usingLaraCommonLanguage = value;
}

/**
 * Contains utility methods related to the weaver.
 */
export default class Weaver {
  /**
   * If defined, sets the default weaver command.
   */
  static DEFAULT_WEAVER_COMMAND = undefined;

  /**
   * @returns The Java instance of the current WeaverEngine
   */
  static getWeaverEngine(): JavaClasses.WeaverEngine {
    return (globalThis as any).__hidden.javaWeaver;
  }

  static writeCode(outputFolder: any) {
    if (outputFolder === undefined) {
      console.log("Weaver.writeCode: Output folder not defined");
      return;
    }

    Weaver.getWeaverEngine().writeCode(outputFolder);
  }

  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isJoinPoint($joinpoint: LaraJoinPoint): boolean;
  /**
   * @deprecated Use the javascript `instanceof` operator instead
   */
  static isJoinPoint($joinpoint: LaraJoinPoint, type?: string): boolean {
    if (type === undefined) {
      return $joinpoint instanceof LaraJoinPoint;
    }
    return $joinpoint.instanceOf(type);
  }

  /**
   * @param joinPointType - The type of the join point
   * @returns The name of the default attribute for the given join point type, or undefined if there is no default attribute
   */
  static getDefaultAttribute<T extends typeof LaraJoinPoint>(
    joinPointType: T
  ): keyof T | null;
  static getDefaultAttribute(joinPointType: string): string | null;
  static getDefaultAttribute<T extends typeof LaraJoinPoint>(
    joinPointType: T | string
  ): keyof T | string | null;
  static getDefaultAttribute<T extends typeof LaraJoinPoint>(
    joinPointType: T | string
  ): keyof T | string | null {
    if (usingLaraCommonLanguage === true) {
      return JavaTypes.getType(
        "pt.up.fe.specs.lara.commonlang.LaraCommonLang"
      ).getDefaultAttribute(joinPointType) as string | null;
    }

    if (typeof joinPointType === "string") {
      // Search for the default attribute in the joinpoint mappers
      for (const mapper of getJoinpointMappers()) {
        const jpClass = mapper.toJpClass(joinPointType);
        if (jpClass) {
          return jpClass._defaultAttributeInfo.name;
        }
      }

      // No wrapper was found, attempt to the collect information from the weaver
      return Weaver.getWeaverEngine().getDefaultAttribute(joinPointType);
    } else {
      return joinPointType._defaultAttributeInfo.name;
    }
  }

  /**
   * Finds the name of the joinpoint class, given the js wrapper class itself
   * @param type - The joinpoint class to find the name of
   * @returns The name of the joinpoint class
   */
  static findJoinpointTypeName<T extends typeof LaraJoinPoint>(
    type: T
  ): string | undefined {
    const joinpointMappers = getJoinpointMappers();

    for (const mapper of joinpointMappers) {
      const match = mapper.fromJpClass(type);
      if (match) {
        return match;
      }
    }

    return undefined;
  }

  static findJoinpointType(name: string): typeof LaraJoinPoint | undefined {
    const joinpointMappers = getJoinpointMappers();

    for (const mapper of joinpointMappers) {
      const jpClass = mapper.toJpClass(name);
      if (jpClass) {
        return jpClass;
      }
    }

    return undefined;
  }

  /**
   * @deprecated Should not be used. Here for a short period to allow for code migration.
   * 
   * Adapts a Java object to JavaScript. Currently converts:
   *
   * - ~Null to undefined~;
   * - ~Java array to JS array~;
   * - Java List to array;
   *
   */
  static toJs<T>(javaObject: JavaClasses.List<T> | T[]): T[] {
    // if is list call toArray
    if (JavaTypes.instanceOf(javaObject, "java.util.List")) {
      // Convert Java List to JS array
      return (javaObject as JavaClasses.List<T>).toArray();
    }
    return javaObject as T[];
  }

  /**
   * @returns The name of the currently executing LARA compiler.
   */
  static getName(): string {
    return Weaver.getWeaverEngine().getName();
  }

  /**
   * Launches several weaving sessions in parallel.
   *
   * @param argsLists - An array where each element is an array with the arguments to pass to the weaver, as if it was launched from the command-line
   * @param threads - Number of threads to use
   * @param weaverCommand - The command we should use to call the weaver (e.g., /usr/local/bin/clava)
   *
   * @returns A list with the results of each of the executions. The executing script must use weaver.Script to set the output (i.e. Script.setOutput())
   */
  static runParallel(
    argsLists: string[][],
    threads = -1,
    weaverCommand: string | string[] = []
  ) {
    if (weaverCommand === undefined) {
      weaverCommand = [];

      if (Weaver.DEFAULT_WEAVER_COMMAND !== undefined) {
        weaverCommand.push(Weaver.DEFAULT_WEAVER_COMMAND);
      }
    }

    if (!(weaverCommand instanceof Array)) {
      weaverCommand = [weaverCommand.toString()];
    }

    // Assures all elements in the argsLists are String
    const safeArgsLists = [];
    for (const argsList of argsLists) {
      safeArgsLists.push(argsList.map((value) => value.toString()));
    }

    const weaverData = WeaverOptions.getData();

    const WeaverLauncher = JavaTypes.WeaverLauncher;
    const jsonStrings = WeaverLauncher.executeParallelStatic(
      safeArgsLists,
      threads,
      JavaInterop.arrayToStringList(weaverCommand),
      weaverData.getContextFolder().getAbsolutePath()
    );

    // Read each json file into its own object
    const results = [];

    for (const jsonString of jsonStrings) {
      results.push(JSON.parse(jsonString));
    }

    return results;
  }

  static get laraArgs(): any {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-argument, @typescript-eslint/no-unsafe-call, @typescript-eslint/no-unsafe-member-access
    const datastore = new DataStore(Weaver.getWeaverEngine().getData().get());
    let jsonString = datastore.get(
      JavaTypes.LaraiKeys.ASPECT_ARGS as string
    ) as string | undefined;

    jsonString ??= "";
    jsonString.trim();

    if (jsonString.endsWith(".json")) {
      return Io.readJson(jsonString);
    }

    // Fix curly braces
    if (!jsonString.startsWith("{")) {
      jsonString = "{" + jsonString;
    }
    if (!jsonString.endsWith("}")) {
      jsonString = jsonString + "}";
    }

    return JSON.parse(jsonString);
  }
}
