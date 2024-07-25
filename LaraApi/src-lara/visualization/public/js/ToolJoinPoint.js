export default class ToolJoinPoint {
    #id;
    #type;
    #code;
    #filepath;
    #info;
    #children;
    constructor(id, type, code, filepath, info, children) {
        this.#id = id;
        this.#type = type;
        this.#filepath = filepath;
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
    get filepath() {
        return this.#filepath;
    }
    get info() {
        return this.#info;
    }
    get children() {
        return this.#children;
    }
    static fromJson(json) {
        return new ToolJoinPoint(json.id, json.type, json.code, json.filepath, json.info, json.children.map((child) => ToolJoinPoint.fromJson(child)));
    }
    toJson() {
        return {
            id: this.#id,
            type: this.#type,
            code: this.#code,
            filepath: this.#filepath,
            info: this.#info,
            children: this.#children.map((child) => child.toJson()),
        };
    }
    clone() {
        return new ToolJoinPoint(this.#id, this.#type, this.#code, this.#filepath, this.#info, this.#children.map((child) => child.clone()));
    }
}
;
//# sourceMappingURL=ToolJoinPoint.js.map