import TraversalType from "../../weaver/TraversalType.js";
import PassTransformationError from "./PassTransformationError.js";
import SimplePass from "./SimplePass.js";
/**
 * Helper class to wrap existing code into a Lara transformation pass.
 */
class AdapterPass extends SimplePass {
    _name;
    matchJp;
    transformJp;
    /**
     * @param includeDescendants - Apply pass to the join point's descendents
     * @param definition - Definition for the Pass
     */
    constructor(includeDescendants = true, definition = {
        name: "",
        traversalType: TraversalType.PREORDER,
        matchJp: () => false,
        transformJp: (jp) => {
            throw new PassTransformationError(this, jp, "Adapter pass not implemented");
        },
    }) {
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
    get name() {
        return this.name;
    }
    /**
     * Predicate that informs the pass whether a certain joinpoint should be transformed
     * @override
     * @param $jp - Join point to match
     * @returns Returns true if the joint point matches the predicate for this pass
     */
    matchJoinpoint($jp) {
        return this.matchJp($jp);
    }
    /**
     * Transformation to be applied to matching joinpoints
     * @override
     * @param $jp - Join point to transform
     * @throws A PassTransformationError if the transformation fails
     * @returns The result of the transformation
     */
    transformJoinpoint($jp) {
        return this.transformJp($jp);
    }
}
export default AdapterPass;
//# sourceMappingURL=AdapterPass.js.map