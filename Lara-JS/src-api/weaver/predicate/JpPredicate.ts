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

    abstract isInstance<T extends LaraJoinPoint>(jp: T):boolean
}
/*
-> obter string com nome
-> testar se é a classe LaraJoinPoint (LCL)
-> Testar se um join point corresponde ao tipo
*/