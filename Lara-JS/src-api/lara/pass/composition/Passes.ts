import { LaraJoinPoint } from "../../../LaraJoinPoint.js";
import { arrayFromArgs } from "../../core/LaraCore.js";
import Pass from "../Pass.js";
import PassResult from "../results/PassResult.js";

/**
 * @deprecated Use AdapterPass instead
 */
export default class Passes {
  protected _name = "Passes";
  /**
   * Applies a sequence of passes.
   *
   * @param $jp - Apply pass using this join point as the starting point
   * @param args - An array or sequence of:
   * 	1) Pass instance;
   * 	2) Pass class;
   * 	3) function that accepts a $jp;
   * 	4) An array where the first element is 2) or 3), followed by arguments that are passed as arguments of the function or class constructor.
   */
  static apply($jp: LaraJoinPoint, ...args: Pass[]): PassResult[] {
    // Ensure it is an array
    const passesArray = arrayFromArgs(args) as Pass[];

    const results = [];

    for (const pass of passesArray) {
      results.push(pass.apply($jp));
    }

    return results;
  }
}
