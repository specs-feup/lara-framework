const getNodeElement = (nodeId) => {
    return document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
};
const getNodeDropdown = (nodeId) => {
    return document.querySelector(`.ast-node-dropdown[data-node-id="${nodeId}"]`);
};
const getNodeRelatedElements = (nodeId) => {
    return Array.from(document.querySelectorAll(`.ast-node[data-node-id="${nodeId}"], .node-code[data-node-id="${nodeId}"]`));
};
const highlightNode = (nodeId) => {
    const elements = document.querySelectorAll(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`);
    for (const element of elements) {
        element.style.backgroundColor = 'yellow';
    }
};
const unhighlightNode = (nodeId) => {
    const elements = document.querySelectorAll(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`);
    for (const element of elements) {
        element.style.backgroundColor = '';
    }
};
const addEventListenersToAstNodes = (root) => {
    const nodeId = root.id;
    const nodeElement = getNodeElement(nodeId);
    const nodeDropdownButton = nodeElement.children[0];
    const nodeDropdown = getNodeDropdown(nodeId);
    let nodeCollapsed = false;
    nodeDropdownButton.addEventListener('click', () => {
        nodeCollapsed = !nodeCollapsed;
        nodeDropdown.style.display = nodeCollapsed ? 'none' : 'block';
        const chevron = nodeDropdownButton.children[0];
        chevron.textContent = nodeCollapsed ? 'keyboard_arrow_right' : 'keyboard_arrow_down';
    });
    const nodeRelatedElements = getNodeRelatedElements(nodeId);
    for (const nodeRelatedElement of nodeRelatedElements) {
        nodeRelatedElement.addEventListener('mouseover', event => {
            highlightNode(nodeId);
            event.stopPropagation();
        });
        nodeRelatedElement.addEventListener('mouseout', event => {
            unhighlightNode(nodeId);
            event.stopPropagation();
        });
    }
    root.children.forEach(child => addEventListenersToAstNodes(child));
};
export { addEventListenersToAstNodes };
//# sourceMappingURL=visualization.js.map