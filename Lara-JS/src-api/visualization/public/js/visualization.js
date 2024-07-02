const highlightNode = (node) => {
  document.querySelectorAll(`span[data-node-id="${node.dataset.nodeId}"]`)
    .forEach(element => element.classList.add('highlighted'));
};

const unhighlightNode = (node) => {
  document.querySelectorAll(`span[data-node-id="${node.dataset.nodeId}"]`)
    .forEach(element => element.classList.remove('highlighted'));
}

const addEventListenersToAstNodes = (nodes) => {
  for (const nodeElement of nodes) {
		nodeElement.addEventListener('mouseover', () => highlightNode(nodeElement));
		nodeElement.addEventListener('mouseout', () => unhighlightNode(nodeElement));
  }
};

(function () {
  const astNodes = document.querySelectorAll('.ast-node');
  addEventListenersToAstNodes(astNodes);
})();
