import { countChar, createIcon, } from './utils.js';
import { addEventListenersToAstNodes } from './visualization.js';
const [getSelectedFilename, selectFile] = (() => {
    let selectedFilename = null;
    const getSelectedFilename = () => selectedFilename;
    const selectFile = (filename, code, codeContainer, astRoot, astContainer) => {
        if (filename !== selectedFilename) {
            importCode(code, codeContainer);
            importAst(astRoot, astContainer, astContainer);
            console.log(filename);
            document.querySelectorAll('.file-tab').forEach(tab => tab.classList.remove('active'));
            document.querySelector(`.file-tab[data-filename="${filename}"]`).classList.add('active');
        }
    };
    return [getSelectedFilename, selectFile];
})();
const createFileTab = (filename, code, codeContainer, astRoot, astContainer) => {
    const fileTab = document.createElement('div');
    fileTab.classList.add('file-tab');
    fileTab.dataset.filename = filename;
    fileTab.textContent = filename;
    fileTab.addEventListener('click', () => selectFile(filename, code, codeContainer, astRoot, astContainer));
    return fileTab;
};
const importCode = (code, codeContainer) => {
    codeContainer.innerHTML = '';
    const codeLines = document.createElement('pre');
    codeLines.classList.add('lines');
    codeContainer.appendChild(codeLines);
    const codeWrapper = document.createElement('pre');
    const codeElement = document.createElement('code');
    codeElement.innerHTML = code;
    codeWrapper.appendChild(codeElement);
    codeContainer.appendChild(codeWrapper);
    const numLines = countChar(code, '\n') + 1;
    codeLines.textContent = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
};
const createAstNodeDropdownButton = () => {
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
    }
    else {
        dropdownButton.disabled = true;
        fragment.appendChild(rootElement);
    }
    return fragment;
};
const importAst = (astRoot, astContainer, codeContainer) => {
    const astFragment = convertAstNodeToHtml(astRoot);
    astContainer.innerHTML = '';
    astContainer.appendChild(astFragment);
    addEventListenersToAstNodes(astRoot, astContainer, codeContainer);
};
export { getSelectedFilename, createFileTab, importAst };
//# sourceMappingURL=ast-import.js.map