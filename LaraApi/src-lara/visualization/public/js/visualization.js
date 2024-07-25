import { selectFile } from "./files.js";
const getNodeElement = (nodeId) => {
    return document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
};
const getNodeRelatedElements = (nodeId) => {
    return Array.from(document.querySelectorAll(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`));
};
const highlightNode = (nodeId, strong) => {
    const nodeCode = document.querySelectorAll(` .node-code[data-node-id="${nodeId}"]`);
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
const showNodeInfo = (node) => {
    const nodeInfoContainer = document.querySelector('#node-info-container');
    nodeInfoContainer.style.display = 'block';
    nodeInfoContainer.innerHTML = '';
    for (const [name, value] of Object.entries(node.info)) {
        const attributeName = document.createElement('span');
        attributeName.textContent = name + ': ';
        const attributeValue = document.createElement('span');
        attributeValue.textContent = value;
        const line = document.createElement('p');
        line.append(attributeName, attributeValue);
        nodeInfoContainer.appendChild(line);
    }
    if (!document.querySelector(`.node-code[data-node-id="${node.id}"]`)) {
        const codeAlert = document.createElement('p');
        codeAlert.classList.add('alert');
        if (node.code !== undefined) {
            codeAlert.textContent = 'Node code not found:';
            const codeWrapper = document.createElement('pre');
            const code = document.createElement('code');
            code.textContent = node.code;
            codeWrapper.appendChild(code);
            nodeInfoContainer.append(codeAlert, codeWrapper);
        }
        else {
            codeAlert.textContent = 'Node does not have code!';
            nodeInfoContainer.appendChild(codeAlert);
        }
    }
};
const hideNodeInfo = () => {
    const nodeInfoContainer = document.querySelector('#node-info-container');
    nodeInfoContainer.style.display = 'none';
    nodeInfoContainer.innerHTML = '';
};
const scrollIntoViewIfNeeded = (element, parent) => {
    const rect = element.getBoundingClientRect();
    const parentRect = parent.getBoundingClientRect();
    if (rect.bottom < parentRect.top || rect.top > parentRect.bottom) {
        const scrollPos = rect.height <= parentRect.height
            ? (rect.top + rect.bottom - parentRect.top - parentRect.bottom) / 2
            : rect.top - parentRect.top;
        parent.scrollBy({ top: scrollPos, left: 0, behavior: 'smooth' });
    }
};
let selectedNodeId = null;
const addHighlighingEvents = (node, astContainer, codeContainer) => {
    const nodeRelatedElements = getNodeRelatedElements(node.id);
    for (const nodeRelatedElement of nodeRelatedElements) {
        nodeRelatedElement.addEventListener('mouseover', event => {
            highlightNode(node.id, false);
            if (selectedNodeId !== null)
                highlightNode(selectedNodeId, true);
            event.stopPropagation();
        });
        nodeRelatedElement.addEventListener('mouseout', event => {
            unhighlightNode(node.id);
            if (selectedNodeId !== null)
                highlightNode(selectedNodeId, true);
            event.stopPropagation();
        });
        nodeRelatedElement.tabIndex = 0;
        nodeRelatedElement.addEventListener('click', event => {
            event.stopPropagation();
            if (selectedNodeId !== null) {
                unhighlightNode(selectedNodeId);
                if (selectedNodeId === node.id) {
                    selectedNodeId = null;
                    hideNodeInfo();
                    return;
                }
            }
            selectedNodeId = node.id;
            highlightNode(node.id, true);
            if (node.filepath)
                selectFile(node.filepath);
            const nodeElement = getNodeElement(node.id);
            scrollIntoViewIfNeeded(nodeElement, astContainer);
            const firstNodeCodeBlock = document.querySelector(`.node-code[data-node-id="${node.id}"]`);
            if (firstNodeCodeBlock)
                scrollIntoViewIfNeeded(firstNodeCodeBlock, codeContainer);
            showNodeInfo(node);
        });
        // For keyboard accessibility
        nodeRelatedElement.addEventListener('keydown', event => {
            if (event.key === 'Enter') {
                nodeRelatedElement.click();
            }
            event.stopPropagation();
        });
    }
};
const addEventListenersToAstNodes = (root, astContainer, codeContainer) => {
    selectedNodeId = null; // To prevent invalid references
    addHighlighingEvents(root, astContainer, codeContainer);
    root.children.forEach(child => addEventListenersToAstNodes(child, astContainer, codeContainer));
};
const addDividerEventListeners = (resizer, astContainer, codeContainer, continueButton) => {
    let drag = false;
    let width = astContainer.offsetWidth;
    const rootStyle = document.documentElement.style;
    resizer.addEventListener('mousedown', () => {
        drag = true;
    });
    document.addEventListener('mouseup', () => {
        drag = false;
    });
    document.addEventListener('mousemove', event => {
        if (drag) {
            const astLeft = astContainer.getBoundingClientRect().left;
            const maxWidth = codeContainer.getBoundingClientRect().right - astLeft - 160;
            width = event.x - astLeft;
            if (width < continueButton.offsetWidth)
                width = continueButton.offsetWidth;
            else if (width > maxWidth)
                width = maxWidth;
            rootStyle.setProperty('--ast-container-width', `${width}px`);
        }
    });
};
export { addEventListenersToAstNodes, addDividerEventListeners };
//# sourceMappingURL=visualization.js.map