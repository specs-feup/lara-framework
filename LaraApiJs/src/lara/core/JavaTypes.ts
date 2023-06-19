import { checkString } from "./LaraCore.js";

/**
 * Static variables with class names of Java classes used in the API.
 * @class
 */
export default class JavaTypes {
    static getType(javaType: any) {
        checkString(javaType, "_JavaTypes.getType::javaType");

        // @ts-ignore
        return Java.type(javaType);
    }

    static get LaraI() {
        return JavaTypes.getType("larai.LaraI");
    }

    static get LaraApiTools() {
        return JavaTypes.getType("pt.up.fe.specs.lara.LaraApiTools");
    }

    static get LaraSystemTools() {
        return JavaTypes.getType("pt.up.fe.specs.lara.LaraSystemTools");
    }

    static get LaraCli() {
        return JavaTypes.getType("org.lara.interpreter.cli.LaraCli");
    }

    static get Uuid() {
        return JavaTypes.getType("java.util.UUID");
    }

    static get Gprofer() {
        return JavaTypes.getType("pt.up.fe.specs.gprofer.Gprofer");
    }

    static get JoinPoint() {
        return JavaTypes.getType(
            "org.lara.interpreter.weaver.interf.JoinPoint"
        );
    }

    static get SpecsStrings() {
        return JavaTypes.getType("pt.up.fe.specs.util.SpecsStrings");
    }

    static get SpecsSystem() {
        return JavaTypes.getType("pt.up.fe.specs.util.SpecsSystem");
    }

    static get ApacheStrings() {
        return JavaTypes.getType("pt.up.fe.specs.lang.ApacheStrings");
    }

    static get StringLines() {
        return JavaTypes.getType("pt.up.fe.specs.util.utilities.StringLines");
    }

    static get LaraIo() {
        return JavaTypes.getType("org.lara.interpreter.api.LaraIo");
    }

    static get SpecsIo() {
        return JavaTypes.getType("pt.up.fe.specs.util.SpecsIo");
    }

    static get JavaSystem() {
        return JavaTypes.getType("java.lang.System");
    }

    static get JavaFile() {
        return JavaTypes.getType("java.io.File");
    }

    static get List() {
        return JavaTypes.getType("java.util.List");
    }

    static get Collections() {
        return JavaTypes.getType("java.util.Collections");
    }

    static get JavaDiff() {
        return JavaTypes.getType("pt.up.fe.specs.lara.util.JavaDiffHelper");
    }

    static get XStreamUtils() {
        return JavaTypes.getType("org.suikasoft.XStreamPlus.XStreamUtils");
    }
}