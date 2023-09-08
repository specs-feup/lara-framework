import java from "java";

export enum Engine {
  GraalVM = "GraalVM",
  NodeJS = "NodeJS",
}

export let engine: Engine = Engine.GraalVM;

if ("Java" in globalThis) {
  engine = Engine.GraalVM;
} else {
  engine = Engine.NodeJS;
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
  export interface LaraSystemTools extends JavaClass {
    runCommand(
      command: string | JavaClasses.List,
      workingDir: string,
      printToConsole: boolean,
      timeoutNanos?: number
    ): JavaClasses.ProcessOutputAsString;
  }
  export interface LaraCli extends JavaClass {}
  export interface Uuid extends JavaClass {}
  export interface Gprofer extends JavaClass {}
  export interface JoinPoint extends JavaClass {}
  export interface SpecsStrings extends JavaClass {
    escapeJson(str: string): string;
  }
  export interface SpecsSystem extends JavaClass {}
  export interface ApacheStrings extends JavaClass {}
  export interface StringLines extends JavaClass {}
  export interface LaraIo extends JavaClass {}
  export interface SpecsIo extends JavaClass {}
  export interface System extends JavaClass {}
  export interface File extends JavaClass {
    getParentFile(): JavaClasses.File;
    getAbsolutePath(): string;
  }
  export interface List extends JavaClass {}
  export interface Collections extends JavaClass {}
  export interface Diff extends JavaClass {}
  export interface XStreamUtils extends JavaClass {}
  export interface Object extends JavaClass {}
  export interface ReplacerHelper extends JavaClass {}
  export interface CsvReader extends JavaClass {}
  export interface CsvWriter extends JavaClasses.JavaClass {}
  export interface CsvField extends JavaClasses.JavaClass {}
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
  export interface HashMap extends JavaClass {}
  export interface SpecsPlatforms extends JavaClass {}
  export interface Runtime extends JavaClass {}
  export interface LARASystem extends JavaClass {}
  export interface ProcessOutputAsString extends JavaClass {
    getOutput(): string;
  }
  export interface JsGear extends JavaClass {
    setJsOnAction(onActionCallback: (d: Record<string, any>) => void): boolean;
    onAction(actionEvent: any): void;
  }
  export interface ProgressCounter extends JavaClasses.JavaClass {}
  /* eslint-enable @typescript-eslint/no-empty-interface */
}

export default class JavaTypes {
  /**
   * @beta Only for very exceptional cases. Should not be used directly, use the static methods instead.
   *
   * @param javaType - String with the name of the Java type to be imported into the javascript environment
   * @returns A Java object
   */
  static getType(javaType: string): any {
    switch (engine) {
      case Engine.GraalVM:
        return Java.type(javaType);
      case Engine.NodeJS:
        return java.import(javaType);
    }
  }

  static instanceOf<T>(value: T, javaTypeName: string): boolean {
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

  static isJavaObject<T>(value: T): boolean {
    try {
      (value as any).getClass().getName();
      return true;
    } catch (error) {
      return false;
    }
  }

  static get LaraI() {
    return JavaTypes.getType("larai.LaraI") as JavaClasses.LaraI;
  }

  static get LaraApiTool() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lara.LaraApiTools"
    ) as JavaClasses.LaraApiTools;
  }

  static get LaraSystemTools() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lara.LaraSystemTools"
    ) as JavaClasses.LaraSystemTools;
  }

  static get LaraCli() {
    return JavaTypes.getType(
      "org.lara.interpreter.cli.LaraCli"
    ) as JavaClasses.LaraCli;
  }

  static get Uuid() {
    return JavaTypes.getType("java.util.UUID") as JavaClasses.Uuid;
  }

  static get Gprofer() {
    return JavaTypes.getType(
      "pt.up.fe.specs.gprofer.Gprofer"
    ) as JavaClasses.Gprofer;
  }

  static get JoinPoint() {
    return JavaTypes.getType(
      "org.lara.interpreter.weaver.interf.JoinPoint"
    ) as JavaClasses.JoinPoint;
  }

  static get SpecsStrings() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.SpecsStrings"
    ) as JavaClasses.SpecsStrings;
  }

  static get SpecsSystem() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.SpecsSystem"
    ) as JavaClasses.SpecsSystem;
  }

  static get ApacheStrings() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lang.ApacheStrings"
    ) as JavaClasses.ApacheStrings;
  }

  static get StringLines() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.utilities.StringLines"
    ) as JavaClasses.StringLines;
  }

  static get LaraIo() {
    return JavaTypes.getType(
      "org.lara.interpreter.api.LaraIo"
    ) as JavaClasses.LaraIo;
  }

  static get SpecsIo() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.SpecsIo"
    ) as JavaClasses.SpecsIo;
  }

  static get System() {
    return JavaTypes.getType("java.lang.System") as JavaClasses.System;
  }

  static get File() {
    return JavaTypes.getType("java.io.File") as JavaClasses.File;
  }

  static get List() {
    return JavaTypes.getType("java.util.List") as JavaClasses.List;
  }

  static get Collections() {
    return JavaTypes.getType(
      "java.util.Collections"
    ) as JavaClasses.Collections;
  }

  static get Diff() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lara.util.JavaDiffHelper"
    ) as JavaClasses.Diff;
  }

  static get XStreamUtils() {
    return JavaTypes.getType(
      "org.suikasoft.XStreamPlus.XStreamUtils"
    ) as JavaClasses.XStreamUtils;
  }

  static get Object() {
    return JavaTypes.getType("java.lang.Object") as JavaClasses.Object;
  }

  static get ReplacerHelper() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lara.util.ReplacerHelper"
    ) as JavaClasses.ReplacerHelper;
  }

  static get CsvReader() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.csv.CsvReader"
    ) as JavaClasses.CsvReader;
  }

  static get CsvField() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.csv.CsvField"
    ) as JavaClasses.CsvField;
  }

  static get CsvWriter() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.csv.CsvWriter"
    ) as JavaClasses.CsvWriter;
  }

  static get DataStore() {
    return JavaTypes.getType(
      "org.suikasoft.jOptions.Interfaces.DataStore"
    ) as JavaClasses.DataStore;
  }

  static get JOptionsUtils() {
    return JavaTypes.getType(
      "org.suikasoft.jOptions.JOptionsUtils"
    ) as JavaClasses.JOptionsUtils;
  }

  static get WeaverEngine() {
    return JavaTypes.getType(
      "org.lara.interpreter.weaver.interf.WeaverEngine"
    ) as JavaClasses.WeaverEngine;
  }

  static get VerboseLevel() {
    return JavaTypes.getType(
      "org.lara.interpreter.joptions.config.interpreter.VerboseLevel"
    ) as JavaClasses.VerboseLevel;
  }

  static get LaraiKeys() {
    return JavaTypes.getType(
      "org.lara.interpreter.joptions.config.interpreter.LaraiKeys"
    ) as JavaClasses.LaraiKeys;
  }

  static get FileList() {
    return JavaTypes.getType(
      "org.lara.interpreter.joptions.keys.FileList"
    ) as JavaClasses.FileList;
  }

  static get OptionalFile() {
    return JavaTypes.getType(
      "org.lara.interpreter.joptions.keys.OptionalFile"
    ) as JavaClasses.OptionalFile;
  }

  static get LaraIUtils() {
    return JavaTypes.getType(
      "org.lara.interpreter.utils.LaraIUtils"
    ) as JavaClasses.LaraIUtils;
  }

  static get WeaverLauncher() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lara.WeaverLauncher"
    ) as JavaClasses.WeaverLauncher;
  }

  static get ArrayList() {
    return JavaTypes.getType("java.util.ArrayList") as JavaClasses.ArrayList;
  }

  static get HashMap() {
    return JavaTypes.getType("java.util.HashMap") as JavaClasses.HashMap;
  }

  static get SpecsPlatforms() {
    return JavaTypes.getType(
      "pt.up.fe.specs.lang.SpecsPlatforms"
    ) as JavaClasses.SpecsPlatforms;
  }

  static get Runtime() {
    return JavaTypes.getType("java.lang.Runtime") as JavaClasses.Runtime;
  }

  static get LARASystem() {
    return JavaTypes.getType("Utils.LARASystem") as JavaClasses.LARASystem;
  }

  static get ProcessOutputAsString() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.system.ProcessOutputAsString"
    ) as JavaClasses.ProcessOutputAsString;
  }

  static get JsGear() {
    return JavaTypes.getType("org.lara.interpreter.utils.JsGear") as {
      new (...args: any[]): JavaClasses.JsGear;
    };
  }

  static get ProgressCounter() {
    return JavaTypes.getType(
      "pt.up.fe.specs.util.utilities.ProgressCounter"
    ) as JavaClasses.ProgressCounter;
  }
}
