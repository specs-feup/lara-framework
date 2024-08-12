import { LaraJoinPoint } from "../../LaraJoinPoint.js";

/**
 * Base class that represents a code mutator.
 *
 * This should not be instantiated directly, instead it should be extended.
 *
 * @param name - the name of the mutator
 *
 */
export default abstract class Mutator {
  name: string;

  /**
   * True if the current code is mutated, false otherwise
   */
  isMutated: boolean = false;

  /**
   * If true, before each call to .mutate() will check if the code is already mutated,
   * and call restore before the mutation is applied
   */
  automaticRestore: boolean = true;

  constructor(name: string = "<unnamed mutator>") {
    this.name = name;
  }

  /**
   * @returns The name of this mutator
   */
  getName(): string {
    return this.name;
  }

  /**
   * Enables/disables automatic restore. Is enabled by default.
   *
   * If enabled, before each call to .mutate() will check if the code is already mutated, and call restore before the mutation is applied.
   *
   * @param value - true to enable, false to disable
   */
  setAutomaticRestore(value: boolean = true) {
    this.automaticRestore = value;
  }

  /**
   * Introduces a single mutation to the code.
   * If the code has been mutated already, restores the code before mutating again.
   * If there are no mutations left, does nothing.
   */
  mutate() {
    // If no mutations left, do nothing
    if (!this.hasMutations()) {
      return;
    }

    // If code is currently mutated, call restore first
    // Otherwise, do nothing
    if (this.isMutated) {
      if (this.automaticRestore) {
        this.restore();
      } else {
        console.log("Calling .mutate() without .restore()");
        return;
      }
    }

    // Now can do the mutation
    this.isMutated = true;
    this.mutatePrivate();
  }

  /**
   * If the code has been mutated, restores the code to its original state. If not, does nothing.
   */
  restore() {
    // If not mutated, return
    if (!this.isMutated) {
      return;
    }

    this.isMutated = false;
    this.restorePrivate();
  }

  /**
   * @returns The number of mutations this mutator will apply
   */
  getTotalMutantions(): number {
    return 1;
  }

  /**
   * @returns True, if the Mutator still has mutations left to do, false otherwise.
   */
  abstract hasMutations(): boolean;

  /**
   * @returns The point in the code where the mutation will occur or his occurring, or undefined if there are not more mutations left, or if this concept is not applicable to this mutator.
   */
  abstract getMutationPoint(): LaraJoinPoint | undefined;

  /**
   * @returns The point with currently mutated code, or undefined if this concept is not applicable to this mutator.
   */
  abstract getCurrentMutation(): LaraJoinPoint | undefined;

  /**
   * Adds a join point to this Mutator. Is only added if the Mutator can be applied over this join point, otherwise it will be ignored.
   */
  abstract addJp($joinpoint: LaraJoinPoint): void;

  /**
   * @deprecated use getName() instead
   * @returns The name of this Mutator
   */
  getType(): string {
    return this.getName();
  }

  protected abstract mutatePrivate(): void;

  protected abstract restorePrivate(): void;
}
