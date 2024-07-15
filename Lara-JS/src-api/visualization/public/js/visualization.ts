import JoinPoint from "./ToolJoinPoint.js";

const getNodeElement = (nodeId: string): HTMLSpanElement | null => {
  return document.querySelector<HTMLSpanElement>(`.ast-node[data-node-id="${nodeId}"]`);
}

const getNodeDropdown = (nodeId: string): HTMLDivElement | null => {
  return document.querySelector<HTMLDivElement>(`.ast-node-dropdown[data-node-id="${nodeId}"]`);
}

const getNodeRelatedElements = (nodeId: string): HTMLElement[] => {
  return Array.from(document.querySelectorAll<HTMLElement>(`.ast-node[data-node-id="${nodeId}"], .node-code[data-node-id="${nodeId}"]`));
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

const addEventListenersToAstNodes = (root: JoinPoint): void => {
  const nodeId = root.id;
  const nodeElement = getNodeElement(nodeId)!;
  const [nodeChevron, nodeText] = nodeElement.children;
  
  const nodeDropdown = getNodeDropdown(nodeId)!;
  let nodeCollapsed = false;
  nodeChevron.addEventListener('click', event => {
    nodeCollapsed = !nodeCollapsed;
    nodeDropdown.style.display = nodeCollapsed ? 'none' : 'block';
    event.stopPropagation();
  });

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

  root.children.forEach(child => addEventListenersToAstNodes(child));
};

export { addEventListenersToAstNodes };
