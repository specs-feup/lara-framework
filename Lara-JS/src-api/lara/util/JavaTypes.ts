import { checkString } from "../core/LaraCore.js";

enum Engine {
  GraalVM = "GraalVM",
  NodeJS = "NodeJS",
}

let engine: Engine = Engine.GraalVM;
let java: { import(str: string): unknown } | undefined = undefined;
  
if ("Java" in globalThis) {
  engine = Engine.GraalVM;
} else {
  //java = await import("java");
  engine = Engine.NodeJS;
}

/**
 * Static variables with class names of Java classes used in the API.
 */
export default class JavaTypes {

  static getType(javaType: string): any {
    checkString(javaType, "_JavaTypes.getType::javaType");

    switch (engine) {
      case Engine.GraalVM:
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
        return Java.type(javaType);
      case Engine.NodeJS:
        return java?.import(javaType);
    }
  }

  static get LaraI() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("larai.LaraI");
  }

  static get LaraApiTools() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.lara.LaraApiTools");
  }

  static get LaraSystemTools() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.lara.LaraSystemTools");
  }

  static get LaraCli() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("org.lara.interpreter.cli.LaraCli");
  }

  static get Uuid() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("java.util.UUID");
  }

  static get Gprofer() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.gprofer.Gprofer");
  }

  static get JoinPoint() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("org.lara.interpreter.weaver.interf.JoinPoint");
  }

  static get SpecsStrings() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.util.SpecsStrings");
  }

  static get SpecsSystem() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.util.SpecsSystem");
  }

  static get ApacheStrings() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.lang.ApacheStrings");
  }

  static get StringLines() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.util.utilities.StringLines");
  }

  static get LaraIo() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("org.lara.interpreter.api.LaraIo");
  }

  static get SpecsIo() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.util.SpecsIo");
  }

  static get JavaSystem() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("java.lang.System");
  }

  static get JavaFile() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("java.io.File");
  }

  static get List() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("java.util.List");
  }

  static get Collections() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("java.util.Collections");
  }

  static get JavaDiff() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("pt.up.fe.specs.lara.util.JavaDiffHelper");
  }

  static get XStreamUtils() {
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return JavaTypes.getType("org.suikasoft.XStreamPlus.XStreamUtils");
  }
  
}
