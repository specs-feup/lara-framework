export default class ToolJoinPoint {
  id: string;
  type: string;
  children: ToolJoinPoint[];

  constructor(id: string, type: string, children: ToolJoinPoint[]) {
    this.id = id;
    this.type = type;
    this.children = children;
  }

  public static fromJson(json: any): ToolJoinPoint {
    return new ToolJoinPoint(
      json.id,
      json.type,
      json.children.map((child: any) => ToolJoinPoint.fromJson(child)),
    );
  }

  public toJson(): any {
    return {
      id: this.id,
      type: this.type,
      children: this.children.map(child => child.toJson()),
    };
  }

  public clone(): ToolJoinPoint {
    return new ToolJoinPoint(this.id, this.type, this.children.map(child => child.clone()));
  }
};