/**
 * @file visualization.ts
 * @brief Functions for handling the visualization behavior and events.
 */

import { createCodeElement, createCodeWrapper, createNodeInfoAlert, createNodeInfoLine, getAstContainer, getCodeContainer, getContinueButton, getFileTabsInternalDiv, getFirstNodeCodeElement, getHighlightableElements, getNodeElement, getNodeInfoContainer, getNodeText, getResizer, updateFileTabsArrows } from "./components.js";
import { selectFile } from "./files.js";
import JoinPoint from "./ToolJoinPoint.js";

/**
 * @brief Highlights the node with the given id.
 * 
 * @param nodeId Node id
 * @param strong If the highlight should use a strong color
 */
const highlightNode = (nodeId: string, strong: boolean): void => {
  const nodeElement = getNodeElement(nodeId);
  if (!nodeElement) {
    console.warn(`There is no node with id ${nodeId}`);
    return;
  }

  const highlightableElements = getHighlightableElements(nodeId);
  highlightableElements.forEach(elem => elem.style.backgroundColor = strong ? 'var(--highlight-color)' : 'var(--secondary-highlight-color)');

  let parentNode = nodeElement.parentElement?.previousSibling;
  while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
    const parentNodeId = parentNode.dataset.nodeId!
    const parentNodeText = getNodeText(parentNodeId)!;
    parentNodeText.style.backgroundColor = strong ? 'var(--secondary-highlight-color)' : 'var(--tertiary-highlight-color)';

    parentNode = parentNode.parentElement?.previousSibling;
  }
};

/**
 * @brief Unhighlights the node with the given id.
 * 
 * @param nodeId Node id
 */
const unhighlightNode = (nodeId: string): void => {
  const nodeElement = getNodeElement(nodeId);
  if (!nodeElement) {
    console.warn(`There is no node with id ${nodeId}`);
    return;
  }

  const highlightableElements = getHighlightableElements(nodeId)!;
  highlightableElements.forEach(elem => elem.style.backgroundColor = '');

  let parentNode = nodeElement.parentElement?.previousSibling;
  while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
    const parentNodeId = parentNode.dataset.nodeId!
    const parentNodeText = getNodeText(parentNodeId)!;
    parentNodeText.style.backgroundColor = '';
    
    parentNode = parentNode.parentElement?.previousSibling;
  }
};

/**
 * @brief Shows the node info container with the given node information.
 * 
 * @param node The target node
 */
const showNodeInfo = (node: JoinPoint): void => {
  const nodeInfoContainer = getNodeInfoContainer();
  nodeInfoContainer.style.display = 'block';
  nodeInfoContainer.innerHTML = ''

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
    } else {
      const alert = createNodeInfoAlert('Node does not have code!');
      nodeInfoContainer.appendChild(alert);
    }
  }
};

/**
 * @brief Hides the node information container.
 */
const hideNodeInfo = (): void => {
  const nodeInfoContainer = getNodeInfoContainer();
  nodeInfoContainer.style.display = 'none';
  nodeInfoContainer.innerHTML = '';
};

const scrollIntoViewIfNeeded = (element: HTMLElement, parent: HTMLElement): void => {
  const rect = element.getBoundingClientRect();
  const parentRect = parent.getBoundingClientRect();

  if (rect.bottom < parentRect.top || rect.top > parentRect.bottom) {
    const scrollPos = rect.height <= parentRect.height
      ? (rect.top + rect.bottom - parentRect.top - parentRect.bottom) / 2
      : rect.top - parentRect.top;
    
    parent.scrollBy({ top: scrollPos, left: 0, behavior: 'smooth' });
  }
};


let selectedNodeId: string | null = null;

const highlightableOnMouseOver = (node: JoinPoint, event: Event): void => {
  highlightNode(node.id, false);
  if (selectedNodeId !== null)
    highlightNode(selectedNodeId, true);
  event.stopPropagation();
};

const highlightableOnMouseOut = (node: JoinPoint, event: Event): void => {
  unhighlightNode(node.id);
  if (selectedNodeId !== null)
    highlightNode(selectedNodeId, true);
  event.stopPropagation();
};

const highlightableOnClick = (node: JoinPoint, event: Event): void => {
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


  const nodeElement = getNodeElement(node.id)!;
  const astContainer = getAstContainer();
  scrollIntoViewIfNeeded(nodeElement, astContainer);

  const firstNodeCodeBlock = getFirstNodeCodeElement(node.id);
  if (firstNodeCodeBlock) {
    const codeContainer = getCodeContainer();
    scrollIntoViewIfNeeded(firstNodeCodeBlock!, codeContainer);
  }

  showNodeInfo(node);
};

/**
 * @brief Adds event listeners to all the highlightable elements relative to
 * the nodes in the given AST.
 * 
 * @param root Root of the AST
 */
const addHighlighingEventListeners = (root: JoinPoint): void => {
  const addListeners = (node: JoinPoint) => {
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
  }

  selectedNodeId = null;  // To prevent invalid references
  addListeners(root);
};

/**
 * @brief Adds event listeners to the resizer element.
 */
const addResizerEventListeners = (): void => {
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

      if (getFileTabsInternalDiv())
        updateFileTabsArrows();
    }
  });
};

export { addHighlighingEventListeners, addResizerEventListeners };
