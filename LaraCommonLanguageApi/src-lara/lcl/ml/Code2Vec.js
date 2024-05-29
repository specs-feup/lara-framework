laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class Code2Vec {
  /**
   * Collect information
   */
  printPaths() {
    for (const desc of Query.search("file").search("joinpoint")) {
      if (!desc.hasChildren) {
        //for all the leafs of the tree
        this.#AST(desc.parent, [desc]);
      }
    }
  }

  #getNodeInfo(node) {
    return node.joinPointType ?? node.toString();
  }

  /**
   * Returns AST path context starting from a leaf
   *
   * @param {*} node
   * @param {[]} lst
   */
  #AST(node, lst) {
    lst.push("(up)");
    lst.push(node);
    if (
      node.parent != undefined &&
      lst.find((e) => {
        typeof e != "string" && e.equals(node.parent);
      })
    ) {
      this.#AST(node.parent, lst);
    }
    this.#goDown(node, lst);
    lst.pop();
    lst.pop();
  }

  /**
   * Go down in the ast to find the paths to the leafs
   *
   * @param {*} parent
   * @param {[]} node_list
   * @returns
   */
  #goDown(parent, node_list) {
    if (!parent.hasChildren) {
      console.log(...node_list.map((node) => this.#getNodeInfo(node)));
      console.log("__________________________________");
      return;
    } else {
      for (const node of parent.children) {
        const previousNode = node_list[node_list.length - 3];
        if (!previousNode.equals(node)) {
          node_list.push("(down)");
          node_list.push(node);
          this.#goDown(node, node_list);
          node_list.pop();
          node_list.pop();
        }
      }
    }
  }
}
