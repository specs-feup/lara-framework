const createAstNodeElements = (ast) => {
	let id = 0;  // TODO: Refactor identification (e.g. using astId)
	const nodeElements = [];

	for (const node of ast.split('\n')) {
		const matches = node.match(/(\s*)Joinpoint '(.+)'/, '');
		if (matches == null) {
			console.warn(`Invalid node: "${node}"`);
			continue;
		}
		const [, indentation, nodeName] = matches;
		
		const nodeElement = document.createElement('span');
		nodeElement.classList.add('ast-node');  // TODO: Add joinpoint info
		nodeElement.dataset.nodeId = id++;
		nodeElement.style.marginLeft = (indentation.length / 2) + "em";
		nodeElement.textContent = nodeName;

		nodeElements.push(nodeElement);
	}

	return nodeElements;
};

const fillAstContainer = (nodeElements, astContainer) => {
	for (const nodeElement of nodeElements) {
		astContainer.appendChild(nodeElement);
		astContainer.appendChild(document.createElement('br'));
	}
}

const linkCodeToAstNodes = (nodeElements, codeContainer) => {
	for (const nodeElement of nodeElements) {
		const nodeCode = `void matrix_mult(double const *A, double const *B, double *C, int const N, int const M, int const K) {
   for(int ii = 0; ii < N; ii++) {
      for(int jj = 0; jj < K; jj++) {
         //C[i][j] = 0;
         C[K * ii + jj] = 0;
      }
   }
   for(int i = 0; i < N; i++) {
      for(int l = 0; l < M; l++) {
         for(int j = 0; j < K; j++) {
            //C[i][j] += A[i][l]*B[l][j];
            C[K * i + j] += A[M * i + l] * B[K * l + j];
         }
      }
   }
}`;  // TODO: Use real node code
    const nodeCodeHtml = escapeHtml(nodeCode);

		const nodeCodeStart = codeContainer.innerHTML.indexOf(nodeCodeHtml);
		if (nodeCodeStart === -1) {
			console.warn(`Node code not found in code container: "${nodeCode}"`);
			continue;
		}

		const nodeCodeWrapper = document.createElement('span');
		nodeCodeWrapper.classList.add('node-code');
		nodeCodeWrapper.dataset.nodeId = nodeElement.dataset.nodeId;
    nodeCodeWrapper.textContent = nodeCode;
		
		codeContainer.innerHTML = codeContainer.innerHTML.replaceAll(nodeCodeHtml, nodeCodeWrapper.outerHTML);
    // TODO: Associate only the real match (this associates all code fragments that are identical to the node code)
	}
}


(function() {
	const astContainer = document.querySelector('#ast code');
	const codeContainer = document.querySelector('#code code');

	const sampleAst = `Joinpoint 'program'
    Joinpoint 'file'
        Joinpoint 'include'
        Joinpoint 'include'
        Joinpoint 'include'
        Joinpoint 'comment'
        Joinpoint 'comment'
        Joinpoint 'function'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'body'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'wrapperStmt'
                            Joinpoint 'comment'
                            Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                                Joinpoint 'arrayAccess'
                                    Joinpoint 'varref'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                Joinpoint 'intLiteral'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'loop'
                            Joinpoint 'declStmt'
                                Joinpoint 'vardecl'
                                    Joinpoint 'intLiteral'
                            Joinpoint 'exprStmt'
                                Joinpoint 'binaryOp'
                                    Joinpoint 'varref'
                                    Joinpoint 'varref'
                            Joinpoint 'exprStmt'
                                Joinpoint 'unaryOp'
                                    Joinpoint 'varref'
                            Joinpoint 'body'
                                Joinpoint 'wrapperStmt'
                                    Joinpoint 'comment'
                                Joinpoint 'exprStmt'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'arrayAccess'
                                        Joinpoint 'varref'
                                        Joinpoint 'binaryOp'
                                            Joinpoint 'binaryOp'
                                                Joinpoint 'varref'
                                                Joinpoint 'varref'
                                            Joinpoint 'varref'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'arrayAccess'
                                            Joinpoint 'varref'
                                            Joinpoint 'binaryOp'
                                                Joinpoint 'binaryOp'
                                                    Joinpoint 'varref'
                                                    Joinpoint 'varref'
                                                Joinpoint 'varref'
                                        Joinpoint 'arrayAccess'
                                            Joinpoint 'varref'
                                            Joinpoint 'binaryOp'
                                                Joinpoint 'binaryOp'
                                                    Joinpoint 'varref'
                                                    Joinpoint 'varref'
                                                Joinpoint 'varref'
        Joinpoint 'comment'
        Joinpoint 'function'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'body'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'wrapperStmt'
                            Joinpoint 'comment'
                            Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                                Joinpoint 'arrayAccess'
                                    Joinpoint 'varref'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                Joinpoint 'binaryOp'
                                    Joinpoint 'parenExpr'
                                        Joinpoint 'cast'
                                        Joinpoint 'call'
                                            Joinpoint 'varref'
                                    Joinpoint 'cast'
                                        Joinpoint 'intLiteral'
        Joinpoint 'function'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'body'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'floatLiteral'
                Joinpoint 'if'
                Joinpoint 'binaryOp'
                    Joinpoint 'varref'
                    Joinpoint 'intLiteral'
                Joinpoint 'body'
                    Joinpoint 'exprStmt'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'intLiteral'
                            Joinpoint 'varref'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'wrapperStmt'
                            Joinpoint 'comment'
                            Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'arrayAccess'
                                    Joinpoint 'varref'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'literal'
                    Joinpoint 'varref'
        Joinpoint 'function'
            Joinpoint 'body'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'intLiteral'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'intLiteral'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'intLiteral'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'cast'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'varref'
                            Joinpoint 'unaryExprOrType'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'cast'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'varref'
                            Joinpoint 'unaryExprOrType'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'cast'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'varref'
                            Joinpoint 'unaryExprOrType'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
        Joinpoint 'function'
            Joinpoint 'body'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'`;  // TODO: Import from Lara

  if (astContainer != null) {
    const nodeElements = createAstNodeElements(sampleAst);
    fillAstContainer(nodeElements, astContainer);
    linkCodeToAstNodes(nodeElements, codeContainer);
  }
})();
