import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import JpPredicate from "./JpPredicate.js";
import Weaver from "../Weaver.js";
export default class TypePredicate extends JpPredicate {
    type;
    constructor(type) {
        super();
        this.type = type;
    }
    jpName() {
        return Weaver.findJoinpointTypeName(this.type) ?? "joinpoint";
    }
    isLaraJoinPoint() {
        return this.type === LaraJoinPoint;
    }
    isInstance(jp) {
        return jp instanceof this.type;
    }
}
//# sourceMappingURL=TypePredicate.js.map