"use strict";

laraImport("clava.ClavaJoinPoints");
laraImport("weaver.WeaverJps");

class VitisHlsUtils {

    static activateAllDirectives(turnOn) {
        const pragmas = [];

        for (var elem of WeaverJps.search("wrapperStmt")) {
            if (elem.code.includes("#pragma HLS") || elem.code.includes("#pragma hls")) {
                pragmas.push(elem);
            }
        }
        for (const pragma of pragmas) {
            println(pragma.code);
            if (turnOn) {
                if (pragma.code.startsWith("//")) {
                    pragma.replaceWith(ClavaJoinPoints.stmtLiteral(pragma.code.replace("//", "")));
                }

            }
            else {
                pragma.replaceWith(ClavaJoinPoints.stmtLiteral("//" + pragma.code));
            }
        }
    }
}