import java from "java";
export var Engine;
(function (Engine) {
    Engine["GraalVM"] = "GraalVM";
    Engine["NodeJS"] = "NodeJS";
})(Engine || (Engine = {}));
export const NodeJavaPrefix = "nodeJava_";
export let engine = Engine.GraalVM;
if ("Java" in globalThis) {
    engine = Engine.GraalVM;
}
else {
    engine = Engine.NodeJS;
}
export default class JavaTypes {
    static typeMap = new Map();
    /**
     * @beta Only for very exceptional cases. Should not be used directly, use the static methods instead.
     *
     * @param javaTypeName - String with the name of the Java type to be imported into the javascript environment
     * @returns A Java object
     */
    static getType(javaTypeName) {
        if (JavaTypes.typeMap.has(javaTypeName)) {
            return JavaTypes.typeMap.get(javaTypeName);
        }
        let javaType;
        switch (engine) {
            case Engine.GraalVM:
                javaType = Java.type(javaTypeName);
                break;
            case Engine.NodeJS:
                javaType = java.import(javaTypeName);
                break;
        }
        JavaTypes.typeMap.set(javaTypeName, javaType);
        return javaType;
    }
    static instanceOf(value, javaTypeName) {
        switch (engine) {
            case Engine.GraalVM:
                // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
                if (Java.isJavaObject(value)) {
                    // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
                    return Java.type(javaTypeName).class.isInstance(value);
                }
                return Java.typeName(value) === javaTypeName;
            case Engine.NodeJS:
                return java.instanceOf(value, javaTypeName);
        }
    }
    static isJavaObject(value) {
        if (engine == Engine.NodeJS) {
            return (typeof value === "object" &&
                value !== null &&
                Object.getPrototypeOf(value).constructor.name.startsWith(NodeJavaPrefix));
        }
        try {
            value.getClass().getName();
            return true;
        }
        catch (error) {
            return false;
        }
    }
    static get LaraI() {
        return JavaTypes.getType("larai.LaraI");
    }
    static get LaraApiTool() {
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
        return JavaTypes.getType("org.lara.interpreter.weaver.interf.JoinPoint");
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
    static get System() {
        return JavaTypes.getType("java.lang.System");
    }
    static get File() {
        return JavaTypes.getType("java.io.File");
    }
    static get List() {
        return JavaTypes.getType("java.util.List");
    }
    static get Collections() {
        return JavaTypes.getType("java.util.Collections");
    }
    static get Diff() {
        return JavaTypes.getType("pt.up.fe.specs.lara.util.JavaDiffHelper");
    }
    static get XStreamUtils() {
        return JavaTypes.getType("org.suikasoft.XStreamPlus.XStreamUtils");
    }
    static get Object() {
        return JavaTypes.getType("java.lang.Object");
    }
    static get ReplacerHelper() {
        return JavaTypes.getType("pt.up.fe.specs.lara.util.ReplacerHelper");
    }
    static get CsvReader() {
        return JavaTypes.getType("pt.up.fe.specs.util.csv.CsvReader");
    }
    static get CsvField() {
        return JavaTypes.getType("pt.up.fe.specs.util.csv.CsvField");
    }
    static get CsvWriter() {
        return JavaTypes.getType("pt.up.fe.specs.util.csv.CsvWriter");
    }
    static get DataStore() {
        return JavaTypes.getType("org.suikasoft.jOptions.Interfaces.DataStore");
    }
    static get JOptionsUtils() {
        return JavaTypes.getType("org.suikasoft.jOptions.JOptionsUtils");
    }
    static get WeaverEngine() {
        return JavaTypes.getType("org.lara.interpreter.weaver.interf.WeaverEngine");
    }
    static get VerboseLevel() {
        return JavaTypes.getType("org.lara.interpreter.joptions.config.interpreter.VerboseLevel");
    }
    static get LaraiKeys() {
        return JavaTypes.getType("org.lara.interpreter.joptions.config.interpreter.LaraiKeys");
    }
    static get FileList() {
        return JavaTypes.getType("org.lara.interpreter.joptions.keys.FileList");
    }
    static get OptionalFile() {
        return JavaTypes.getType("org.lara.interpreter.joptions.keys.OptionalFile");
    }
    static get LaraIUtils() {
        return JavaTypes.getType("org.lara.interpreter.utils.LaraIUtils");
    }
    static get WeaverLauncher() {
        return JavaTypes.getType("pt.up.fe.specs.lara.WeaverLauncher");
    }
    static get ArrayList() {
        return JavaTypes.getType("java.util.ArrayList");
    }
    static get HashMap() {
        return JavaTypes.getType("java.util.HashMap");
    }
    static get SpecsPlatforms() {
        return JavaTypes.getType("pt.up.fe.specs.lang.SpecsPlatforms");
    }
    static get Runtime() {
        return JavaTypes.getType("java.lang.Runtime");
    }
    static get LARASystem() {
        return JavaTypes.getType("Utils.LARASystem");
    }
    static get ProcessOutputAsString() {
        return JavaTypes.getType("pt.up.fe.specs.util.system.ProcessOutputAsString");
    }
    static get JsGear() {
        return JavaTypes.getType("org.lara.interpreter.utils.JsGear");
    }
    static get ProgressCounter() {
        return JavaTypes.getType("pt.up.fe.specs.util.utilities.ProgressCounter");
    }
    static get LineStream() {
        return JavaTypes.getType("pt.up.fe.specs.util.utilities.LineStream");
    }
}
//# sourceMappingURL=JavaTypes.js.map