import { LaraJoinPoint } from "../../../LaraJoinPoint.js";
import Pass from "../Pass.js";

/**
 * The result of a Lara transformation pass
 *
 */
class PassResult {
  /**
   * Name of the pass that generated this result
   *
   */
  protected _name: string;
  /**
   * Joinpoint where this pass was applied
   *
   */
  protected _$jp: LaraJoinPoint;
  /**
   * True if the pass was applied to the given node, false if the node was ignored or there were no changes
   *
   */
  protected _appliedPass: boolean;
  /**
   * True if the pass inserted literal code
   *
   */
  protected _insertedLiteralCode: boolean;

  /**
   * @param pass - Pass which generated this result
   * @param $jp - Join point related to this pass result
   * @param params - Properties of a defined PassResult
   */
  constructor(
    pass: Pass,
    $jp: LaraJoinPoint,
    params: PassResult.PassResultParams = {
      appliedPass: true,
      insertedLiteralCode: false,
    }
  ) {
    this._name = pass.name;
    this._$jp = $jp;
    this._appliedPass = params.appliedPass;
    this._insertedLiteralCode = params.insertedLiteralCode;
  }

  /**
   * @returns Name of the pass that generated this result
   */
  get name(): string {
    return this._name;
  }

  /**
   * @returns Joinpoint where this pass was applied
   */
  get jp(): LaraJoinPoint {
    return this._$jp;
  }

  /**
   * @returns True if the pass was applied to the given node, false if the node was ignored or there were no changes
   */
  get appliedPass(): boolean {
    return this._appliedPass;
  }

  /**
   * @returns True if the pass inserted literal code
   */
  get insertedLiteralCode(): boolean {
    return this._insertedLiteralCode;
  }

  toString(): string {
    return (
      `PassResult { name: ${this.name}; ` +
      `appliedPass: ${this.appliedPass}; ` +
      `insertedLiteralCode: ${this.insertedLiteralCode} }`
    );
  }
}

// eslint-disable-next-line @typescript-eslint/no-namespace
namespace PassResult {
  export interface PassResultParams {
    /**
     * True if the pass was applied to the given node, false if the node was ignored or there were no changes
     */
    appliedPass: boolean;
    /**
     * True if the pass inserted literal code
     */
    insertedLiteralCode: boolean;
  }
}

export default PassResult;
