export default class ToolJoinPoint {
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
    static fromJson(json) {
        return new ToolJoinPoint(json.id, json.type, json.code, json.children.map((child) => ToolJoinPoint.fromJson(child)));
    }
    toJson() {
        return {
            id: this.id,
            type: this.type,
            code: this.code,
            children: this.children.map(child => child.toJson()),
        };
    }
    clone() {
        return new ToolJoinPoint(this.id, this.type, this.code, this.children.map(child => child.clone()));
    }
}
;
//# sourceMappingURL=ToolJoinPoint.js.map