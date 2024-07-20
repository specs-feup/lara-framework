export default class ToolJoinPoint {
    #id;
    #type;
    #info;
    #children;
    constructor(id, type, info, children) {
        this.#id = id;
        this.#type = type;
        this.#info = info;
        this.#children = children;
    }
    get id() {
        return this.#id;
    }
    get type() {
        return this.#type;
    }
    get info() {
        return this.#info;
    }
    get children() {
        return this.#children;
    }
    static fromJson(json) {
        return new ToolJoinPoint(json.id, json.type, json.info, json.children.map((child) => ToolJoinPoint.fromJson(child)));
    }
    toJson() {
        return {
            id: this.#id,
            type: this.#type,
            info: this.#info,
            children: this.#children.map((child) => child.toJson()),
        };
    }
    clone() {
        return new ToolJoinPoint(this.#id, this.#type, this.#info, this.#children.map((child) => child.clone()));
    }
}
;
//# sourceMappingURL=ToolJoinPoint.js.map