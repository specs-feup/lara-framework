import TraversalType from "../../weaver/TraversalType.js";
import Pass from "./Pass.js";
import PassTransformationError from "./PassTransformationError.js";
import AggregatePassResult from "./results/AggregatePassResult.js";
/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - matchJoinpoint($jp)
 *  - transformJoinpoint($jp)
 */
export default class SimplePass extends Pass {
    _traversalType = TraversalType.PREORDER;
    /**
     * @param includeDescendants - Apply pass to the join point's descendents
     */
    constructor(includeDescendants = true) {
        super();
        this.includeDescendants = includeDescendants;
    }
    /**
     * Should the Pass be applied to the join point's descendants
     *
     */
    includeDescendants;
    /**
     * Order in which the join point's descendants should be visited
     *
     */
    get traversalType() {
        return this._traversalType;
    }
    /**
     * Selects the join points and its descendants, if needed, according to the traversalType
     * @param $jp - The point in the code from which to select
     * @returns Array of join points selected
     */
    _selectedJps($jp) {
        if (this.includeDescendants === false) {
            return [$jp];
        }
        switch (this._traversalType) {
            case TraversalType.PREORDER:
                return [$jp, ...$jp.descendants];
            case TraversalType.POSTORDER:
                throw new Error("Postorder descendants not implemented");
            default:
                throw new Error("Traversal type not implemented: " + String(this._traversalType));
        }
    }
    /**
     * Apply tranformation to
     *
     * @param $jp - Joinpoint on which the pass will be applied
     * @returns Results of applying this pass to the given joinpoint
     */
    _apply_impl($jp) {
        const matchingJps = this._selectedJps($jp).filter(($jp) => this.matchJoinpoint($jp));
        const aggResult = new AggregatePassResult(this, $jp);
        for (const $jp of matchingJps) {
            try {
                const result = this.transformJoinpoint($jp);
                aggResult.pushResult(result);
            }
            catch (e) {
                if (e instanceof PassTransformationError) {
                    aggResult.pushError(e);
                }
                else
                    throw e;
            }
        }
        return aggResult;
    }
}
//# sourceMappingURL=SimplePass.js.map