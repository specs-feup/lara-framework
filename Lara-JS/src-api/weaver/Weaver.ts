import { LaraJoinPoint, wrapJoinPoint } from "../LaraJoinPoint.js";
import JavaInterop from "../lara/JavaInterop.js";
import Strings from "../lara/Strings.js";
import JavaTypes from "../lara/util/JavaTypes.js";
import PrintOnce from "../lara/util/PrintOnce.js";
import WeaverDataStore from "./util/WeaverDataStore.js";

/**
 * Contains utility methods related to the weaver.
 */
export default class Weaver {
  /**
   * If defined, sets the default weaver command.
   */
  static DEFAULT_WEAVER_COMMAND = undefined;

  /**
   *
   * @returns {J#org.lara.interpreter.weaver.interf.WeaverEngine} the Java instance of the current WeaverEngine
   */
  static getWeaverEngine() {
    return JavaTypes.WeaverEngine.getThreadLocalWeaver();
  }

  static getLaraLoc() {
    return JavaTypes.LaraIUtils.getLaraLoc(
      Weaver.getWeaverEngine(),
      JavaTypes.LaraI.getThreadLocalData()
    );
  }

  static getLaraLocTotals() {
    var laraLoc = Java.type("pt.up.fe.specs.lara.loc.LaraLoc");
    return Java.type("org.lara.interpreter.utils.LaraIUtils")
      .getLaraLoc(
        Weaver.getWeaverEngine(),
        JavaTypes.LaraI.getThreadLocalData()
      )
      .get(laraLoc.getTotalsKey());
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
  static getDefaultAttribute(joinPointType: string): string {
    return Weaver.getWeaverEngine().getDefaultAttribute(joinPointType);
  }

  /**
   * @param jpTypeName - a join point, or the name of a join point
   * @param attributeName - the name of the attribute to check
   *
   * @returns True, if the given join point or join point name support the attribute with the given name
   *
   * @deprecated The typescript compiler will tell you this
   */
  static hasAttribute(jpTypeName: string, attributeName: string): boolean {
    const joinPoint: any = Weaver.getWeaverEngine()
      .getLanguageSpecificationV2()
      .getJoinPoint(jpTypeName);

    if (joinPoint === null) {
      return false;
    }

    return !joinPoint.getAttribute(attributeName).isEmpty();
  }

  /**
   * Converts a given join point to a string.
   *
   * @param $jp - The join point to serialize.
   *
   * @returns A string representation of the join point.
   */
  static serialize($jp: LaraJoinPoint): string {
    if (JavaTypes.SpecsSystem.getJavaVersionNumber() > 16) {
      PrintOnce.message(
        "Weaver.serialize: Java version 17 or higher detected, XML serialization of AST might not work"
      );
    }

    return Strings.toXml($jp.node);
  }

  /**
   * Converts a serialized join point back to an object.
   *
   * @param string - The serialized join point.
   *
   * @returns The deserialized join point.
   */
  static deserialize(string: string): LaraJoinPoint {
    if (JavaTypes.SpecsSystem.getJavaVersionNumber() > 16) {
      PrintOnce.message(
        "Weaver.deserialize: Java version 17 or higher detected, XML serialization of AST might not work"
      );
    }

    return wrapJoinPoint(
      Weaver.AST_METHODS.toJavaJoinPoint(Strings.fromXml(string))
    );
  }

  /**
   * An instance of the basic interface that the AST nodes must support.
   */
  static AST_METHODS = Weaver.getWeaverEngine().getAstMethods();

  /**
   * Adapts a Java object to JavaScript. Currently converts:
   *
   * - Null to undefined;
   * - Java array to JS array;
   * - List to array;
   *
   */
  static toJs(javaObject: any) {
    return Weaver.getWeaverEngine().getScriptEngine().toJs(javaObject);
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

    // WeaverOptions has a function for this, but imports Weaver
    const weaverData = new WeaverDataStore(
      JavaTypes.LaraI.getThreadLocalData()
    );

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
}