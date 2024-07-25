const getAstContainer = (() => {
  const astContainer = document.querySelector<HTMLDivElement>('#ast-container');
  if (!astContainer) {
    throw new Error('Could not find AST container');
  }
  return (): HTMLDivElement => astContainer;
})();

const getCodeContainer = (() => {
  const codeContainer = document.querySelector<HTMLDivElement>('#code-container');
  if (!codeContainer) {
    throw new Error('Could not find code container');
  }
  return (): HTMLDivElement => codeContainer;
})();

const getNodeInfoContainer = (() => {
  const nodeInfoContainer = document.querySelector<HTMLDivElement>('#node-info-container');
  if (!nodeInfoContainer) {
    throw new Error('Could not find node info container')
  }
  return (): HTMLDivElement => nodeInfoContainer;
})();

const getContinueButton = (() => {
  const continueButton = document.querySelector<HTMLButtonElement>('#continue-button');
  if (!continueButton) {
    throw new Error('Could not find continue button');
  }
  return (): HTMLButtonElement => continueButton;
})();

const getResizer = (() => {
  const resizer = document.querySelector<HTMLDivElement>('#resizer');
  if (!resizer) {
    throw new Error('Could not find resizer');
  }
  return (): HTMLDivElement => resizer;
})();

const getFileTabs = (() => {
  const fileTabs = document.querySelector<HTMLDivElement>('#file-tabs');
  if (!fileTabs) {
    throw new Error('Could not find file tabs');
  }
  return (): HTMLDivElement => fileTabs;
})();


const getNodeElement = (nodeId: string): HTMLSpanElement | null => {
  return document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
}

const getNodeText = (nodeId: string): HTMLSpanElement | null => {
  const nodeElement = getNodeElement(nodeId);
  return nodeElement?.querySelector('.node-text') ?? null;
};

const getFirstNodeCodeElement = (nodeId: string): HTMLSpanElement | null => {
  return document.querySelector(`.node-code[data-node-id="${nodeId}"]`);
}

const getNodeCodeElements = (nodeId: string): HTMLSpanElement[] => {
  return Array.from(document.querySelectorAll(`.node-code[data-node-id="${nodeId}"]`));
};

const getHighlightableElements = (nodeId: string): HTMLElement[] => {
  const nodeText = getNodeText(nodeId);
  if (!nodeText)
    return [];

  const nodeCodeElements = getNodeCodeElements(nodeId);
  return [nodeText, ...nodeCodeElements];
};


const createCodeElement = (code: string = ''): HTMLElement => {
  const codeElement = document.createElement('code');
  codeElement.textContent = code;
  return codeElement;
}

const createCodeWrapper = (): HTMLPreElement => {
  return document.createElement('pre');
}

const createNodeInfoLine = (name: string, value: string): HTMLParagraphElement => {
  const attributeName = document.createElement('span');
  attributeName.textContent = name + ': ';

  const attributeValue = document.createElement('span');
  attributeValue.textContent = value;

  const line = document.createElement('p');
  line.append(attributeName, attributeValue);
  return line;
}

const createNodeInfoAlert = (alert: string): HTMLParagraphElement => {
  const codeAlert = document.createElement('p');
  codeAlert.classList.add('alert');
  codeAlert.textContent = alert;
  return codeAlert;
}

const createFileTab = (filepath: string): HTMLButtonElement => {
	const fileTab = document.createElement('button');
	fileTab.classList.add('file-tab');
	fileTab.dataset.filepath = filepath;

  fileTab.title = filepath;
	fileTab.textContent = filepath !== '' ? filepath.slice(filepath.lastIndexOf('/') + 1) : '<no file>';

	return fileTab;
};

export {
  getAstContainer,
  getCodeContainer,
  getNodeInfoContainer,
  getContinueButton,
  getResizer,
  getFileTabs,
  getNodeElement,
  getNodeText,
  getFirstNodeCodeElement,
  getNodeCodeElements,
  getHighlightableElements,
  createNodeInfoLine,
  createNodeInfoAlert,
  createCodeElement,
  createCodeWrapper,
  createFileTab,
};