import { LaraJoinPoint } from "../../LaraJoinPoint.ts";
import JpPredicate from "./JpPredicate.ts";

export default class StringPredicate extends JpPredicate {

    private name: string;

    constructor(name: string) {
        super();
        this.name = name;
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