export type JoinPointInfo = { [attribute: string]: string };

export default class ToolJoinPoint {
  #id: string;
  #type: string;
  #code: string | undefined;
  #filepath: string | undefined;
  #info: JoinPointInfo;
  #children: ToolJoinPoint[];

  constructor(id: string, type: string, code: string | undefined, filepath: string | undefined, info: JoinPointInfo, children: ToolJoinPoint[]) {
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
  get id(): string {
    return this.#id;
  }

  /**
   * @brief Returns the type of join point.
   */
  get type(): string {
    return this.#type;
  }

  /**
   * @brief Returns the code of the join point.
   */
  get code(): string | undefined {
    return this.#code;
  }

  /**
   * @brief Returns the filepath of the file this join point belongs to.
   */
  get filepath(): string | undefined {
    return this.#filepath;
  }

  /**
   * @brief Returns extra information about the join point.
   */
  get info(): JoinPointInfo {
    return this.#info;
  }

  /**
   * @brief Returns the children of the join point.
   */
  get children(): ToolJoinPoint[] {
    return this.#children
  }

  /**
   * @brief Creates a new ToolJoinPoint object from a JSON object.
   */
  public static fromJson(json: any): ToolJoinPoint {
    return new ToolJoinPoint(
      json.id,
      json.type,
      json.code,
      json.filepath,
      json.info,
      json.children.map((child: any) => ToolJoinPoint.fromJson(child))
    );
  }

  /**
   * @brief Converts the ToolJoinPoint object to a JSON object.
   */
  public toJson(): any {
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
  public clone(): ToolJoinPoint {
    return new ToolJoinPoint(
      this.#id,
      this.#type,
      this.#code,
      this.#filepath,
      this.#info,
      this.#children.map((child) => child.clone())
    );
  }
};