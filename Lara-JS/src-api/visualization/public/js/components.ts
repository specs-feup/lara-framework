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
};

const getNodeText = (nodeId: string): HTMLSpanElement | null => {
  const nodeElement = getNodeElement(nodeId);
  return nodeElement?.querySelector('.node-text') ?? null;
};

const getFirstNodeCodeElement = (nodeId: string): HTMLSpanElement | null => {
  return document.querySelector(`.node-code[data-node-id="${nodeId}"]`);
};

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


const getMainCodeWrapper = (): HTMLPreElement | null => {
  return getCodeContainer().querySelector('.code-wrapper');
};

const getCodeLines = (): HTMLPreElement | null => {
  return getCodeContainer().querySelector('.lines');
};

const getActiveCodeElement = (): HTMLElement | null => {
  return getMainCodeWrapper()?.querySelector('code.active') ?? null;
};

const getFileCodeElement = (filename: string): HTMLElement | null => {
  return getCodeContainer().querySelector(`code[data-filepath="${filename}"]`);
};


const getFileTabsInternalDiv = (): HTMLDivElement | null => {
  return getFileTabs().querySelector('div');
}

const getFileTab = (filepath: string): HTMLButtonElement | null => {
  return getFileTabs().querySelector(`.file-tab[data-filepath="${filepath}"]`);
};

const getActiveFileTab = (): HTMLButtonElement | null => {
  return getFileTabs().querySelector('.file-tab.active');
};

const getFileTabsArrow = (direction: 'left' | 'right'): HTMLButtonElement | null => {
  return document.querySelector(`#file-tabs-arrow-${direction}`);
}


const createIcon = (name: string): HTMLElement => {
  const icon = document.createElement('span');
  icon.classList.add('icon', 'material-symbols-outlined');
  icon.textContent = name;
  
  return icon;
};


const createNodeDropdown = (nodeId: string): HTMLDivElement => {
	const dropdown = document.createElement('div');
	dropdown.classList.add('ast-node-dropdown');
	dropdown.dataset.nodeId = nodeId;

	return dropdown;
};

const createDropdownButtonOnClick = (dropdown: HTMLElement) => {
	let nodeCollapsed = false;
	return (event: Event): void => {
		nodeCollapsed = !nodeCollapsed;
		dropdown.style.display = nodeCollapsed ? 'none' : 'block';

		const dropdownButton = event.currentTarget as HTMLElement;
		const chevronIcon = dropdownButton.children[0] as HTMLElement;
		chevronIcon.textContent = nodeCollapsed ? 'keyboard_arrow_right' : 'keyboard_arrow_down';

		event.stopPropagation();
	};
};

const createNodeDropdownButton = (dropdown?: HTMLElement): HTMLButtonElement => {
	const dropdownButton = document.createElement('button');

  const arrowIcon = createIcon('keyboard_arrow_down');
  dropdownButton.appendChild(arrowIcon);

  if (dropdown) {
    dropdownButton.addEventListener('click', createDropdownButtonOnClick(dropdown));
  } else {
		dropdownButton.disabled = true;
  }

	return dropdownButton;
};

const createNodeElement = (nodeId: string, text: string, dropdownButton: HTMLElement): HTMLSpanElement => {
	const nodeElement = document.createElement('span');  // TODO: Convert to div
	nodeElement.classList.add('ast-node');

	nodeElement.dataset.nodeId = nodeId;

	const nodeText = document.createElement('span');
	nodeText.classList.add('node-text');
	nodeText.textContent = text;

	nodeElement.appendChild(dropdownButton);
	nodeElement.appendChild(nodeText);

	return nodeElement;
};


const createCodeElement = (code: string = ''): HTMLElement => {
  const codeElement = document.createElement('code');
  codeElement.innerHTML = code;
  return codeElement;
};

const createCodeLines = (numLines: number): HTMLPreElement => {
  const codeLines = document.createElement('pre');
  codeLines.classList.add('lines');
  codeLines.textContent = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
  return codeLines;
};

const createCodeWrapper = (): HTMLPreElement => {
  const codeWrapper = document.createElement('pre');
	codeWrapper.classList.add('code-wrapper');
  return codeWrapper;
};

const createNodeInfoLine = (name: string, value: string): HTMLParagraphElement => {
  const attributeName = document.createElement('span');
  attributeName.textContent = name + ': ';

  const attributeValue = document.createElement('span');
  attributeValue.textContent = value;

  const line = document.createElement('p');
  line.append(attributeName, attributeValue);
  return line;
};

const createNodeInfoAlert = (alert: string): HTMLParagraphElement => {
  const codeAlert = document.createElement('p');
  codeAlert.classList.add('alert');
  codeAlert.textContent = alert;
  return codeAlert;
};

const createFileTab = (filepath: string): HTMLButtonElement => {
	const fileTab = document.createElement('button');
	fileTab.classList.add('file-tab');
	fileTab.dataset.filepath = filepath;

  fileTab.title = filepath;
	fileTab.textContent = filepath !== '' ? filepath.slice(filepath.lastIndexOf('/') + 1) : '<no file>';

	return fileTab;
};

const fileTabsArrowOnClick = (event: Event, direction: 'left' | 'right') => {
  const fileTabsInternalDiv = getFileTabsInternalDiv()!;
  const activeTabIndex = Array.from(fileTabsInternalDiv.children).findIndex(tab => tab.classList.contains('active'));

  if (direction === 'left' && activeTabIndex > 0) {
    (fileTabsInternalDiv.children[activeTabIndex - 1] as HTMLButtonElement).click();
  } else if (direction === 'right' && activeTabIndex < fileTabsInternalDiv.children.length - 1) {
    (fileTabsInternalDiv.children[activeTabIndex + 1] as HTMLButtonElement).click();
  }

  event.stopPropagation();
};

const createFileTabsArrow = (direction: 'left' | 'right'): HTMLButtonElement => {
  const arrow = document.createElement('button');
  arrow.classList.add('file-tabs-arrow');
  arrow.id = `file-tabs-arrow-${direction}`;

  arrow.appendChild(createIcon(`keyboard_arrow_${direction}`));
  arrow.addEventListener('click', event => fileTabsArrowOnClick(event, direction));
  return arrow;
}

/**
 * @brief Updates the file tabs arrows, enabling or disabling them based on the
 * number of tabs and selected tab.
 */
const updateFileTabsArrows = (): void => {
  const fileTabs = getFileTabs();
  const fileTabsInternalDiv = getFileTabsInternalDiv()!;
  const activeFileTab = getActiveFileTab();

  const fileTabsLeftArrow = getFileTabsArrow('left')!;
  const fileTabsRightArrow = getFileTabsArrow('right')!;

  const fileTabsOverflow = fileTabs.scrollWidth < fileTabsInternalDiv.scrollWidth;
  fileTabsLeftArrow.disabled = !fileTabsOverflow || fileTabsInternalDiv.children[0] === activeFileTab;
  fileTabsRightArrow.disabled = !fileTabsOverflow || fileTabsInternalDiv.children[fileTabsInternalDiv.children.length - 1] === activeFileTab;
}

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
  getMainCodeWrapper,
  getCodeLines,
  getActiveCodeElement,
  getFileCodeElement,
  getFileTab,
  getFileTabsInternalDiv,
  getActiveFileTab,
  getFileTabsArrow,
  createIcon,
  createNodeDropdown,
  createNodeDropdownButton,
  createNodeElement,
  createNodeInfoLine,
  createNodeInfoAlert,
  createCodeLines,
  createCodeElement,
  createCodeWrapper,
  createFileTab,
  createFileTabsArrow,
  updateFileTabsArrows,
};