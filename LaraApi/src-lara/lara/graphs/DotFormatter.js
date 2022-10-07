class DotFormatter {
  #nodeAttrs;

  #edgeAttrs;

  constructor() {
    this.#nodeAttrs = [];
    this.#edgeAttrs = [];
  }

  addNodeAttribute(attrString, predicate) {
    if (predicate === undefined) {
      predicate = (node) => true;
    }

    this.#nodeAttrs.push({ attr: attrString, predicate: predicate });
  }

  addEdgeAttribute(attrString, predicate) {
    if (predicate === undefined) {
      predicate = (edge) => true;
    }

    this.#edgeAttrs.push({ attr: attrString, predicate: predicate });
  }

  getNodeAttributes(node) {
    return this.#nodeAttrs
      .filter((obj) => obj.predicate(node))
      .map((obj) => obj.attr)
      .join(" ");
  }

  getEdgeAttributes(edge) {
    return this.#edgeAttrs
      .filter((obj) => obj.predicate(edge))
      .map((obj) => obj.attr)
      .join(" ");
  }
}
