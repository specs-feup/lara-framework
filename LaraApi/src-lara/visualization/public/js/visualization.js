import { createCodeElement, createCodeWrapper, createNodeInfoAlert, createNodeInfoLine, getAstContainer, getCodeContainer, getContinueButton, getFirstNodeCodeElement, getHighlightableElements, getNodeElement, getNodeInfoContainer, getNodeText, getResizer } from "./components.js";
import { selectFile } from "./files.js";
const highlightNode = (nodeId, strong) => {
    const nodeElement = getNodeElement(nodeId);
    if (!nodeElement) {
        console.warn(`There is no node with id ${nodeId}`);
        return;
    }
    const highlightableElements = getHighlightableElements(nodeId);
    highlightableElements.forEach(elem => elem.style.backgroundColor = strong ? 'var(--highlight-color)' : 'var(--secondary-highlight-color)');
    let parentNode = nodeElement.parentElement?.previousSibling;
    while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
        const parentNodeId = parentNode.dataset.nodeId;
        const parentNodeText = getNodeText(parentNodeId);
        parentNodeText.style.backgroundColor = strong ? 'var(--secondary-highlight-color)' : 'var(--tertiary-highlight-color)';
        parentNode = parentNode.parentElement?.previousSibling;
    }
};
const unhighlightNode = (nodeId) => {
    const nodeElement = getNodeElement(nodeId);
    if (!nodeElement) {
        console.warn(`There is no node with id ${nodeId}`);
        return;
    }
    const highlightableElements = getHighlightableElements(nodeId);
    highlightableElements.forEach(elem => elem.style.backgroundColor = '');
    let parentNode = nodeElement.parentElement?.previousSibling;
    while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
        const parentNodeId = parentNode.dataset.nodeId;
        const parentNodeText = getNodeText(parentNodeId);
        parentNodeText.style.backgroundColor = '';
        parentNode = parentNode.parentElement?.previousSibling;
    }
};
const showNodeInfo = (node) => {
    const nodeInfoContainer = getNodeInfoContainer();
    nodeInfoContainer.style.display = 'block';
    nodeInfoContainer.innerHTML = '';
    for (const [name, value] of Object.entries(node.info)) {
        const line = createNodeInfoLine(name, value);
        nodeInfoContainer.appendChild(line);
    }
    const hasCode = getFirstNodeCodeElement(node.id) !== null;
    if (!hasCode) {
        if (node.code) {
            const alert = createNodeInfoAlert('Node code not found:');
            const codeElement = createCodeElement(node.code);
            const codeWrapper = createCodeWrapper();
            codeWrapper.appendChild(codeElement);
            nodeInfoContainer.append(alert, codeWrapper);
        }
        else {
            const alert = createNodeInfoAlert('Node does not have code!');
            nodeInfoContainer.appendChild(alert);
        }
    }
};
const hideNodeInfo = () => {
    const nodeInfoContainer = getNodeInfoContainer();
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
const highlightableOnMouseOver = (node, event) => {
    highlightNode(node.id, false);
    if (selectedNodeId !== null)
        highlightNode(selectedNodeId, true);
    event.stopPropagation();
};
const highlightableOnMouseOut = (node, event) => {
    unhighlightNode(node.id);
    if (selectedNodeId !== null)
        highlightNode(selectedNodeId, true);
    event.stopPropagation();
};
const highlightableOnClick = (node, event) => {
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
    const astContainer = getAstContainer();
    scrollIntoViewIfNeeded(nodeElement, astContainer);
    const firstNodeCodeBlock = getFirstNodeCodeElement(node.id);
    if (firstNodeCodeBlock) {
        const codeContainer = getCodeContainer();
        scrollIntoViewIfNeeded(firstNodeCodeBlock, codeContainer);
    }
    showNodeInfo(node);
};
const addHighlighingEventListeners = (root) => {
    const addListeners = (node) => {
        const highlightableElements = getHighlightableElements(node.id);
        for (const element of highlightableElements) {
            element.addEventListener('mouseover', event => highlightableOnMouseOver(node, event));
            element.addEventListener('mouseout', event => highlightableOnMouseOut(node, event));
            element.tabIndex = 0;
            element.addEventListener('click', event => highlightableOnClick(node, event));
            // For keyboard accessibility
            element.addEventListener('keydown', event => {
                if (event.key === 'Enter') {
                    element.click();
                }
                event.stopPropagation();
            });
        }
        node.children.forEach(child => addListeners(child));
    };
    selectedNodeId = null; // To prevent invalid references
    addListeners(root);
};
const addDividerEventListeners = () => {
    const resizer = getResizer();
    const astContainer = getAstContainer();
    const codeContainer = getCodeContainer();
    const continueButton = getContinueButton();
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
            const minWidth = continueButton.offsetWidth;
            const maxWidth = codeContainer.getBoundingClientRect().right - astLeft - 160;
            width = event.x - astLeft;
            if (width < minWidth)
                width = minWidth;
            else if (width > maxWidth)
                width = maxWidth;
            rootStyle.setProperty('--ast-container-width', `${width}px`);
        }
    });
};
export { addHighlighingEventListeners, addDividerEventListeners };
//# sourceMappingURL=visualization.js.map