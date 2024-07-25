import { LaraJoinPoint } from "../LaraJoinPoint.js";
import ToolJoinPoint from "./public/js/ToolJoinPoint.js";

export type FilesCode = {
  [file: string]: string;
};

export default interface GenericAstConverter {
  updateAst(): void;
  getToolAst(root: LaraJoinPoint): ToolJoinPoint; 
  getPrettyHtmlCode(root: LaraJoinPoint): FilesCode;
}