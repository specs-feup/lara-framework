import { countChar, createIcon, } from './utils.js';
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

const importCode = (code: string, codeContainer: HTMLElement): void => {
  codeContainer.querySelector('code')!.innerHTML = code;
	
	const numLines = countChar(code, '\n') + 1;
	const codeLines = codeContainer.querySelector<HTMLElement>('.lines')!;
	codeLines.innerText = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
}

const importAst = (astRoot: JoinPoint, astContainer: HTMLElement, codeContainer: HTMLElement): void => {
  const astFragment = convertAstNodeToHtml(astRoot);
	astContainer.innerHTML = '';
  astContainer.appendChild(astFragment);

  addEventListenersToAstNodes(astRoot);
}

export { importCode, importAst };
