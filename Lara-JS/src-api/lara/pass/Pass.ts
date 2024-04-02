import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import Query from "../../weaver/Query.js";
import { debug } from "../core/LaraCore.js";
import PassResult from "./results/PassResult.js";

/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - _apply_impl($jp)
 */
export default abstract class Pass {

  protected abstract _name: string;
  
  /**
   * @returns Name of the pass
   */
  get name(): string {
    return this._name;
  }

  /**
   * Applies this pass starting at the given join point. If no join point is given, uses the root join point
   *
   * @param $jp - Joint point on which the pass will be applied
   * @returns Results of applying this pass to the given joint point
   */
  apply($jp: LaraJoinPoint): PassResult {
    const $actualJp = $jp ?? Query.root();
    debug(() => `Applying pass '${this.name}' to ${$actualJp.joinPointType}`);

    return this._apply_impl($actualJp);
  }

  /**
   * Apply tranformation to
   *
   * @param $jp - Joint point on which the pass will be applied
   * @returns Results of applying this pass to the given joint point
   */
  protected abstract _apply_impl($jp: LaraJoinPoint): PassResult;
}
