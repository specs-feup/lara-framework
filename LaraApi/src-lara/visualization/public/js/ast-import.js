import { countChar } from './utils.js';
import { createCodeElement, createCodeLines, createCodeWrapper, createNodeDropdown, createNodeDropdownButton, createNodeElement, getActiveCodeElement, getAstContainer, getCodeContainer, getCodeLines, getMainCodeWrapper } from './components.js';
const updateLines = () => {
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
const initCodeContainer = () => {
    const codeContainer = getCodeContainer();
    codeContainer.innerHTML = '';
    const codeLines = createCodeLines(0);
    const codeWrapper = createCodeWrapper();
    codeContainer.append(codeLines, codeWrapper);
};
const addCode = (code, filepath) => {
    const codeWrapper = getMainCodeWrapper();
    if (!codeWrapper)
        throw new Error('Code container not initialized');
    const codeElement = createCodeElement(code);
    codeElement.dataset.filepath = filepath;
    codeWrapper.appendChild(codeElement);
};
const toNodeElements = (root) => {
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
    }
    else {
        const dropdownButton = createNodeDropdownButton();
        const nodeElement = createNodeElement(root.id, root.type, dropdownButton);
        fragment.appendChild(nodeElement);
    }
    return fragment;
};
const importAst = (astRoot) => {
    const astContainer = getAstContainer();
    const astFragment = toNodeElements(astRoot);
    astContainer.innerHTML = '';
    astContainer.appendChild(astFragment);
};
export { importAst, initCodeContainer, addCode, updateLines };
//# sourceMappingURL=ast-import.js.map