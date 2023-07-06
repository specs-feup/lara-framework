enum Engine {
  GraalVM = "GraalVM",
  NodeJS = "NodeJS",
}

let engine: Engine = Engine.GraalVM;
// eslint-disable-next-line prefer-const
let java: any = undefined;

if ("Java" in globalThis) {
  engine = Engine.GraalVM;
} else {
  engine = Engine.NodeJS;
  /**
   * This is a hack to load Java classes in NodeJS.
   * If the dynamic import is not done inside the eval function, then GraalVM
   * will try to load the 'java' module and silently fail (even if it shouln't
   * as this 'else' branch is never executed in a GraalVM environment).
   *
   * The anonymous async function is needed to avoid the following error:
   * SyntaxError: await is only valid in async functions and the top level
   * bodies of modules
   *
   */
  eval(`(async () => {
    const { default: javaLocal } = await import("java");
    java = javaLocal;
  })();`);
}

/**
 * Static variables with class names of Java classes used in the API.
 */
export default class JavaTypes {
  static getType(javaType: string): any {
    switch (engine) {
      case Engine.GraalVM:
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
        return Java.type(javaType);
      case Engine.NodeJS:
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
        return java?.import(javaType);
    }
  }

  static isJavaObject(obj: any) {
    switch (engine) {
      case Engine.GraalVM:
        return Java.isJavaObject(obj);
      case Engine.NodeJS:
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return, @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
        return java.instanceOf(obj, "java.lang.Object");
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

  static get ReplacerHelper(){
    // eslint-disable-next-line @typescript-eslint/no-unsafe-return
    return Java.type("pt.up.fe.specs.lara.util.ReplacerHelper");
  }
}
