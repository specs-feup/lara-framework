class ToolJoinPoint {
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

  public static fromJSON(json: any): ToolJoinPoint {
    return new ToolJoinPoint(
      json.id,
      json.type,
      json.code,
      json.children.map((child: any) => ToolJoinPoint.fromJSON(child)),
    );
  }
};

export default ToolJoinPoint;