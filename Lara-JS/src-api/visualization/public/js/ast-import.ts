import { escapeHtml } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';

const createAstNodeElements = (ast: any): HTMLElement[] => {
	let id = 0;  // TODO: Refactor identification (e.g. using astId)
	const nodeElements = [];

	for (const node of ast.split('\n')) {
		const matches = node.match(/^(\s*)Joinpoint '(.+)'$/);
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
		const nodeCode = nodeElement.textContent;  // TODO: Use real node code
		if (nodeCode == null) {
			console.warn(`Node with null code: "${nodeCode}"`);
			continue;
		}

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

const importCode = (ast: any, codeContainer: HTMLElement): void => {
  codeContainer.textContent = ast;
}

const importAst = (ast: any, astContainer: HTMLElement, codeContainer: HTMLElement): void => {
  const nodeElements = createAstNodeElements(ast);
  fillAstContainer(nodeElements, astContainer);
  linkCodeToAstNodes(nodeElements, codeContainer);
  addEventListenersToAstNodes(nodeElements);
}

export { importCode, importAst };
