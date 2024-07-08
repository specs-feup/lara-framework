import { escapeHtml } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
import JoinPoint from './ToolJoinPoint.js';

const convertAstNodesToElements = (root: JoinPoint, depth: number = 0): HTMLElement[] => {
	const rootElement = document.createElement('span');
	rootElement.classList.add('ast-node');
	rootElement.dataset.nodeId = root.id;
	rootElement.style.marginLeft = (depth * 2) + "em";
	rootElement.textContent = root.type;

	const nodeElements = [rootElement];
	for (const node of root.children) {
		nodeElements.push(...convertAstNodesToElements(node, depth + 1));
	}

	return nodeElements;
};

const fillAstContainer = (nodeElements: HTMLElement[], astContainer: HTMLElement): void => {
	astContainer.innerHTML = '';

	for (const nodeElement of nodeElements) {
		astContainer.appendChild(nodeElement);
		astContainer.appendChild(document.createElement('br'));
	}
}

const linkCodeToAstNodes = (root: JoinPoint, codeContainer: HTMLElement, codeStart: number = 0): number => {
	const nodeElement = document.querySelector<HTMLElement>(`span.ast-node[data-node-id="${root.id}"]`);
	if (nodeElement == null) {
		console.warn(`Node element not found: "${root.id}"`);
		return 0;
	}

	const nodeCode = root.code.trim();
	const nodeCodeHtml = escapeHtml(nodeCode);
	const nodeCodeStart = codeContainer.innerHTML.indexOf(nodeCodeHtml, codeStart);
	if (nodeCodeStart === -1) {
		console.warn(`Node code not found in code container: "${nodeCodeHtml}"`);
		return 0;
	}

	const nodeCodeWrapper = document.createElement('span');
	nodeCodeWrapper.classList.add('node-code');
	nodeCodeWrapper.dataset.nodeId = root.id.toString();
	nodeCodeWrapper.innerHTML = nodeCodeHtml;
	codeContainer.innerHTML = codeContainer.innerHTML.replace(nodeCodeHtml, nodeCodeWrapper.outerHTML);
  // TODO: Associate only the real match (this associates all code fragments that are identical to the node code)

	const nodeCodeContainer = codeContainer.querySelector<HTMLElement>(`span.node-code[data-node-id="${root.id}"]`);
	let nodeCodeLowerBound = 0;
	for (const child of root.children) {
		nodeCodeLowerBound = linkCodeToAstNodes(child, nodeCodeContainer!, nodeCodeLowerBound);
	}

	const codeEnd = nodeCodeStart + nodeCodeWrapper.outerHTML.length;
	return codeEnd;
}

const importCode = (astRoot: JoinPoint, codeContainer: HTMLElement): void => {
  codeContainer.innerHTML = escapeHtml(astRoot.code);
}

const importAst = (astRoot: JoinPoint, astContainer: HTMLElement, codeContainer: HTMLElement): void => {
  const nodeElements = convertAstNodesToElements(astRoot);
  fillAstContainer(nodeElements, astContainer);
  linkCodeToAstNodes(astRoot, codeContainer);
  addEventListenersToAstNodes(nodeElements);
}

export { importCode, importAst };
