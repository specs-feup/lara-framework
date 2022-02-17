laraImport("lcl.LaraCommonLanguage");
laraImport("weaver.Query");


class Code2Vec {

	//#currentPath;

	constructor() {
		//this.#currentPath = undefined;
	}

	printPaths() {

		// Collect information 
		for (var desc of Query.search("file").search()) {
			if (!desc.hasChildren) { //for all the leafs of the tree
				this.#AST(desc.parent, [desc]);
			}
		}
	}



	#getNodeInfo(node) {
		var type = node.joinPointType;

		if (type === undefined) {
			type = node.toString();
		}

		return type;
	}

	#AST(node, lst) {                      // return AST path context starting from a leaf
		lst.push('(up)');
		lst.push(node);
		if (node.astParent != undefined && !lst.includes(node.astParent)) {
			this.#AST(node.astParent, lst);
		}
		this.#goDown(node, lst);
		lst.pop();
		lst.pop();
	}


	#goDown(parent, node_list) { //go down in the ast to find the paths to the leafs
		if (!parent.hasChildren) {
			for (var node of node_list) {
				print(this.#getNodeInfo(node) + " ");
				/*
				if (node.code != undefined)
					println(node.code);
				else
					println(node);
				*/
			}
			println("\n__________________________________");
			return;
		}
		else {
			for (var node of parent.children) {
				var previousNode = node_list[node_list.length - 3];
				if (!(previousNode.equals(node))) {
					node_list.push('(down)');
					node_list.push(node);
					this.#goDown(node, node_list);
					node_list.pop();
					node_list.pop();
				}
			}

		}
	}

}



