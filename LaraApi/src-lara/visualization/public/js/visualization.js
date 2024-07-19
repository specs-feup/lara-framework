const getNodeElement = (nodeId) => {
    return document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
};
const getNodeDropdown = (nodeId) => {
    return document.querySelector(`.ast-node-dropdown[data-node-id="${nodeId}"]`);
};
const getNodeRelatedElements = (nodeId) => {
    return Array.from(document.querySelectorAll(`.ast-node[data-node-id="${nodeId}"], .node-code[data-node-id="${nodeId}"]`));
};
const highlightNode = (nodeId, strong) => {
    const nodeCode = document.querySelectorAll(`.node-code[data-node-id="${nodeId}"]`);
    nodeCode.forEach(elem => elem.style.backgroundColor = strong ? 'var(--highlight-color)' : 'var(--secondary-highlight-color)');
    const nodeElement = document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
    const nodeText = nodeElement.querySelector('.ast-node-text');
    nodeText.style.backgroundColor = strong ? 'var(--highlight-color)' : 'var(--secondary-highlight-color)';
    let parentNode = nodeElement.parentElement?.previousSibling;
    while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
        const parentNodeText = parentNode.querySelector('.ast-node-text');
        parentNodeText.style.backgroundColor = 'var(--secondary-highlight-color)';
        parentNode = parentNode.parentElement?.previousSibling;
    }
};
const unhighlightNode = (nodeId) => {
    const nodeCode = document.querySelectorAll(`.node-code[data-node-id="${nodeId}"]`);
    nodeCode.forEach(elem => elem.style.backgroundColor = '');
    const nodeElement = document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
    const nodeText = nodeElement.querySelector('.ast-node-text');
    nodeText.style.backgroundColor = '';
    let parentNode = nodeElement.parentElement?.previousSibling;
    while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
        const parentNodeText = parentNode.querySelector('.ast-node-text');
        parentNodeText.style.backgroundColor = '';
        parentNode = parentNode.parentElement?.previousSibling;
    }
};
const addHighlighingEvents = (() => {
    let selectedNodeId = null;
    return (nodeId) => {
        const nodeRelatedElements = getNodeRelatedElements(nodeId);
        for (const nodeRelatedElement of nodeRelatedElements) {
            nodeRelatedElement.addEventListener('mouseover', event => {
                highlightNode(nodeId, false);
                event.stopPropagation();
            });
            nodeRelatedElement.addEventListener('mouseout', event => {
                unhighlightNode(nodeId);
                if (selectedNodeId !== null)
                    highlightNode(selectedNodeId, true);
                event.stopPropagation();
            });
            nodeRelatedElement.addEventListener('click', event => {
                if (selectedNodeId !== null) {
                    unhighlightNode(selectedNodeId);
                }
                selectedNodeId = nodeId;
                highlightNode(nodeId, true);
                for (const nodeRelatedElement of nodeRelatedElements.slice(0, 2)) {
                    nodeRelatedElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
                event.stopPropagation();
            });
        }
    };
})();
const addEventListenersToAstNodes = (root) => {
    const nodeId = root.id;
    const nodeElement = getNodeElement(nodeId);
    const nodeDropdownButton = nodeElement.children[0];
    const nodeDropdown = getNodeDropdown(nodeId);
    let nodeCollapsed = false;
    nodeDropdownButton.addEventListener('click', event => {
        nodeCollapsed = !nodeCollapsed;
        nodeDropdown.style.display = nodeCollapsed ? 'none' : 'block';
        const chevron = nodeDropdownButton.children[0];
        chevron.textContent = nodeCollapsed ? 'keyboard_arrow_right' : 'keyboard_arrow_down';
        event.stopPropagation();
    });
    addHighlighingEvents(nodeId);
    root.children.forEach(child => addEventListenersToAstNodes(child));
};
export { addEventListenersToAstNodes };
//# sourceMappingURL=visualization.js.map