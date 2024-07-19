import { countChar, createIcon, } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
const createAstNodeDropdownButton = (nodeId) => {
    const dropdownButton = document.createElement('button');
    const chevronIcon = createIcon("keyboard_arrow_down");
    dropdownButton.appendChild(chevronIcon);
    return dropdownButton;
};
const createDropdownButtonOnClick = (dropdown) => {
    let nodeCollapsed = false;
    return (event) => {
        nodeCollapsed = !nodeCollapsed;
        dropdown.style.display = nodeCollapsed ? "none" : "block";
        const dropdownButton = event.currentTarget;
        const chevronIcon = dropdownButton.children[0];
        chevronIcon.textContent = nodeCollapsed ? "keyboard_arrow_right" : "keyboard_arrow_down";
        event.stopPropagation();
    };
};
const createAstNodeElement = (nodeId, text, dropdownButton) => {
    const nodeElement = document.createElement('span');
    nodeElement.classList.add('ast-node');
    nodeElement.dataset.nodeId = nodeId;
    const nodeText = document.createElement('span');
    nodeText.classList.add('ast-node-text');
    nodeText.textContent = text;
    nodeElement.appendChild(dropdownButton);
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
    const fragment = new DocumentFragment();
    const dropdownButton = createAstNodeDropdownButton(root.id);
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
    }
    else {
        dropdownButton.disabled = true;
        fragment.appendChild(rootElement);
    }
    return fragment;
};
const importCode = (code, codeContainer) => {
    codeContainer.querySelector('code').innerHTML = code;
    const numLines = countChar(code, '\n') + 1;
    const codeLines = codeContainer.querySelector('.lines');
    codeLines.innerText = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
};
const importAst = (astRoot, astContainer) => {
    const astFragment = convertAstNodeToHtml(astRoot);
    astContainer.innerHTML = '';
    astContainer.appendChild(astFragment);
    addEventListenersToAstNodes(astRoot);
};
export { importCode, importAst };
//# sourceMappingURL=ast-import.js.map