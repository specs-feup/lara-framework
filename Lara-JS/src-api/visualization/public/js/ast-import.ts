import { createLucideIcon, escapeHtml, replaceAfter } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
import JoinPoint from './ToolJoinPoint.js';

const createAstNodeElement = (nodeId: string, text: string): HTMLSpanElement => {
	const nodeElement = document.createElement('span');
	nodeElement.classList.add('ast-node');

	nodeElement.dataset.nodeId = nodeId;

	const chevronDropdownButton = document.createElement('button');
	chevronDropdownButton.appendChild(createLucideIcon('chevron-down'));

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

const addIdentation = (code: string, indentation: number): string => {
	return code.split('\n').map((line, i) => i > 0 ? '   '.repeat(indentation) + line : line).join('\n');
}

const refineAst = (root: JoinPoint, indentation: number = 0): void => {
	root.code = addIdentation(root.code.trim(), indentation);

	const children = root.children;
	if (['WhileStmt', 'DoStmt', 'ForStmt'].includes(root.type)) {
		children
			.filter(child => child.type === 'ExprStmt')
			.forEach(child => child.code = child.code.slice(0, -1));	// Remove semicolon from expression statements inside loop parentheses
	}

	if (root.type == 'DeclStmt') {
		root.children
			.slice(1)
			.forEach(child => {
				child.code = child.code.match(/(?:\S+\s+)(\S.*)/)![1];
			});  // Remove type from variable declarations
	}


	for (const child of root.children) {
		refineAst(child, root.type === 'CompoundStmt' ? indentation + 1 : indentation);
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
	codeContainer.innerHTML = replaceAfter(codeContainer.innerHTML, nodeCodeHtml, nodeCodeWrapper.outerHTML, codeStart);

	const nodeCodeContainer = codeContainer.querySelector<HTMLElement>(`span.node-code[data-node-id="${root.id}"]`);
	let nodeCodeLowerBound = 0;
	for (const child of root.children) {
		nodeCodeLowerBound = linkCodeToAstNodes(child, nodeCodeContainer!, nodeCodeLowerBound);
	}

	const codeEnd = nodeCodeStart + nodeCodeContainer!.outerHTML.length;
	return codeEnd;
}

const importCode = (astRoot: JoinPoint, codeContainer: HTMLElement): void => {
  codeContainer.innerHTML = escapeHtml(astRoot.code);
}

const importAst = (astRoot: JoinPoint, astContainer: HTMLElement, codeContainer: HTMLElement): void => {
	const refinedAstRoot = astRoot.clone();
	refineAst(refinedAstRoot);

  const astFragment = convertAstNodeToHtml(refinedAstRoot);
	astContainer.innerHTML = '';
  astContainer.appendChild(astFragment);

  linkCodeToAstNodes(refinedAstRoot, codeContainer);
  addEventListenersToAstNodes(refinedAstRoot);
}

export { importCode, importAst };
