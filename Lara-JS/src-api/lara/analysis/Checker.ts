export default class Checker {
  name: string;

  constructor(name: string) {
    this.name = name;
  }

  check(node: any) {
    throw "Not Implemented";
  }

  getName() {
    return this.name;
  }
}
