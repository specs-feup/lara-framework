import { LaraJoinPoint } from "../../LaraJoinPoint.js";

export default abstract class JpPredicate {
  
    /**
     * @returns the name of the join point
     */
    abstract jpName() : string;

    /**
     * @returns true if the underlying type is THE class LaraJoinPoint
     */
    abstract isLaraJoinPoint() : boolean;

    /**
     * 
     * @param jp the join point we want to test
     * @returns true if the join point is accepted by this predicate 
     */
    abstract isInstance<T extends LaraJoinPoint>(jp: T):boolean
}