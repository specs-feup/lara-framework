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
import JavaTypes from "./lara/util/JavaTypes.js";
export class LaraJoinPoint {
    _javaObject;
    static _defaultAttribute = null;
    constructor(obj) {
        this._javaObject = obj;
    }
    get attributes() { return wrapJoinPoint(this._javaObject.getAttributes()); }
    get selects() { return wrapJoinPoint(this._javaObject.getSelects()); }
    get actions() { return wrapJoinPoint(this._javaObject.getActions()); }
    get dump() { return wrapJoinPoint(this._javaObject.getDump()); }
    get joinPointType() { return wrapJoinPoint(this._javaObject.getJoinPointType()); }
    get node() { return (this._javaObject.getNode()); }
    get self() { return wrapJoinPoint(this._javaObject.getSelf()); }
    get super() { return wrapJoinPoint(this._javaObject.getSuper()); }
    get children() { return wrapJoinPoint(this._javaObject.getChildren()); }
    get descendants() { return wrapJoinPoint(this._javaObject.getDescendants()); }
    get scopeNodes() { return wrapJoinPoint(this._javaObject.getScopeNodes()); }
    insert(p1, p2) { return wrapJoinPoint(this._javaObject.insert(unwrapJoinPoint(p1), unwrapJoinPoint(p2))); }
    def(attribute, value) { return wrapJoinPoint(this._javaObject.def(unwrapJoinPoint(attribute), unwrapJoinPoint(value))); }
    toString() { return wrapJoinPoint(this._javaObject.toString()); }
    equals(jp) { return wrapJoinPoint(this._javaObject.equals(unwrapJoinPoint(jp))); }
    instanceOf(p1) { return wrapJoinPoint(this._javaObject.instanceOf(unwrapJoinPoint(p1))); }
}
const JoinpointMappers = [];
export function registerJoinpointMapper(mapper) {
    JoinpointMappers.push(mapper);
}
/**
 * This function is for internal use only. DO NOT USE IT!
 */
export function clearJoinpointMappers() {
    JoinpointMappers.length = 0;
}
export function wrapJoinPoint(obj) {
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
        return Array.from(obj).map(wrapJoinPoint);
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
    if (JavaTypes.instanceOf(obj, "pt.up.fe.specs.jsengine.node.UndefinedValue")) {
        return undefined;
    }
    if (JavaTypes.instanceOf(obj, "org.suikasoft.jOptions.DataStore.DataClass") &&
        !JavaTypes.instanceOf(obj, "pt.up.fe.specs.clava.ClavaNode")) {
        return obj;
    }
    if (obj.getClass().isEnum()) {
        return obj.toString();
    }
    const isJavaJoinPoint = JavaTypes.JoinPoint.isJoinPoint(obj);
    if (!isJavaJoinPoint) {
        throw new Error(
        // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
        `Given Java join point is a Java class but is not a JoinPoint: ${obj.getClass()}`);
    }
    const jpType = obj.getJoinPointType();
    for (const mapper of JoinpointMappers) {
        if (mapper[jpType]) {
            return new mapper[jpType](obj);
        }
    }
    throw new Error("No mapper found for join point type: " + jpType);
}
export function unwrapJoinPoint(obj) {
    if (obj instanceof LaraJoinPoint) {
        return obj._javaObject;
    }
    if (Array.isArray(obj)) {
        return obj.map(unwrapJoinPoint);
    }
    return obj;
}
//# sourceMappingURL=LaraJoinPoint.js.map