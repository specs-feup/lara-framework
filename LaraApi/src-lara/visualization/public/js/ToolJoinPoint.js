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
    /**
     * @brief Returns the join point ID.
     */
    get id() {
        return this.#id;
    }
    /**
     * @brief Returns the type of join point.
     */
    get type() {
        return this.#type;
    }
    /**
     * @brief Returns the code of the join point.
     */
    get code() {
        return this.#code;
    }
    /**
     * @brief Returns the filepath of the file this join point belongs to.
     */
    get filepath() {
        return this.#filepath;
    }
    /**
     * @brief Returns extra information about the join point.
     */
    get info() {
        return this.#info;
    }
    /**
     * @brief Returns the children of the join point.
     */
    get children() {
        return this.#children;
    }
    /**
     * @brief Creates a new ToolJoinPoint object from a JSON object.
     */
    static fromJson(json) {
        return new ToolJoinPoint(json.id, json.type, json.code, json.filepath, json.info, json.children.map((child) => ToolJoinPoint.fromJson(child)));
    }
    /**
     * @brief Converts the ToolJoinPoint object to a JSON object.
     */
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
    /**
     * @brief Clones the join point.
     */
    clone() {
        return new ToolJoinPoint(this.#id, this.#type, this.#code, this.#filepath, this.#info, this.#children.map((child) => child.clone()));
    }
}
;
//# sourceMappingURL=ToolJoinPoint.js.map