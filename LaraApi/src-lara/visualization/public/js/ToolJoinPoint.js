class ToolJoinPoint {
    id;
    type;
    code;
    children;
    constructor(id, type, code, children) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.children = children;
    }
    static fromJSON(json) {
        return new ToolJoinPoint(json.id, json.type, json.code, json.children.map((child) => ToolJoinPoint.fromJSON(child)));
    }
    clone() {
        return new ToolJoinPoint(this.id, this.type, this.code, this.children.map(child => child.clone()));
    }
}
;
export default ToolJoinPoint;
//# sourceMappingURL=ToolJoinPoint.js.map