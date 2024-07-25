import { countChar, createIcon, } from './utils.js';
import JoinPoint from './ToolJoinPoint.js';
import { getAstContainer, getCodeContainer } from './components.js';

const updateLines = (): void => {
	const codeContainer = getCodeContainer();
	const codeLines = codeContainer.querySelector('.lines')!;
	const code = codeContainer.querySelector('code.active')!.textContent!;

	const numLines = countChar(code, '\n') + 1;
	codeLines.textContent = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
};

const initCodeContainer = (): void => {
  const codeContainer = getCodeContainer();
	codeContainer.innerHTML = '';

	const codePre = document.createElement('pre');
	codePre.classList.add('lines');

	const codeLines = document.createElement('pre');
	codeLines.classList.add('code-wrapper');

	codeContainer.append(codePre, codeLines)
}

const addCode = (code: string, filepath: string): void => {
	const codeElement = document.createElement('code');
  codeElement.dataset.filepath = filepath;
  codeElement.innerHTML = code;

	const codeContainer = getCodeContainer();
	const codePre = codeContainer.querySelector('pre.code-wrapper')!;
	codePre.appendChild(codeElement);
}

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

const importAst = (astRoot: JoinPoint): void => {
  const astContainer = getAstContainer();

  const astFragment = convertAstNodeToHtml(astRoot);
	astContainer.innerHTML = '';
  astContainer.appendChild(astFragment);
}

export { importAst, initCodeContainer, addCode, updateLines };
