import JavaTypes from "../../lara/util/JavaTypes.js";
import { println } from "../../core/output.js";
import Check from "../../lara/Check.js";
import PrintOnce from "../../lara/util/PrintOnce.js";
import { Strings } from "../../lara/Strings.js";
import WeaverDataStore from "./WeaverDataStore.js";
import JavaInterop from "../../lara/JavaInterop.js";

/**
 * Contains utility methods related to the weaver.
 * @class
 */
export default class Weaver {
    /**
    * If defined, sets the default weaver command.
    */
    static DEFAULT_WEAVER_COMMAND = undefined;

    /**
    *
    * @return {J#org.lara.interpreter.weaver.interf.WeaverEngine} the Java instance of the current WeaverEngine
    */
    static getWeaverEngine() {
        return JavaTypes.getJavaWeaverEngine().getThreadLocalWeaver();

    }

  static getLaraLoc() {
    return JavaTypes.getLaraIUtils().getLaraLoc(
      Weaver.getWeaverEngine(),
      JavaTypes.getJavaLaraI().getThreadLocalData()
    );
  }

  static getLaraLocTotals() {
    var laraLoc = Java.type("pt.up.fe.specs.lara.loc.LaraLoc");
    return Java.type("org.lara.interpreter.utils.LaraIUtils")
      .getLaraLoc(Weaver.getWeaverEngine(), JavaTypes.getJavaLaraI().getThreadLocalData())
      .get(laraLoc.getTotalsKey());
  }

  static writeCode(outputFolder: any) {
    if (outputFolder === undefined) {
      println("Weaver.writeCode: Output folder not defined");
      return;
    }

    Weaver.getWeaverEngine().writeCode(outputFolder);
  }

  /**
   * @param $joinpoint
   * @param {String} type
   */
  static isJoinPoint($joinpoint: any, type: string) {                                 
    return Check.isJoinPoint($joinpoint, type);       
  }                        
    /*
	var isJoinPoint = Java.type("org.lara.interpreter.weaver.interf.JoinPoint").isJoinPoint($joinpoint);

	if(type === undefined) {
		return isJoinPoint;
	}

	if(!isJoinPoint) {
		throw "Weaver.isJoinPoint: Asking if object is of join point '"+type+"', but object is not a join point";
	}
	
	return $joinpoint.instanceOf(type);
	*/


  /**
   * @param {String} joinPointType the type of the join point
   * @return {String} the name of the default attribute for the given join point type, or undefined if there is no default attribute
   */
  static getDefaultAttribute(joinPointType: String) {
    return Weaver.getWeaverEngine().getDefaultAttribute(joinPointType) as String;
  }

  /**
   * @para {String|$jp} jp - a join point, or the name of a join point
   * @para {String} attributeName - the name of the attribute to check
   * @deprecated Use $jp.attributes instead
   *
   * @return {boolean} true, if the given join point or join point name support the attribute with the given name
   */
  static hasAttribute(jp: any, attributeName: string) {
    println("DEPRECATED Weaver.hasAttribute, use $jp.attributes");
    
    var jpType = JavaTypes.getJavaJoinPoint()().isJoinPoint(jp) ? jp.joinPointType : jp.toString();

    var joinPoint = Weaver.getWeaverEngine()
      .getLanguageSpecificationV2()
      .getJoinPoint(jpType);

    if (joinPoint === null) {
      return false;
    }

    //return joinPoint.getAttribute(attributeName) !== null;
    return !joinPoint.getAttribute(attributeName).isEmpty();
  }

  /**
   * Converts a given join point to a string.
   *
   * @param {Object} object - The join point to serialize.
   *
   * @return {String} A string representation of the join point.
   */
  static serialize($jp: any) {
    Check.isJoinPoint($jp);

    if (JavaTypes.getJavaSpecsSystem().getJavaVersionNumber() > 16) {
      PrintOnce.message(
        "Weaver.serialize: Java version 17 or higher detected, XML serialization of AST might not work"
      );
    }

    return Strings.toXml($jp.node);
  }

  /**
   * Converts a serialized join point back to an object.
   *
   * @param {String} string - The serialized join point.
   *
   * @return {$jp} The deserialized join point.
   */
  static deserialize(string: string) {
    if (JavaTypes.getJavaSpecsSystem().getJavaVersionNumber() > 16) {
      PrintOnce.message(
        "Weaver.deserialize: Java version 17 or higher detected, XML serialization of AST might not work"
      );
    }

    return Weaver.AST_METHODS.toJavaJoinPoint(Strings.fromXml(string));
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
   * @return {String} the name of the currently executing LARA compiler.
   */
  static getName() {
    return Weaver.getWeaverEngine().getName();
  }

  /**
   * Launches several weaving sessions in parallel.
   *
   * @param {string[][]} argsLists - An array where each element is an array with the arguments to pass to the weaver, as if it was launched from the command-line
   * @param {int} [threads = -1] -  Number of threads to use
   * @param {string[]|string} [weaverCommand = []] -  The command we should use to call the weaver (e.g., /usr/local/bin/clava)
   *
   * @return {Object[]} a list with the results of each of the executions. The executing script must use weaver.Script to set the output (i.e. Script.setOutput())
   */
  static runParallel(argsLists: Array<Array<string>>, threads: number, weaverCommand: Array<string>|string) {
    if (threads === undefined) {
      threads = -1;
    }

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
    const weaverData = new WeaverDataStore(JavaTypes.getJavaLaraI().getThreadLocalData());

    const WeaverLauncher = JavaTypes.getWeaverLauncher();
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
