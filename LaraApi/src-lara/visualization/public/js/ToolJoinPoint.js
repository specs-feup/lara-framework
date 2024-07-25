export default class ToolJoinPoint {
    #id;
    #type;
    #code;
    #filename;
    #info;
    #children;
    constructor(id, type, code, filename, info, children) {
        this.#id = id;
        this.#type = type;
        this.#filename = filename;
        this.#code = code;
        this.#info = info;
        this.#children = children;
    }
    get id() {
        return this.#id;
    }
    get type() {
        return this.#type;
    }
    get code() {
        return this.#code;
    }
    get filename() {
        return this.#filename;
    }
    get info() {
        return this.#info;
    }
    get children() {
        return this.#children;
    }
    static fromJson(json) {
        return new ToolJoinPoint(json.id, json.type, json.code, json.filename, json.info, json.children.map((child) => ToolJoinPoint.fromJson(child)));
    }
    toJson() {
        return {
            id: this.#id,
            type: this.#type,
            code: this.#code,
            filename: this.#filename,
            info: this.#info,
            children: this.#children.map((child) => child.toJson()),
        };
    }
    clone() {
        return new ToolJoinPoint(this.#id, this.#type, this.#code, this.#filename, this.#info, this.#children.map((child) => child.clone()));
    }
}
;
//# sourceMappingURL=ToolJoinPoint.js.map