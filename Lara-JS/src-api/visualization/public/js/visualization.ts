const getElementsWithNodeId = (id: number): NodeListOf<HTMLElement> => {
  return document.querySelectorAll(`span[data-node-id="${id}"]`);
}

const highlightElements = (elements: NodeListOf<HTMLElement>): void => {
  elements.forEach(element => element.classList.add('highlighted'));
};

const unhighlightElements = (elements: NodeListOf<HTMLElement>): void => {
  elements.forEach(element => element.classList.remove('highlighted'));
}

const addEventListenersToAstNodes = (nodes: NodeListOf<HTMLElement>): void => {
  for (const nodeElement of nodes) {
    if (!nodeElement.dataset.nodeId) {
      continue;
    }

    const nodeId = parseInt(nodeElement.dataset.nodeId!);
    const nodeRelatedElements = getElementsWithNodeId(nodeId);

    for (const nodeRelatedElement of nodeRelatedElements) {
      nodeRelatedElement.addEventListener('mouseover', () => highlightElements(nodeRelatedElements));
      nodeRelatedElement.addEventListener('mouseout', () => unhighlightElements(nodeRelatedElements));
    }
  }
};

export { addEventListenersToAstNodes };
