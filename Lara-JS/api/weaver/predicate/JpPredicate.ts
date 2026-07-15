import { LaraJoinPoint } from "../../LaraJoinPoint.ts";

export default abstract class JpPredicate {
  
    /**
     * @returns The name of the join point
     */
    abstract jpName() : string;

    /**
     * @returns True if the underlying type is THE class LaraJoinPoint
     */
    abstract isLaraJoinPoint() : boolean;

    /**
     * 
     * @param jp - The join point we want to test
     * @returns True if the join point is accepted by this predicate 
     */
    abstract isInstance<T extends LaraJoinPoint>(jp: T):boolean
}