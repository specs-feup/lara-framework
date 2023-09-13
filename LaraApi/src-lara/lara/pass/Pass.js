import Query from "../../weaver/Query.js";
import { debug } from "../core/LaraCore.js";
/**
 * Represents a Lara transformation pass.
 *
 * Need to implement:
 *  - _apply_impl($jp)
 */
export default class Pass {
    /**
     * @returns Name of the pass
     */
    get name() {
        return this._name;
    }
    /**
     * Applies this pass starting at the given join point. If no join point is given, uses the root join point
     *
     * @param $jp - Joint point on which the pass will be applied
     * @returns Results of applying this pass to the given joint point
     */
    apply($jp) {
        const $actualJp = $jp ?? Query.root();
        debug(() => `Applying pass '${this.name}' to ${$actualJp.joinPointType}`);
        return this._apply_impl($actualJp);
    }
}
//# sourceMappingURL=Pass.js.map