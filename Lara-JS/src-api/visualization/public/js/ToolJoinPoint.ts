export default class ToolJoinPoint {
  id: string;
  type: string;
  code: string;
  children: ToolJoinPoint[];

  constructor(id: string, type: string, code: string, children: ToolJoinPoint[]) {
    this.id = id;
    this.type = type;
    this.code = code;
    this.children = children;
  }

  public static fromJson(json: any): ToolJoinPoint {
    return new ToolJoinPoint(
      json.id,
      json.type,
      json.code,
      json.children.map((child: any) => ToolJoinPoint.fromJson(child)),
    );
  }

  public toJson(): any {
    return {
      id: this.id,
      type: this.type,
      code: this.code,
      children: this.children.map(child => child.toJson()),
    };
  }

  public clone(): ToolJoinPoint {
    return new ToolJoinPoint(this.id, this.type, this.code, this.children.map(child => child.clone()));
  }
};