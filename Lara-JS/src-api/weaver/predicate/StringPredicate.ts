import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import JpPredicate from "./JpPredicate.js";

export default class StringPredicate extends JpPredicate {

    constructor(private name: string) {
        super();
    }

    jpName(): string {
        return this.name;
    }
    isLaraJoinPoint(): boolean 
    {
        return false;
    }

    isInstance<T extends LaraJoinPoint>(jp: T): boolean {
        return jp.instanceOf(this.name);
    }

}