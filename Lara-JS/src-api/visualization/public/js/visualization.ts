import JoinPoint from "./ToolJoinPoint.js";

const getNodeElement = (nodeId: string): HTMLSpanElement | null => {
  return document.querySelector<HTMLSpanElement>(`.ast-node[data-node-id="${nodeId}"]`);
};

const getNodeDropdown = (nodeId: string): HTMLDivElement | null => {
  return document.querySelector<HTMLDivElement>(`.ast-node-dropdown[data-node-id="${nodeId}"]`);
};

const getNodeRelatedElements = (nodeId: string): HTMLElement[] => {
  return Array.from(document.querySelectorAll<HTMLElement>(`.ast-node[data-node-id="${nodeId}"], .node-code[data-node-id="${nodeId}"]`));
};

const highlightNode = (nodeId: string): void => {
  const nodeCode = document.querySelectorAll<HTMLElement>(`.node-code[data-node-id="${nodeId}"]`)!;
  nodeCode.forEach(elem => elem.style.backgroundColor = 'var(--highlight-color)');

  const nodeElement = document.querySelector<HTMLElement>(`.ast-node[data-node-id="${nodeId}"]`)!;
  const nodeText = nodeElement.querySelector<HTMLElement>('.ast-node-text')!;
  nodeText.style.backgroundColor = 'var(--highlight-color)';

  let parentNode = nodeElement.parentElement?.previousSibling;
  while (parentNode instanceof HTMLElement && parentNode.classList.contains('ast-node')) {
    const parentNodeText = parentNode.querySelector<HTMLElement>('.ast-node-text')!;
    parentNodeText.style.backgroundColor = 'var(--secondary-highlight-color)';

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

const addHighlighingEvents = (() => {
  let selectedNodeId: string | null = null;
  return (nodeId: string): void => {
    const nodeRelatedElements = getNodeRelatedElements(nodeId);
    for (const nodeRelatedElement of nodeRelatedElements) {
      nodeRelatedElement.addEventListener('mouseover', event => {
        highlightNode(nodeId);
        event.stopPropagation();
      });
      nodeRelatedElement.addEventListener('mouseout', event => {
        unhighlightNode(nodeId);
        if (selectedNodeId !== null) {
          highlightNode(selectedNodeId);
        }
        event.stopPropagation();
      });
      nodeRelatedElement.addEventListener('click', event => {
        if (selectedNodeId !== null) {
          unhighlightNode(selectedNodeId);
        }
        
        selectedNodeId = nodeId;
        highlightNode(nodeId);
        event.stopPropagation();
      });
    }
  };
})();

const addEventListenersToAstNodes = (root: JoinPoint): void => {
  const nodeId = root.id;
  const nodeElement = getNodeElement(nodeId)!;
  const nodeDropdownButton = nodeElement.children[0] as HTMLButtonElement;
  
  const nodeDropdown = getNodeDropdown(nodeId)!;
  let nodeCollapsed = false;

  nodeDropdownButton.addEventListener('click', () => {
    nodeCollapsed = !nodeCollapsed;
    nodeDropdown.style.display = nodeCollapsed ? 'none' : 'block';
    
    const chevron = nodeDropdownButton.children[0] as HTMLElement;
    chevron.textContent = nodeCollapsed ? 'keyboard_arrow_right' : 'keyboard_arrow_down';
  });

  addHighlighingEvents(nodeId);
  root.children.forEach(child => addEventListenersToAstNodes(child));
};

export { addEventListenersToAstNodes };
