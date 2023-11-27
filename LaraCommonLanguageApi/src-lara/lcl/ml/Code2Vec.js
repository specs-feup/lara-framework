laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");

class Code2Vec {
  printPaths() {
    // Collect information
    for (var desc of Query.search("file").search()) {
      if (!desc.hasChildren) {
        //for all the leafs of the tree
        this.#AST(desc.parent, [desc]);
      }
    }
  }

  #getNodeInfo(node) {
    return node.joinPointType ?? node.toString();
  }

  #AST(node, lst) {
    // return AST path context starting from a leaf
    lst.push("(up)");
    lst.push(node);
    if (node.parent != undefined && !lst.includes(node.parent)) {
      this.#AST(node.parent, lst);
    }
    this.#goDown(node, lst);
    lst.pop();
    lst.pop();
  }

  #goDown(parent, node_list) {
    //go down in the ast to find the paths to the leafs
    if (!parent.hasChildren) {
      console.log(
        ...node_list.map((node) => {
          return `${this.#getNodeInfo(node)} `;
        })
      );

      console.log("__________________________________");
      return;
    } else {
      for (var node of parent.children) {
        var previousNode = node_list[node_list.length - 3];
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
