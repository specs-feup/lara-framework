import { LaraJoinPoint } from "../../../LaraJoinPoint.js";
import Pass from "../Pass.js";
import PassTransformationError from "../PassTransformationError.js";
import PassResult from "./PassResult.js";

export default class AggregatePassResult extends PassResult {
  /**
   * Intermediate results collected from the pass
   *
   */
  private _intermediateResults: Readonly<PassResult>[] = [];

  /**
   * Errors thrown where the pass transformation failed
   *
   */
  private _transformationErrors: Readonly<PassTransformationError>[] = [];

  private _casesApplied = 0;

  /**
   * @param pass - Pass which generated this result
   * @param $jp - Join point related to this pass result
   */
  constructor(pass: Pass, $jp: LaraJoinPoint) {
    super(pass, $jp, { appliedPass: false, insertedLiteralCode: false });
  }

  /**
   * @returns True if the pass was applied successfully at least once
   */
  get appliedPass(): boolean {
    return this._casesApplied > 0;
  }

  /**
   * @returns Total number of cases where this pass applied, independently of its success
   */
  get casesFound(): number {
    return this.casesFailed + this._intermediateResults.length;
  }

  /**
   * @returns Number of cases where this pass was successfully applied
   */
  get casesApplied(): number {
    return this._casesApplied;
  }

  /**
   * @returns Number of cases that resulted in an error
   */
  get casesFailed(): number {
    return this._transformationErrors.length;
  }

  /**
   * @returns List of results registered during the pass
   */
  get results(): Readonly<PassResult>[] {
    return this._intermediateResults;
  }

  /**
   * @returns List of errors registered during the pass
   */
  get errors(): Readonly<PassTransformationError>[] {
    return this._transformationErrors;
  }

  /**
   * Register a new error
   *
   */
  pushError(error: PassTransformationError) {
    this._transformationErrors.push(Object.freeze(error));
  }

  /**
   * Register a new partial result
   * @param result - PassResult from applying a predicate to a joinpoint
   */
  pushResult(result: PassResult) {
    this._intermediateResults.push(Object.freeze(result));
    if (result.appliedPass) {
      this._appliedPass = true;
      this._casesApplied += 1;
    }
    this._insertedLiteralCode =
      this._insertedLiteralCode || result.insertedLiteralCode;
  }

  toString(): string {
    return (
      `PassResult { name: ${this.name}; ` +
      `appliedPass: ${this.appliedPass}; ` +
      `insertedLiteralCode: ${this.insertedLiteralCode}; ` +
      `casesFound: ${this.casesFound}; ` +
      `casesApplied: ${this.casesApplied}; ` +
      `casesFailed: ${this.casesFailed} }`
    );
  }
}
