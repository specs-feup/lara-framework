export enum Engine {
  GraalVM = "GraalVM",
  NodeJS = "NodeJS",
}

export let engine: Engine = Engine.GraalVM;
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
export function getType(javaType: string): unknown {
  switch (engine) {
    case Engine.GraalVM:
      const a = Java.type(javaType);
      return a;

    case Engine.NodeJS:
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
      return java?.import(javaType);
  }
}

// eslint-disable-next-line @typescript-eslint/no-namespace
export namespace JavaClasses {
  export interface JavaClass {
    (...args: unknown[]): any;
    new (...args: unknown[]): any;
    [key: string]: any;
  }

  /* eslint-disable @typescript-eslint/no-empty-interface */
  export interface LaraI extends JavaClass {}
  export interface LaraApiTools extends JavaClass {}
  export interface LaraSystemTools extends JavaClass {}
  export interface LaraCli extends JavaClass {}
  export interface Uuid extends JavaClass {}
  export interface Gprofer extends JavaClass {}
  export interface JoinPoint extends JavaClass {}
  export interface SpecsStrings extends JavaClass {}
  export interface SpecsSystem extends JavaClass {}
  export interface ApacheStrings extends JavaClass {}
  export interface StringLines extends JavaClass {}
  export interface LaraIo extends JavaClass {}
  export interface SpecsIo extends JavaClass {}
  export interface System extends JavaClass {}
  export interface File extends JavaClass {}
  export interface List extends JavaClass {}
  export interface Collections extends JavaClass {}
  export interface Diff extends JavaClass {}
  export interface XStreamUtils extends JavaClass {}
  export interface Object extends JavaClass {}
  export interface ReplacerHelper extends JavaClass {}
  export interface CsvReader extends JavaClass {}
  export interface DataStore extends JavaClass {}
  export interface JOptionsUtils extends JavaClass {}
  export interface WeaverEngine extends JavaClass {}
  export interface VerboseLevel extends JavaClass {}
  export interface LaraiKeys extends JavaClass {}
  export interface FileList extends JavaClass {}
  export interface OptionalFile extends JavaClass {}
  export interface LaraIUtils extends JavaClass {}
  export interface WeaverLauncher extends JavaClass {}
  export interface ArrayList extends JavaClass {}
  /* eslint-enable @typescript-eslint/no-empty-interface */
}

export default class JavaTypes {
  static get LaraI() {
    return getType("larai.LaraI") as JavaClasses.LaraI;
  }

  static get LaraApiTool() {
    return getType(
      "pt.up.fe.specs.lara.LaraApiTools"
    ) as JavaClasses.LaraApiTools;
  }

  static get LaraSystemTools() {
    return getType(
      "pt.up.fe.specs.lara.LaraSystemTools"
    ) as JavaClasses.LaraSystemTools;
  }

  static get LaraCli() {
    return getType("org.lara.interpreter.cli.LaraCli") as JavaClasses.LaraCli;
  }

  static get Uuid() {
    return getType("java.util.UUID") as JavaClasses.Uuid;
  }

  static get Gprofer() {
    return getType("pt.up.fe.specs.gprofer.Gprofer") as JavaClasses.Gprofer;
  }

  static get JoinPoint() {
    return getType(
      "org.lara.interpreter.weaver.interf.JoinPoint"
    ) as JavaClasses.JoinPoint;
  }

  static get SpecsStrings() {
    return getType(
      "pt.up.fe.specs.util.SpecsStrings"
    ) as JavaClasses.SpecsStrings;
  }

  static get SpecsSystem() {
    return getType(
      "pt.up.fe.specs.util.SpecsSystem"
    ) as JavaClasses.SpecsSystem;
  }

  static get ApacheStrings() {
    return getType(
      "pt.up.fe.specs.lang.ApacheStrings"
    ) as JavaClasses.ApacheStrings;
  }

  static get StringLines() {
    return getType(
      "pt.up.fe.specs.util.utilities.StringLines"
    ) as JavaClasses.StringLines;
  }

  static get LaraIo() {
    return getType("org.lara.interpreter.api.LaraIo") as JavaClasses.LaraIo;
  }

  static get SpecsIo() {
    return getType("pt.up.fe.specs.util.SpecsIo") as JavaClasses.SpecsIo;
  }

  static get System() {
    return getType("java.lang.System") as JavaClasses.System;
  }

  static get File() {
    return getType("java.io.File") as JavaClasses.File;
  }

  static get List() {
    return getType("java.util.List") as JavaClasses.List;
  }

  static get Collections() {
    return getType("java.util.Collections") as JavaClasses.Collections;
  }

  static get Diff() {
    return getType(
      "pt.up.fe.specs.lara.util.JavaDiffHelper"
    ) as JavaClasses.Diff;
  }

  static get XStreamUtils() {
    return getType(
      "org.suikasoft.XStreamPlus.XStreamUtils"
    ) as JavaClasses.XStreamUtils;
  }

  static get Object() {
    return getType("java.lang.Object") as JavaClasses.Object;
  }

  static get ReplacerHelper() {
    return getType(
      "pt.up.fe.specs.lara.util.ReplacerHelper"
    ) as JavaClasses.ReplacerHelper;
  }

  static get CsvReader() {
    return getType(
      "pt.up.fe.specs.util.csv.CsvReader"
    ) as JavaClasses.CsvReader;
  }

  static get DataStore() {
    return getType(
      "org.suikasoft.jOptions.Interfaces.DataStore"
    ) as JavaClasses.DataStore;
  }

  static get JOptionsUtils() {
    return getType(
      "org.suikasoft.jOptions.JOptionsUtils"
    ) as JavaClasses.JOptionsUtils;
  }

  static get WeaverEngine() {
    return getType(
      "org.lara.interpreter.weaver.interf.WeaverEngine"
    ) as JavaClasses.WeaverEngine;
  }

  static get VerboseLevel() {
    return getType(
      "org.lara.interpreter.joptions.config.interpreter"
    ) as JavaClasses.VerboseLevel;
  }

  static get LaraiKeys() {
    return getType(
      "org.lara.interpreter.joptions.config.interpreter.LaraiKeys"
    ) as JavaClasses.LaraiKeys;
  }

  static get FileList() {
    return getType(
      "org.lara.interpreter.joptions.keys.FileList"
    ) as JavaClasses.FileList;
  }

  static get OptionalFile() {
    return getType(
      "org.lara.interpreter.joptions.keys.OptionalFile"
    ) as JavaClasses.OptionalFile;
  }

  static get LaraIUtils() {
    return getType(
      "org.lara.interpreter.utils.LaraIUtils"
    ) as JavaClasses.LaraIUtils;
  }

  static get WeaverLauncher() {
    return getType(
      "pt.up.fe.specs.lara.WeaverLauncher"
    ) as JavaClasses.WeaverLauncher;
  }

  static get ArrayList() {
    return getType("java.util.ArrayList") as JavaClasses.ArrayList;
  }
}
