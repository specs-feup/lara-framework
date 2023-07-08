enum Engine {
  GraalVM = "GraalVM",
  NodeJS = "NodeJS",
}

let engine: Engine = Engine.GraalVM;
// eslint-disable-next-line prefer-const, @typescript-eslint/no-explicit-any
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
    java.classpath.push("../../ClavaWeaver.jar");
  })();`);
}

// eslint-disable-next-line @typescript-eslint/ban-types
function getType(javaType: string): unknown {
  switch (engine) {
    case Engine.GraalVM:
      return Java.type(javaType);
    case Engine.NodeJS:
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      return java?.import(javaType);
  }
}

// eslint-disable-next-line @typescript-eslint/no-namespace
namespace JavaTypes {
  export interface JavaClass {
    (...args: unknown[]): any;
    new (...args: unknown[]): any;
    [key: string]: any;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaLaraI extends JavaClass {}

  export function getJavaLaraI(): JavaLaraI {
    return getType("larai.LaraI") as JavaLaraI;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaLaraApiTools extends JavaClass {}
  export function getJavaLaraApiTools() {
    return getType("pt.up.fe.specs.lara.LaraApiTools") as JavaLaraApiTools;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaLaraSystemTools extends JavaClass {}
  export function getJavaLaraSystemTools() {
    return getType(
      "pt.up.fe.specs.lara.LaraSystemTools"
    ) as JavaLaraSystemTools;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaLaraCli extends JavaClass {}
  export function getJavaLaraCli() {
    return getType("org.lara.interpreter.cli.LaraCli") as JavaLaraCli;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaUuid extends JavaClass {}
  export function getJavaUuid() {
    return getType("java.util.UUID") as JavaUuid;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaGprofer extends JavaClass {}
  export function getJavaGprofer() {
    return getType("pt.up.fe.specs.gprofer.Gprofer") as JavaGprofer;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaJoinPoint extends JavaClass {}
  export function getJavaJoinPoint() {
    return getType(
      "org.lara.interpreter.weaver.interf.JoinPoint"
    ) as JavaJoinPoint;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaSpecsStrings extends JavaClass {}
  export function getJavaSpecsStrings() {
    return getType("pt.up.fe.specs.util.SpecsStrings") as JavaSpecsStrings;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaSpecsSystem extends JavaClass {}
  export function getJavaSpecsSystem() {
    return getType("pt.up.fe.specs.util.SpecsSystem") as JavaSpecsSystem;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaApacheStrings extends JavaClass {}
  export function getJavaApacheStrings() {
    return getType("pt.up.fe.specs.lang.ApacheStrings") as JavaApacheStrings;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaStringLines extends JavaClass {}
  export function getJavaStringLines() {
    return getType(
      "pt.up.fe.specs.util.utilities.StringLines"
    ) as JavaStringLines;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaLaraIo extends JavaClass {}
  export function getJavaLaraIo() {
    return getType("org.lara.interpreter.api.LaraIo") as JavaLaraIo;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaSpecsIo extends JavaClass {}
  export function getJavaSpecsIo() {
    return getType("pt.up.fe.specs.util.SpecsIo") as JavaSpecsIo;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaSystem extends JavaClass {}
  export function getJavaSystem() {
    return getType("java.lang.System") as JavaSystem;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaFile extends JavaClass {}
  export function getJavaFile() {
    return getType("java.io.File") as JavaFile;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaList extends JavaClass {}
  export function getJavaList() {
    return getType("java.util.List") as JavaList;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaCollections extends JavaClass {}
  export function getJavaCollections() {
    return getType("java.util.Collections") as JavaCollections;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaDiff extends JavaClass {}
  export function getJavaDiff() {
    return getType("pt.up.fe.specs.lara.util.JavaDiffHelper") as JavaDiff;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaXStreamUtils extends JavaClass {}
  export function getJavaXStreamUtils() {
    return getType(
      "org.suikasoft.XStreamPlus.XStreamUtils"
    ) as JavaXStreamUtils;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaObject extends JavaClass {}
  export function getJavaObject() {
    return getType("java.lang.Object") as JavaObject;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaReplacerHelper extends JavaClass {}
  export function getJavaReplacerHelper() {
    return getType(
      "pt.up.fe.specs.lara.util.ReplacerHelper"
    ) as JavaReplacerHelper;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaCsvReader extends JavaClass {}
  export function getJavaCsvReader() {
    return getType("pt.up.fe.specs.util.csv.CsvReader") as JavaCsvReader;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaDataStore extends JavaClass {}
  export function getJavaDataStore() {
    return getType(
      "org.suikasoft.jOptions.Interfaces.DataStore"
    ) as JavaDataStore;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  export interface JavaJOptionsUtils extends JavaClass {}
  export function getJavaJOptionsUtils() {
    return getType("org.suikasoft.jOptions.JOptionsUtils") as JavaJOptionsUtils;
  }
}

export default JavaTypes;
