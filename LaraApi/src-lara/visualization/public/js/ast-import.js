import { escapeHtml, replaceAfter } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
const convertAstNodesToElements = (root, depth = 0) => {
    const rootElement = document.createElement('span');
    rootElement.classList.add('ast-node');
    rootElement.dataset.nodeId = root.id;
    rootElement.style.marginLeft = (depth * 2) + 'em';
    rootElement.textContent = root.type;
    const nodeElements = [rootElement];
    for (const node of root.children) {
        nodeElements.push(...convertAstNodesToElements(node, depth + 1));
    }
    return nodeElements;
};
const fillAstContainer = (nodeElements, astContainer) => {
    astContainer.innerHTML = '';
    for (const nodeElement of nodeElements) {
        astContainer.appendChild(nodeElement);
        astContainer.appendChild(document.createElement('br'));
    }
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
const linkCodeToAstNodes = (root, codeContainer, codeStart = 0) => {
    const nodeElement = document.querySelector(`span.ast-node[data-node-id="${root.id}"]`);
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
    // TODO: Associate only the real match (this associates all code fragments that are identical to the node code)
    const nodeCodeContainer = codeContainer.querySelector(`span.node-code[data-node-id="${root.id}"]`);
    let nodeCodeLowerBound = 0;
    for (const child of root.children) {
        nodeCodeLowerBound = linkCodeToAstNodes(child, nodeCodeContainer, nodeCodeLowerBound);
    }
    const codeEnd = nodeCodeStart + nodeCodeContainer.outerHTML.length;
    return codeEnd;
};
const importCode = (astRoot, codeContainer) => {
    codeContainer.innerHTML = escapeHtml(astRoot.code);
};
const importAst = (astRoot, astContainer, codeContainer) => {
    const refinedAstRoot = astRoot.clone();
    refineAst(refinedAstRoot);
    const nodeElements = convertAstNodesToElements(refinedAstRoot);
    fillAstContainer(nodeElements, astContainer);
    linkCodeToAstNodes(refinedAstRoot, codeContainer);
    addEventListenersToAstNodes(nodeElements);
};
export { importCode, importAst };
//# sourceMappingURL=ast-import.js.map