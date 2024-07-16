import { countChar, createLucideIcon, escapeHtml, replaceAfter } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
const createAstNodeElement = (nodeId, text) => {
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
};
const createAstNodeDropdown = (nodeId) => {
    const dropdown = document.createElement('div');
    dropdown.classList.add('ast-node-dropdown');
    dropdown.dataset.nodeId = nodeId;
    return dropdown;
};
const convertAstNodeToHtml = (root) => {
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
const addIdentation = (code, indentation) => {
    return code.split('\n').map((line, i) => i > 0 ? '   '.repeat(indentation) + line : line).join('\n');
};
const refineAst = (root, indentation = 0) => {
    root.code = addIdentation(root.code.trim(), indentation);
    const children = root.children;
    if (['WhileStmt', 'DoStmt', 'ForStmt'].includes(root.type)) {
        children
            .filter(child => child.type === 'ExprStmt')
            .forEach(child => child.code = child.code.slice(0, -1)); // Remove semicolon from expression statements inside loop parentheses
    }
    if (root.type == 'DeclStmt') {
        root.children
            .slice(1)
            .forEach(child => {
            child.code = child.code.match(/(?:\S+\s+)(\S.*)/)[1];
        }); // Remove type from variable declarations
    }
    for (const child of root.children) {
        refineAst(child, root.type === 'CompoundStmt' ? indentation + 1 : indentation);
    }
};
const linkCodeToAstNodes = (root, codeElement, codeStart = 0) => {
    const nodeElement = document.querySelector(`span.ast-node[data-node-id="${root.id}"]`);
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
    const nodeCodeContainer = codeElement.querySelector(`span.node-code[data-node-id="${root.id}"]`);
    let nodeCodeLowerBound = 0;
    for (const child of root.children) {
        nodeCodeLowerBound = linkCodeToAstNodes(child, nodeCodeContainer, nodeCodeLowerBound);
    }
    const codeEnd = nodeCodeStart + nodeCodeContainer.outerHTML.length;
    return codeEnd;
};
const importCode = (astRoot, codeContainer) => {
    const trimedCode = astRoot.code.trim();
    codeContainer.querySelector('code').innerHTML = escapeHtml(trimedCode);
    const numLines = countChar(trimedCode, '\n') + 1;
    const codeLines = codeContainer.querySelector('.lines');
    codeLines.textContent = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
};
const importAst = (astRoot, astContainer, codeContainer) => {
    const refinedAstRoot = astRoot.clone();
    refineAst(refinedAstRoot);
    const astFragment = convertAstNodeToHtml(refinedAstRoot);
    astContainer.innerHTML = '';
    astContainer.appendChild(astFragment);
    linkCodeToAstNodes(refinedAstRoot, codeContainer.querySelector('code'));
    addEventListenersToAstNodes(refinedAstRoot);
};
export { importCode, importAst };
//# sourceMappingURL=ast-import.js.map