const getNodeRelatedElements = (nodeId: string): HTMLElement[] => {
  return Array.from(document.querySelectorAll<HTMLElement>(`[data-node-id="${nodeId}"]`));
}

const highlightNode = (nodeId: string): void => {
  const elements = document.querySelectorAll<HTMLElement>(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`);
  for (const element of elements) {
    element.style.backgroundColor = 'yellow';
  }
}

const unhighlightNode = (nodeId: string): void => {
  const elements = document.querySelectorAll<HTMLElement>(`.ast-node[data-node-id="${nodeId}"] .ast-node-text, .node-code[data-node-id="${nodeId}"]`);
  for (const element of elements) {
    element.style.backgroundColor = '';
  }
}

const addEventListenersToAstNodes = (nodes: HTMLElement[]): void => {
  for (const nodeElement of nodes) {
    if (!nodeElement.dataset.nodeId) {
      console.warn('Node element does not have a data-node-id attribute');
      continue;
    }

    const nodeId = nodeElement.dataset.nodeId!;
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
  }
};

export { addEventListenersToAstNodes };
