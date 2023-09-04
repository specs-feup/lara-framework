import { JavaClasses } from "../util/JavaTypes.js";
import Strings from "../Strings.js";

export default class ToolUtils {
  static parsePath(path: string | JavaClasses.File): string {
    // Ensure paths use / instead of \
    // Using regular expression in order to replace all
    let parsedPath = path.toString().replace(/\\/g, "/");

    // Escape characters
    parsedPath = Strings.escapeJson(parsedPath);

    return parsedPath;
  }
}
