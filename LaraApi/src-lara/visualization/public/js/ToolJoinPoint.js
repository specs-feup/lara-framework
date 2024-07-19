export default class ToolJoinPoint {
    #id;
    #type;
    #children;
    constructor(id, type, children) {
        this.#id = id;
        this.#type = type;
        this.#children = children;
    }
    get id() {
        return this.#id;
    }
    get type() {
        return this.#type;
    }
    get children() {
        return this.#children;
    }
    static fromJson(json) {
        return new ToolJoinPoint(json.id, json.type, json.children.map((child) => ToolJoinPoint.fromJson(child)));
    }
    toJson() {
        return {
            id: this.#id,
            type: this.#type,
            children: this.#children.map((child) => child.toJson()),
        };
    }
    clone() {
        return new ToolJoinPoint(this.#id, this.#type, this.#children.map((child) => child.clone()));
    }
}
;
//# sourceMappingURL=ToolJoinPoint.js.map