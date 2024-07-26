import { LaraJoinPoint } from "../LaraJoinPoint.js";
import ToolJoinPoint from "./public/js/ToolJoinPoint.js";

/**
 * @brief Object type for storing the code of each file
 */
export type FilesCode = {
  [filepath: string]: string;
};

/**
 * @brief Interface for the AST converter.
 * @details This interface includes all the compiler specific operations that
 * are required by the LARA visualization tool to work.
 */
export default interface GenericAstConverter {
  /**
   * @brief Updates/Rebuilds the AST on the compiler.
   */
  updateAst(): void;

  /**
   * @brief Converts the compiler AST joinpoints to ToolJoinPoint.
   * 
   * @param root Root of the AST
   * @returns The AST converted to ToolJoinPoint
   */
  getToolAst(root: LaraJoinPoint): ToolJoinPoint; 

  /**
   * @brief Converts the compiled code to HTML code.
   * @details This method should perform the mapping of each AST joinpoint to
   * their respective code, using HTML tags, and optionally its syntax
   * highlighting. See the getNodeCodeTags and getSyntaxHighlightTags methods
   * in AstConverterUtils.js for more information.
   * 
   * @param root Root of the AST
   * @returns The HTML code
   */
  getPrettyHtmlCode(root: LaraJoinPoint): FilesCode;
}