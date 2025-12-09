import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import Pass from "./Pass.js";

export default class PassTransformationError extends Error {
  name = "PassTransformationError";

  /**
   * Joinpoint where the transformation was applied and failed.
   * 
   */
  private joinpoint: LaraJoinPoint;

  /**
   * Message describing the error that occurred.
   * 
   */
  private errorDescription: string;

  /**
   * Pass that was being applied when the error was emitted.
   * 
   */
  private compilationPass: Pass;

  constructor(pass: Pass, $joinpoint: LaraJoinPoint, description: string) {
    super(`${pass.name} @ ${$joinpoint.joinPointType}: ${description}`);
    this.errorDescription = description;
    this.joinpoint = $joinpoint;
    this.compilationPass = pass;
  }

  get description() {
    return this.errorDescription;
  }

  get $joinpoint() {
    return this.joinpoint;
  }

  get pass() {
    return this.compilationPass;
  }
}
