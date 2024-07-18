import { LaraJoinPoint } from "../LaraJoinPoint.js";
import ToolJoinPoint from "./public/js/ToolJoinPoint.js";

export default interface GenericAstConverter {
  getToolAst(root: LaraJoinPoint): ToolJoinPoint; 
  getPrettyHtmlCode(root: LaraJoinPoint): string;
}