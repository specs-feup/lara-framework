import { LaraJoinPoint } from "../../LaraJoinPoint.ts";
import JpPredicate from "./JpPredicate.ts";
import Weaver from "../Weaver.ts";

export default class TypePredicate<T extends typeof LaraJoinPoint> extends JpPredicate {

    private type: T;

    constructor(type: T) {
        super();
        this.type = type;
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
