const codeContainer = document.querySelector('#code code');

const highlightNode = (node) => {
  document.querySelectorAll(`span[data-node-id="${node.dataset.nodeId}"]`)
    .forEach(element => element.classList.add('highlighted'));
};

const unhighlightNode = (node) => {
  document.querySelectorAll(`span[data-node-id="${node.dataset.nodeId}"]`)
    .forEach(element => element.classList.remove('highlighted'));
}

const addEventListenersToAstNodes = () => {
  const astNodes = document.querySelectorAll('.ast-node');

  for (const nodeElement of astNodes) {
		nodeElement.addEventListener('mouseover', () => highlightNode(nodeElement));
		nodeElement.addEventListener('mouseout', () => unhighlightNode(nodeElement));
  }
};

addEventListenersToAstNodes();
