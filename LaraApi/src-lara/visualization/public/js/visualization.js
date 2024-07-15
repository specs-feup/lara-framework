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