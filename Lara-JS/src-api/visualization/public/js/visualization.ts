const getElementsWithNodeId = (id: string): NodeListOf<HTMLElement> => {
  return document.querySelectorAll(`span[data-node-id="${id}"]`);
}

const highlightElements = (elements: NodeListOf<HTMLElement>): void => {
  elements.forEach(element => element.style.backgroundColor = 'yellow');
};

const unhighlightElements = (elements: NodeListOf<HTMLElement>): void => {
  elements.forEach(element => element.style.backgroundColor = '');
}

const addEventListenersToAstNodes = (nodes: HTMLElement[]): void => {
  for (const nodeElement of nodes) {
    if (!nodeElement.dataset.nodeId) {
      console.warn('Node element does not have a data-node-id attribute');
      continue;
    }

    const nodeId = nodeElement.dataset.nodeId!;
    const nodeRelatedElements = getElementsWithNodeId(nodeId);

    for (const nodeRelatedElement of nodeRelatedElements) {
      nodeRelatedElement.addEventListener('mouseover', event => {
        highlightElements(nodeRelatedElements);
        event.stopPropagation();
      });
      nodeRelatedElement.addEventListener('mouseout', event => {
        unhighlightElements(nodeRelatedElements);
        event.stopPropagation();
      });
    }
  }
};

export { addEventListenersToAstNodes };
