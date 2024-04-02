import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import TraversalType from "../../weaver/TraversalType.js";
import PassTransformationError from "./PassTransformationError.js";
import SimplePass from "./SimplePass.js";
import AggregatePassResult from "./results/AggregatePassResult.js";

/**
 * Helper class to wrap existing code into a Lara transformation pass.
 */
class AdapterPass extends SimplePass {
  protected _name: string;
  private matchJp: AdapterPass.AdapterPassDefinition["matchJp"];
  private transformJp: AdapterPass.AdapterPassDefinition["transformJp"];

  /**
   * @param includeDescendants - Apply pass to the join point's descendents
   * @param definition - Definition for the Pass
   */
  constructor(
    includeDescendants: boolean = true,
    definition: AdapterPass.AdapterPassDefinition = {
      name: "",
      traversalType: TraversalType.PREORDER,
      matchJp: () => false,
      transformJp: (jp: LaraJoinPoint) => {
        throw new PassTransformationError(
          this,
          jp,
          "Adapter pass not implemented"
        );
      },
    }
  ) {
    super(includeDescendants);
    this._name = definition.name;
    this._traversalType = definition.traversalType;
    this.matchJp = definition.matchJp;
    this.transformJp = definition.transformJp;
  }

  /**
   * @returns Name of the pass
   * @override
   */
  get name(): string {
    return this.name;
  }

  /**
   * Predicate that informs the pass whether a certain joinpoint should be transformed
   * @override
   * @param $jp - Join point to match
   * @returns Returns true if the joint point matches the predicate for this pass
   */
  matchJoinpoint($jp: LaraJoinPoint): boolean {
    return this.matchJp($jp);
  }

  /**
   * Transformation to be applied to matching joinpoints
   * @override
   * @param $jp - Join point to transform
   * @throws A PassTransformationError if the transformation fails
   * @returns The result of the transformation
   */
  transformJoinpoint(
    $jp: LaraJoinPoint
  ): ReturnType<AdapterPass["transformJp"]> {
    return this.transformJp($jp);
  }
}

// eslint-disable-next-line @typescript-eslint/no-namespace
namespace AdapterPass {
  /**
   * @param name - Name of the pass
   * @param traversalType - Order in which the join point's descendants should be visited
   * @param matchJp - Predicate that informs the pass whether a certain joinpoint should be transformed
   * @param transformJp - Transformation to be applied to matching joinpoints
   */
  export interface AdapterPassDefinition {
    name: string;
    traversalType: TraversalType;
    matchJp: (jp: LaraJoinPoint) => boolean;
    transformJp: (jp: LaraJoinPoint) => AggregatePassResult | never;
  }
}

export default AdapterPass;
