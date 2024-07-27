//////////////////////////////////////////////////////
// This file is generated by build-LaraJoinPoint.js //
//////////////////////////////////////////////////////

/* eslint-disable @typescript-eslint/ban-types */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/no-duplicate-type-constituents */

import java from "java";
import JavaTypes, { Engine, engine } from "./lara/util/JavaTypes.js";

/**
 * Type for type equality assertion. If T is equal to U, return Y, otherwise return N.
 * Source: https://github.com/Microsoft/TypeScript/issues/27024#issuecomment-421529650
 * @example
 * type A = Equals<string, string, "Y", "N">; // "Y"
 * type B = Equals<string, number, "Y", "N">; // "N"
 */
type Equals<T, U, Y = unknown, N = never> =
  (<G>() => G extends T ? 1 : 2) extends
  (<G>() => G extends U ? 1 : 2) ? Y : N;

type DefaultAttributeHelper<
  T extends typeof LaraJoinPoint,
  DefaultAttributeMap,
  PrivateMapper,
> = {
  [K in keyof DefaultAttributeMap]: K extends keyof PrivateMapper
    ? Equals<T, PrivateMapper[K], DefaultAttributeMap[K], never>
    : never;
}[keyof DefaultAttributeMap];

// Extract the type A from A | undefined
type ExtractedType<T> = T extends undefined ? never : T;

export type DefaultAttribute<T extends typeof LaraJoinPoint> = DefaultAttributeHelper<
  T,
  ExtractedType<T["_defaultAttributeInfo"]["map"]>,
  ExtractedType<T["_defaultAttributeInfo"]["type"]>
>;

type NameFromWrapperClassHelper<T extends typeof LaraJoinPoint, U> = {
  [K in keyof U]: Equals<T, U[K], K, never>;
}[keyof U];

export type NameFromWrapperClass<T extends typeof LaraJoinPoint> = NameFromWrapperClassHelper<
  T,
  ExtractedType<T["_defaultAttributeInfo"]["jpMapper"]>
>;

export class LaraJoinPoint {
  /**
   * @internal
   */
  static readonly _defaultAttributeInfo: {readonly map?: any, readonly name: string | null, readonly type?: any, readonly jpMapper?: any} = {
    name: null,
  };
  /**
   * @internal
   */
  _javaObject!: any;
  constructor(obj: any) {
    this._javaObject = obj;
  }
  get attributes(): string[] { return wrapJoinPoint(this._javaObject.getAttributes()) }
  get selects(): string[] { return wrapJoinPoint(this._javaObject.getSelects()) }
  get actions(): string[] { return wrapJoinPoint(this._javaObject.getActions()) }
  get dump(): string { return wrapJoinPoint(this._javaObject.getDump()) }
  get joinPointType(): string { return wrapJoinPoint(this._javaObject.getJoinPointType()) }
  get node(): object { return (this._javaObject.getNode()) }
  get self(): LaraJoinPoint { return wrapJoinPoint(this._javaObject.getSelf()) }
  get super(): LaraJoinPoint { return wrapJoinPoint(this._javaObject.getSuper()) }
  get children(): LaraJoinPoint[] { return wrapJoinPoint(this._javaObject.getChildren()) }
  get descendants(): LaraJoinPoint[] { return wrapJoinPoint(this._javaObject.getDescendants()) }
  get scopeNodes(): LaraJoinPoint[] { return wrapJoinPoint(this._javaObject.getScopeNodes()) }
  insert(position: "before" | "after" | "replace", code: string): LaraJoinPoint;
  insert(position: "before" | "after" | "replace", joinpoint: LaraJoinPoint): LaraJoinPoint;
  insert(p1: "before" | "after" | "replace", p2: string | LaraJoinPoint): LaraJoinPoint { return wrapJoinPoint(this._javaObject.insert(unwrapJoinPoint(p1), unwrapJoinPoint(p2))); }
  def(attribute: string, value: object): void { return wrapJoinPoint(this._javaObject.def(unwrapJoinPoint(attribute), unwrapJoinPoint(value))); }
  toString(): string { return wrapJoinPoint(this._javaObject.toString()); }
  equals(jp: LaraJoinPoint): boolean { return wrapJoinPoint(this._javaObject.equals(unwrapJoinPoint(jp))); }
  instanceOf(name: string): boolean;
  instanceOf(names: string[]): boolean;
  instanceOf(p1: string | string[]): boolean { return wrapJoinPoint(this._javaObject.instanceOf(unwrapJoinPoint(p1))); }
}


export type JoinpointMapperType = { [key: string]: typeof LaraJoinPoint };

const JoinpointMappers: JoinpointMapperType[] = [];

export function registerJoinpointMapper(mapper: JoinpointMapperType): void {
  JoinpointMappers.push(mapper);
}

/**
 * This function is for internal use only. DO NOT USE IT!
 */
export function clearJoinpointMappers(): void {
  JoinpointMappers.length = 0;
}

/**
 * This function is for internal use only. DO NOT USE IT!
 */
export function getJoinpointMappers(): JoinpointMapperType[] {
  return JoinpointMappers;
}

export function wrapJoinPoint(obj: any): any {
  if (JoinpointMappers.length === 0) {
    return obj;
  }

  if (obj === undefined) {
    return obj;
  }

  if (obj instanceof LaraJoinPoint) {
    return obj;
  }

  if (ArrayBuffer.isView(obj)) {
    return Array.from(obj as any).map(wrapJoinPoint);
  }

  if (typeof obj !== "object") {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(wrapJoinPoint);
  }

  if (!JavaTypes.isJavaObject(obj)) {
    return obj;
  }

  if (
    JavaTypes.instanceOf(obj, "pt.up.fe.specs.jsengine.node.UndefinedValue")
  ) {
    return undefined;
  }

  if (
    JavaTypes.instanceOf(obj, "org.suikasoft.jOptions.DataStore.DataClass") &&
    !JavaTypes.instanceOf(obj, "pt.up.fe.specs.clava.ClavaNode")
  ) {
    return obj;
  }

  if (obj.getClass().isEnum()) {
    return obj.toString();
  }

  const isJavaJoinPoint = JavaTypes.JoinPoint.isJoinPoint(obj);
  if (!isJavaJoinPoint) {
    throw new Error(
      // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
      `Given Java join point is a Java class but is not a JoinPoint: ${obj.getClass()}`
    );
  }

  const jpType: string = obj.getJoinPointType();
  for (const mapper of JoinpointMappers) {
    if (mapper[jpType]) {
      return new mapper[jpType](obj);
    }
  }
  throw new Error("No mapper found for join point type: " + jpType);
}

export function unwrapJoinPoint(obj: any): any {
  if (obj instanceof LaraJoinPoint) {
    return obj._javaObject;
  }

  if (Array.isArray(obj)) {
    if (engine == Engine.NodeJS) {
      const isJpArray = obj.reduce((prev, curr) => {
          return prev && curr instanceof LaraJoinPoint;
      }, true);

      const getClassName = (jp: LaraJoinPoint) =>
          Object.getPrototypeOf(jp._javaObject).constructor.name;

      if (isJpArray) {
        const clazz = (
            obj.map(getClassName).reduce((prev, curr) => {
                if (prev != curr) {
                    return undefined;
                }
                return prev;
            }) ?? "java.lang.Object"
        )
            .replace("nodeJava_", "")
            .replaceAll("_", ".");
        
        return java.newArray(clazz, obj.map(unwrapJoinPoint));
      }
    }

    return obj.map(unwrapJoinPoint);
  }

  return obj;
}
