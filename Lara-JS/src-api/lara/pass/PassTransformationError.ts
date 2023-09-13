import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import Pass from "./Pass.js";

export default class PassTransformationError extends Error {
  name = "PassTransformationError";

  /**
   * Joinpoint where the transformation was applied and failed.
   * 
   */
  #joinpoint: LaraJoinPoint;

  /**
   * Message describing the error that occurred.
   * 
   */
  #description: string;

  /**
   * Pass that was being applied when the error was emitted.
   * 
   */
  #pass: Pass;

  constructor(pass: Pass, $joinpoint: LaraJoinPoint, description: string) {
    super(`${pass.name} @ ${$joinpoint.joinPointType}: ${description}`);
    this.#description = description;
    this.#joinpoint = $joinpoint;
    this.#pass = pass;
  }

  get description() {
    return this.#description;
  }

  get $joinpoint() {
    return this.#joinpoint;
  }

  get pass() {
    return this.#pass;
  }
}
