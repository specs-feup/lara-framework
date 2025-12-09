import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import MutationResult from "./MutationResult.js";

/**
 * Base class that represents a code mutation.
 *
 * This should not be instantiated directly, instead it should be extended.
 *
 * @param name - the name of the mutation
 *
 */
export default abstract class Mutation {
  name: string;

  constructor(name: string = "<unnamed mutation>") {
    this.name = name;
  }

  /**
   * @returns The name of this mutation
   */
  getName(): string {
    return this.name;
  }

  /**
   * Generator function for the mutations.
   *
   * @param $jp - The point in the code to mutate.
   *
   * @returns An iterator that results the results of each mutation on each iteration.
   */
  *mutate($jp: LaraJoinPoint): IterableIterator<MutationResult> {
    const mutants = this.getMutants($jp);
    for (const $mutant of mutants) {
      yield $mutant;
    }
  }

  /**
   * @param $jp - A point in the code to test
   *
   * @returns True if the given join point is a valid mutation point, false otherwise
   */
  abstract isMutationPoint($jp: LaraJoinPoint): boolean;

  /**
   * @param $jp - The point in the code to mutate
   *
   * @returns An array with the results of each mutation, which must be out-of-tree copies of the given join point
   */
  abstract getMutants($jp: LaraJoinPoint): MutationResult[];
}
