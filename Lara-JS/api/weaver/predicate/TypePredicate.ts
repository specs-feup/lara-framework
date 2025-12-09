import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import JpPredicate from "./JpPredicate.js";
import Weaver from "../Weaver.js";

export default class TypePredicate<T extends typeof LaraJoinPoint> extends JpPredicate {

    constructor(private type: T) {
        super();
    }

    jpName(): string {
        return Weaver.findJoinpointTypeName(this.type) ?? "joinpoint"
    }

    isLaraJoinPoint(): boolean {
        return this.type === LaraJoinPoint;
    }
    isInstance<T extends LaraJoinPoint>(jp: T): boolean {
        return jp instanceof this.type;
    }
    
}
