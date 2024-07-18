import { countChar, createIcon, escapeHtml, replaceAfter } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
import JoinPoint from './ToolJoinPoint.js';

const createAstNodeElement = (nodeId: string, text: string): HTMLSpanElement => {
	const nodeElement = document.createElement('span');
	nodeElement.classList.add('ast-node');

	nodeElement.dataset.nodeId = nodeId;

	const chevronDropdownButton = document.createElement('button');
	chevronDropdownButton.appendChild(createIcon('keyboard_arrow_down'));

	const nodeText = document.createElement('span');
	nodeText.classList.add('ast-node-text');
	nodeText.textContent = text;

	nodeElement.appendChild(chevronDropdownButton);
	nodeElement.appendChild(nodeText);

	return nodeElement;
}

const createAstNodeDropdown = (nodeId: string): HTMLDivElement => {
	const dropdown = document.createElement('div');
	dropdown.classList.add('ast-node-dropdown');
	dropdown.dataset.nodeId = nodeId;

	return dropdown;
}

const convertAstNodeToHtml = (root: JoinPoint): DocumentFragment => {
	const rootElement = createAstNodeElement(root.id, root.type);
	const rootDropdown = createAstNodeDropdown(root.id);

	for (const node of root.children) {
		const descendantNodeElements = convertAstNodeToHtml(node);
		rootDropdown.appendChild(descendantNodeElements);
	}

	const fragment = new DocumentFragment();
	fragment.appendChild(rootElement);
	fragment.appendChild(rootDropdown);

	return fragment;
};

const linkCodeToAstNodes = (root: JoinPoint, codeElement: HTMLElement, codeStart: number = 0): number => {
	const nodeElement = document.querySelector<HTMLElement>(`span.ast-node[data-node-id="${root.id}"]`);
	if (nodeElement == null) {
		console.warn(`Node element not found: "${root.id}"`);
		return 0;
	}

	const nodeCode = root.code.trim();
	const nodeCodeHtml = escapeHtml(nodeCode);
	const nodeCodeStart = codeElement.innerHTML.indexOf(nodeCodeHtml, codeStart);
	if (nodeCodeStart === -1) {
		console.warn(`Node code not found in code container: "${nodeCodeHtml}"`);
		return 0;
	}

	const nodeCodeWrapper = document.createElement('span');
	nodeCodeWrapper.classList.add('node-code');
	nodeCodeWrapper.dataset.nodeId = root.id;
	nodeCodeWrapper.innerHTML = nodeCodeHtml;
	codeElement.innerHTML = replaceAfter(codeElement.innerHTML, nodeCodeHtml, nodeCodeWrapper.outerHTML, codeStart);

	const nodeCodeContainer = codeElement.querySelector<HTMLElement>(`span.node-code[data-node-id="${root.id}"]`);
	let nodeCodeLowerBound = 0;
	for (const child of root.children) {
		nodeCodeLowerBound = linkCodeToAstNodes(child, nodeCodeContainer!, nodeCodeLowerBound);
	}

	const codeEnd = nodeCodeStart + nodeCodeContainer!.outerHTML.length;
	return codeEnd;
}

const importCode = (astRoot: JoinPoint, codeContainer: HTMLElement): void => {
	const trimedCode = astRoot.code.trim();
  codeContainer.querySelector('code')!.innerHTML = escapeHtml(trimedCode);
	
	const numLines = countChar(trimedCode, '\n') + 1;
	const codeLines = codeContainer.querySelector<HTMLElement>('.lines')!;
	codeLines.innerText = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
}

const importAst = (astRoot: JoinPoint, astContainer: HTMLElement, codeContainer: HTMLElement): void => {
  const astFragment = convertAstNodeToHtml(astRoot);
	astContainer.innerHTML = '';
  astContainer.appendChild(astFragment);

  linkCodeToAstNodes(astRoot, codeContainer.querySelector('code')!);
  addEventListenersToAstNodes(astRoot);
}

export { importCode, importAst };
