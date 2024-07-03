const getElementsWithNodeId = (id) => {
  return document.querySelectorAll(`span[data-node-id="${id}"]`);
}

const highlightElements = (elements) => {
  elements.forEach(element => element.classList.add('highlighted'));
};

const unhighlightElements = (elements) => {
  elements.forEach(element => element.classList.remove('highlighted'));
}

const addEventListenersToAstNodes = (nodes) => {
  for (const nodeElement of nodes) {
    const nodeId = nodeElement.dataset.nodeId;
    const nodeRelatedElements = getElementsWithNodeId(nodeId);

    for (const nodeRelatedElement of nodeRelatedElements) {
      nodeRelatedElement.addEventListener('mouseover', () => highlightElements(nodeRelatedElements));
      nodeRelatedElement.addEventListener('mouseout', () => unhighlightElements(nodeRelatedElements));
    }
  }
};

(function () {
  const astNodes = document.querySelectorAll('.ast-node');
  addEventListenersToAstNodes(astNodes);
})();
