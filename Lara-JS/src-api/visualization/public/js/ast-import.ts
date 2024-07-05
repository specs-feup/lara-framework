import { escapeHtml } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';

const createAstNodeElements = (ast: any): HTMLElement[] => {
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
		nodeElement.dataset.nodeId = (id++).toString();
		nodeElement.style.marginLeft = (indentation.length / 2) + "em";
		nodeElement.textContent = nodeName;

		nodeElements.push(nodeElement);
	}

	return nodeElements;
};

const fillAstContainer = (nodeElements: HTMLElement[], astContainer: HTMLElement): void => {
	for (const nodeElement of nodeElements) {
		astContainer.appendChild(nodeElement);
		astContainer.appendChild(document.createElement('br'));
	}
}

const linkCodeToAstNodes = (nodeElements: HTMLElement[], codeContainer: HTMLElement): void => {
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

const importAst = (ast: any, astContainer: HTMLElement, codeContainer: HTMLElement): void => {
  const nodeElements = createAstNodeElements(ast);
  fillAstContainer(nodeElements, astContainer);
  linkCodeToAstNodes(nodeElements, codeContainer);
  addEventListenersToAstNodes(nodeElements);
}

export { importAst };
