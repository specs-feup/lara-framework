export type JoinPointInfo = { [attribute: string]: string };

export default class ToolJoinPoint {
  #id: string;
  #type: string;
  #code: string | undefined;
  #filename: string | undefined;
  #info: JoinPointInfo;
  #children: ToolJoinPoint[];

  constructor(id: string, type: string, code: string | undefined, filename: string | undefined, info: JoinPointInfo, children: ToolJoinPoint[]) {
    this.#id = id;
    this.#type = type;
    this.#filename = filename;
    this.#code = code;
    this.#info = info;
    this.#children = children;
  }

  get id(): string {
    return this.#id;
  }

  get type(): string {
    return this.#type;
  }

  get code(): string | undefined {
    return this.#code;
  }

  get filename(): string | undefined {
    return this.#filename;
  }

  get info(): JoinPointInfo {
    return this.#info;
  }

  get children(): ToolJoinPoint[] {
    return this.#children
  }

  public static fromJson(json: any): ToolJoinPoint {
    return new ToolJoinPoint(
      json.id,
      json.type,
      json.code,
      json.filename,
      json.info,
      json.children.map((child: any) => ToolJoinPoint.fromJson(child))
    );
  }

  public toJson(): any {
    return {
      id: this.#id,
      type: this.#type,
      code: this.#code,
      filename: this.#filename,
      info: this.#info,
      children: this.#children.map((child) => child.toJson()),
    };
  }

  public clone(): ToolJoinPoint {
    return new ToolJoinPoint(
      this.#id,
      this.#type,
      this.#code,
      this.#filename,
      this.#info,
      this.#children.map((child) => child.clone())
    );
  }
};