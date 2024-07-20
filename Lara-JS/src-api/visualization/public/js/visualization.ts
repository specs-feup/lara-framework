import JoinPoint, { JoinPointInfo } from "./ToolJoinPoint.js";

const getNodeElement = (nodeId: string): HTMLElement | null => {
  return document.querySelector<HTMLElement>(`.ast-node[data-node-id="${nodeId}"]`);
}

const getNodeRelatedElements = (nodeId: string): HTMLElement[] => {
  return Array.from(document.querySelectorAll<HTMLElement>(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`));
};

const highlightNode = (nodeId: string, strong: boolean): void => {
  const nodeCode = document.querySelectorAll<HTMLElement>(`.node-code[data-node-id="${nodeId}"]`)!;
  nodeCode.forEach(elem => elem.style.backgroundColor = strong ? 'var(--highlight-color)' : 'var(--secondary-highlight-color)');

  const nodeElement = document.querySelector<HTMLElement>(`.ast-node[data-node-id="${nodeId}"]`)!;
  const nodeText = nodeElement.querySelector<HTMLElement>('.ast-node-text')!;
  nodeText.style.backgroundColor = strong ? 'var(--highlight-color)' : 'var(--secondary-highlight-color)';

  let parentNode = nodeElement.parentElement?.previousSibling;
  while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
    const parentNodeText = parentNode.querySelector<HTMLElement>('.ast-node-text')!;
    parentNodeText.style.backgroundColor = strong ? 'var(--secondary-highlight-color)' : 'var(--tertiary-highlight-color)';

    parentNode = parentNode.parentElement?.previousSibling;
  }
};

const unhighlightNode = (nodeId: string): void => {
  const nodeCode = document.querySelectorAll<HTMLElement>(`.node-code[data-node-id="${nodeId}"]`)!;
  nodeCode.forEach(elem => elem.style.backgroundColor = '');

  const nodeElement = document.querySelector<HTMLElement>(`.ast-node[data-node-id="${nodeId}"]`)!;
  const nodeText = nodeElement.querySelector<HTMLElement>('.ast-node-text')!;
  nodeText.style.backgroundColor = '';

  let parentNode = nodeElement.parentElement?.previousSibling as HTMLElement | null | undefined;
  while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
    const parentNodeText = parentNode.querySelector<HTMLElement>('.ast-node-text')!;
    parentNodeText.style.backgroundColor = '';
    
    parentNode = parentNode.parentElement?.previousSibling as HTMLElement | null | undefined;
  }
};

const showNodeInfo = (nodeInfo: JoinPointInfo): void => {
  const nodeInfoContainer = document.querySelector<HTMLElement>('#node-info-container')!;
  nodeInfoContainer.style.display = 'block';
  nodeInfoContainer.innerHTML = ''

  for (const [name, value] of Object.entries(nodeInfo)) {
    const attributeName = document.createElement('span');
    attributeName.textContent = name;

    const attributeValue = document.createElement('span');
    attributeValue.textContent = value;

    const line = document.createElement('p');
    line.append(attributeName, attributeValue);
    nodeInfoContainer.appendChild(line);
  }
}

const hideNodeInfo = (): void => {
  const nodeInfoContainer = document.querySelector<HTMLElement>('#node-info-container')!;
  nodeInfoContainer.style.display = 'none';
  nodeInfoContainer.innerHTML = '';
}

const addHighlighingEvents = (() => {
  let selectedNodeId: string | null = null;
  return (node: JoinPoint): void => {
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

        const nodeElement = getNodeElement(node.id)!;
        const firstNodeCodeBlock = document.querySelector<HTMLElement>(`.node-code[data-node-id="${node.id}"]`);
        for (const element of [nodeElement, firstNodeCodeBlock]) {
          element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }

        showNodeInfo(node.info);
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
})();

const addEventListenersToAstNodes = (root: JoinPoint): void => {
  const nodeId = root.id;

  addHighlighingEvents(root);
  root.children.forEach(child => addEventListenersToAstNodes(child));
};

export { addEventListenersToAstNodes };
