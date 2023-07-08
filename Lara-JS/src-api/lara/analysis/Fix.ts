export default class Fix {
  node: any;
  fixAction: any;

  constructor(node: any, fixAction: any) {
    this.node = node;
    this.fixAction = fixAction;
  }

  getNode() {
    return this.node;
  }

  execute() {
    this.fixAction(this.node);
  }
}
