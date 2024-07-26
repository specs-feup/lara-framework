import { countChar } from './utils.js';
import JoinPoint from './ToolJoinPoint.js';
import { createCodeElement, createCodeLines, createCodeWrapper, createNodeDropdown, createNodeDropdownButton, createNodeElement, getActiveCodeElement, getAstContainer, getCodeContainer, getCodeLines, getMainCodeWrapper } from './components.js';

/**
 * @brief Updates the line numbering of the code container.
 */
const updateLines = (): void => {
	const codeLines = getCodeLines();
	const codeWrapper = getMainCodeWrapper();
	if (!codeLines || !codeWrapper)
		throw new Error('Code container not initialized');

	const codeElement = getActiveCodeElement();
  const code = codeElement?.textContent ?? '';

	const numLines = countChar(code, '\n') + 1;
	const newCodeLines = createCodeLines(numLines);
  codeLines.replaceWith(newCodeLines);
};

/**
 * @brief Initializes the code container, by adding the code lines and the code wrapper.
 */
const initCodeContainer = (): void => {
  const codeContainer = getCodeContainer();
	codeContainer.innerHTML = '';

	const codeLines = createCodeLines(0);
	const codeWrapper = createCodeWrapper();

	codeContainer.append(codeLines, codeWrapper);
};

/**
 * @brief Adds a new hidden code element, with the given code, to the code container.
 * 
 * @param code Code of the element
 * @param filepath Path of the file
 */
const addFileCode = (code: string, filepath: string): void => {
  const codeWrapper = getMainCodeWrapper();
  if (!codeWrapper)
    throw new Error('Code container not initialized');

	const codeElement = createCodeElement(code);
  codeElement.dataset.filepath = filepath;
	codeWrapper.appendChild(codeElement);
};

/**
 * @brief Converts the AST to node HTML elements and their respective dropdowns.
 * 
 * @param root Root of the AST
 * @returns The resulting node HTML elements
 */
const toNodeElements = (root: JoinPoint): DocumentFragment => {
	const fragment = new DocumentFragment();

	if (root.children.length > 0) {
		const dropdown = createNodeDropdown(root.id);

		for (const node of root.children) {
			const descendantNodeElements = toNodeElements(node);
			dropdown.appendChild(descendantNodeElements);
		}

    const dropdownButton = createNodeDropdownButton(dropdown);
    const nodeElement = createNodeElement(root.id, root.type, dropdownButton);

		fragment.append(nodeElement, dropdown);
	} else {
    const dropdownButton = createNodeDropdownButton();
    const nodeElement = createNodeElement(root.id, root.type, dropdownButton);

		fragment.appendChild(nodeElement);
	}

	return fragment;
};

/**
 * @brief Imports the AST to the AST container.
 * 
 * @param astRoot Root of the AST
 */
const importAst = (astRoot: JoinPoint): void => {
  const astContainer = getAstContainer();

  const astFragment = toNodeElements(astRoot);
	astContainer.innerHTML = '';
  astContainer.appendChild(astFragment);
};

export { importAst, initCodeContainer, addFileCode, updateLines };
