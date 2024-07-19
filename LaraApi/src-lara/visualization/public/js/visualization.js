const getNodeElement = (nodeId) => {
    return document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
};
const getNodeRelatedElements = (nodeId) => {
    return Array.from(document.querySelectorAll(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`));
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
        parentNodeText.style.backgroundColor = strong ? 'var(--secondary-highlight-color)' : 'var(--tertiary-highlight-color)';
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
                if (selectedNodeId !== null)
                    highlightNode(selectedNodeId, true);
                event.stopPropagation();
            });
            nodeRelatedElement.addEventListener('mouseout', event => {
                unhighlightNode(nodeId);
                if (selectedNodeId !== null)
                    highlightNode(selectedNodeId, true);
                event.stopPropagation();
            });
            nodeRelatedElement.tabIndex = 0;
            nodeRelatedElement.addEventListener('click', event => {
                event.stopPropagation();
                if (selectedNodeId !== null) {
                    unhighlightNode(selectedNodeId);
                    if (selectedNodeId === nodeId) {
                        selectedNodeId = null;
                        return;
                    }
                }
                selectedNodeId = nodeId;
                highlightNode(nodeId, true);
                const nodeElement = getNodeElement(nodeId);
                const firstNodeCodeBlock = document.querySelector('.node-code[data-node-id]');
                for (const element of [nodeElement, firstNodeCodeBlock]) {
                    element.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            });
            nodeRelatedElement.addEventListener('keydown', event => {
                if (event.key === 'Enter') {
                    nodeRelatedElement.click();
                }
                event.stopPropagation();
            });
        }
    };
})();
const addEventListenersToAstNodes = (root) => {
    const nodeId = root.id;
    addHighlighingEvents(nodeId);
    root.children.forEach(child => addEventListenersToAstNodes(child));
};
export { addEventListenersToAstNodes };
//# sourceMappingURL=visualization.js.map