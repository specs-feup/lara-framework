import { LaraJoinPoint } from "../../LaraJoinPoint.js";

/**
 * Contains the results of a single mutation.
 */
export default class MutationResult {
  private $mutation: LaraJoinPoint;

  constructor($mutation: LaraJoinPoint) {
    this.$mutation = $mutation;
  }

  /**
   * @returns A copy of the original join point, where the mutation was applied.
   */
  getMutation(): LaraJoinPoint {
    return this.$mutation;
  }
}
