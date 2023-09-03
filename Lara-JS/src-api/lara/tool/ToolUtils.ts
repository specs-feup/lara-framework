"use strict";

class ToolUtils {
    static parsePath = function (path) {
        //println("ORIGINAL PATH: " + path);

        // Ensure paths use / instead of \	
        // Using regular expression in order to replace all
        var parsedPath = Strings.replacer(path.toString(), /\\/g, '/');
        //var parsedPath = Strings.replacer(path.toString(), '\\\\', '/');

        //println("NO SLASH PATH: " + parsedPath);

        // Escape characters
        var parsedPath = Strings.escapeJson(parsedPath);

        //println("ESCAPED PATH: " + parsedPath);

        return parsedPath;
    }
}