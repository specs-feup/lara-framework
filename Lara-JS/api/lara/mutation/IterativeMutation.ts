import { LaraJoinPoint } from "../../LaraJoinPoint.ts";
import Mutation from "./Mutation.ts";
import MutationResult from "./MutationResult.ts";

/**
 * Abstract mutation class that implements .getMutants(), but makes function Mutation.mutate($jp) abstract. Allows more efficient, generator-based Mutation implementations.
 */
export default abstract class IterativeMutation extends Mutation {
  getMutants($jp: LaraJoinPoint): MutationResult[] {
    const mutations: MutationResult[] = [];

    for (const mutation of this.mutate($jp)) {
      mutations.push(mutation);
    }

    return mutations;
  }

  /**
   * Iterative implementation of the function.
   */
  abstract mutate($jp: LaraJoinPoint): IterableIterator<MutationResult>;
}
