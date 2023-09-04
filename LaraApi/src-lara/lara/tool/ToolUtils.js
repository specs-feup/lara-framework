import Strings from "../Strings.js";
export default class ToolUtils {
    static parsePath(path) {
        // Ensure paths use / instead of \
        // Using regular expression in order to replace all
        let parsedPath = path.toString().replace(/\\/g, "/");
        // Escape characters
        parsedPath = Strings.escapeJson(parsedPath);
        return parsedPath;
    }
}
//# sourceMappingURL=ToolUtils.js.map