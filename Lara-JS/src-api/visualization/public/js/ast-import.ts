import { countChar, createIcon, } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
import JoinPoint from './ToolJoinPoint.js';

const createAstNodeDropdownButton = (): HTMLButtonElement => {
	const dropdownButton = document.createElement('button');

  const chevronIcon = createIcon("keyboard_arrow_down");
  dropdownButton.appendChild(chevronIcon);

	return dropdownButton;
};

const createDropdownButtonOnClick = (dropdown: HTMLElement) => {
	let nodeCollapsed = false;
	return (event: Event): void => {
		nodeCollapsed = !nodeCollapsed;
		dropdown.style.display = nodeCollapsed ? "none" : "block";

		const dropdownButton = event.currentTarget as HTMLElement;
		const chevronIcon = dropdownButton.children[0] as HTMLElement;
		chevronIcon.textContent = nodeCollapsed ? "keyboard_arrow_right" : "keyboard_arrow_down";

		event.stopPropagation();
	};
};

const createAstNodeElement = (nodeId: string, text: string, dropdownButton: HTMLElement): HTMLSpanElement => {
	const nodeElement = document.createElement('span');
	nodeElement.classList.add('ast-node');

	nodeElement.dataset.nodeId = nodeId;

	const nodeText = document.createElement('span');
	nodeText.classList.add('ast-node-text');
	nodeText.textContent = text;

	nodeElement.appendChild(dropdownButton);
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
	const fragment = new DocumentFragment();
	const dropdownButton = createAstNodeDropdownButton();
	const rootElement = createAstNodeElement(root.id, root.type, dropdownButton);

	if (root.children.length > 0) {
		const rootDropdown = createAstNodeDropdown(root.id);

		for (const node of root.children) {
			const descendantNodeElements = convertAstNodeToHtml(node);
			rootDropdown.appendChild(descendantNodeElements);
		}

    dropdownButton.addEventListener('click', createDropdownButtonOnClick(rootDropdown));

		fragment.appendChild(rootElement);
		fragment.appendChild(rootDropdown);
	} else {
		dropdownButton.disabled = true;

		fragment.appendChild(rootElement);
	}

	return fragment;
};

const importCode = (code: string, codeContainer: HTMLElement): void => {
  codeContainer.querySelector('code')!.innerHTML = code;
	
	const numLines = countChar(code, '\n') + 1;
	const codeLines = codeContainer.querySelector<HTMLElement>('.lines')!;
	codeLines.innerText = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
}

const importAst = (astRoot: JoinPoint, astContainer: HTMLElement): void => {
  const astFragment = convertAstNodeToHtml(astRoot);
	astContainer.innerHTML = '';
  astContainer.appendChild(astFragment);

  addEventListenersToAstNodes(astRoot);
}

export { importCode, importAst };
