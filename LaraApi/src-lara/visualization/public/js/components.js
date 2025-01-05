const getAstContainer = (() => {
    const astContainer = document.querySelector('#ast-container');
    if (!astContainer) {
        throw new Error('Could not find AST container');
    }
    return () => astContainer;
})();
const getCodeContainer = (() => {
    const codeContainer = document.querySelector('#code-container');
    if (!codeContainer) {
        throw new Error('Could not find code container');
    }
    return () => codeContainer;
})();
const getNodeInfoContainer = (() => {
    const nodeInfoContainer = document.querySelector('#node-info-container');
    if (!nodeInfoContainer) {
        throw new Error('Could not find node info container');
    }
    return () => nodeInfoContainer;
})();
const getContinueButton = (() => {
    const continueButton = document.querySelector('#continue-button');
    if (!continueButton) {
        throw new Error('Could not find continue button');
    }
    return () => continueButton;
})();
const getResizer = (() => {
    const resizer = document.querySelector('#resizer');
    if (!resizer) {
        throw new Error('Could not find resizer');
    }
    return () => resizer;
})();
const getFileTabs = (() => {
    const fileTabs = document.querySelector('#file-tabs');
    if (!fileTabs) {
        throw new Error('Could not find file tabs');
    }
    return () => fileTabs;
})();
const getNodeElement = (nodeId) => {
    return document.querySelector(`.ast-node[data-node-id="${nodeId}"]`);
};
const getNodeText = (nodeId) => {
    const nodeElement = getNodeElement(nodeId);
    return nodeElement?.querySelector('.node-text') ?? null;
};
const getFirstNodeCodeElement = (nodeId) => {
    return document.querySelector(`.node-code[data-node-id="${nodeId}"]`);
};
const getNodeCodeElements = (nodeId) => {
    return Array.from(document.querySelectorAll(`.node-code[data-node-id="${nodeId}"]`));
};
const getHighlightableElements = (nodeId) => {
    const nodeText = getNodeText(nodeId);
    if (!nodeText)
        return [];
    const nodeCodeElements = getNodeCodeElements(nodeId);
    return [nodeText, ...nodeCodeElements];
};
const getMainCodeWrapper = () => {
    return getCodeContainer().querySelector('.code-wrapper');
};
const getCodeLines = () => {
    return getCodeContainer().querySelector('.lines');
};
const getActiveCodeElement = () => {
    return getMainCodeWrapper()?.querySelector('code.active') ?? null;
};
const getFileCodeElement = (filename) => {
    return getCodeContainer().querySelector(`code[data-filepath="${filename}"]`);
};
const getFileTabsInternalDiv = () => {
    return getFileTabs().querySelector('div');
};
const getFileTab = (filepath) => {
    return getFileTabs().querySelector(`.file-tab[data-filepath="${filepath}"]`);
};
const getActiveFileTab = () => {
    return getFileTabs().querySelector('.file-tab.active');
};
const getFileTabsArrow = (direction) => {
    return document.querySelector(`#file-tabs-arrow-${direction}`);
};
const createIcon = (name) => {
    const icon = document.createElement('span');
    icon.classList.add('icon', 'material-symbols-outlined');
    icon.textContent = name;
    return icon;
};
const createNodeDropdown = (nodeId) => {
    const dropdown = document.createElement('div');
    dropdown.classList.add('ast-node-dropdown');
    dropdown.dataset.nodeId = nodeId;
    return dropdown;
};
const createDropdownButtonOnClick = (dropdown) => {
    let nodeCollapsed = false;
    return (event) => {
        nodeCollapsed = !nodeCollapsed;
        dropdown.style.display = nodeCollapsed ? 'none' : 'block';
        const dropdownButton = event.currentTarget;
        const chevronIcon = dropdownButton.children[0];
        chevronIcon.textContent = nodeCollapsed ? 'keyboard_arrow_right' : 'keyboard_arrow_down';
        event.stopPropagation();
    };
};
const createNodeDropdownButton = (dropdown) => {
    const dropdownButton = document.createElement('button');
    const arrowIcon = createIcon('keyboard_arrow_down');
    dropdownButton.appendChild(arrowIcon);
    if (dropdown) {
        dropdownButton.addEventListener('click', createDropdownButtonOnClick(dropdown));
    }
    else {
        dropdownButton.disabled = true;
    }
    return dropdownButton;
};
const createNodeElement = (nodeId, text, dropdownButton) => {
    const nodeElement = document.createElement('span'); // TODO: Convert to div
    nodeElement.classList.add('ast-node');
    nodeElement.dataset.nodeId = nodeId;
    const nodeText = document.createElement('span');
    nodeText.classList.add('node-text');
    nodeText.textContent = text;
    nodeElement.appendChild(dropdownButton);
    nodeElement.appendChild(nodeText);
    return nodeElement;
};
const createCodeElement = (code = '') => {
    const codeElement = document.createElement('code');
    codeElement.innerHTML = code;
    return codeElement;
};
const createCodeLines = (numLines) => {
    const codeLines = document.createElement('pre');
    codeLines.classList.add('lines');
    codeLines.textContent = Array.from({ length: numLines }, (_, i) => i + 1).join('\n');
    return codeLines;
};
const createCodeWrapper = () => {
    const codeWrapper = document.createElement('pre');
    codeWrapper.classList.add('code-wrapper');
    return codeWrapper;
};
const createNodeInfoLine = (name, value) => {
    const attributeName = document.createElement('span');
    attributeName.textContent = name + ': ';
    const attributeValue = document.createElement('span');
    attributeValue.textContent = value;
    const line = document.createElement('p');
    line.append(attributeName, attributeValue);
    return line;
};
const createNodeInfoAlert = (alert) => {
    const codeAlert = document.createElement('p');
    codeAlert.classList.add('alert');
    codeAlert.textContent = alert;
    return codeAlert;
};
const createFileTab = (filepath) => {
    const fileTab = document.createElement('button');
    fileTab.classList.add('file-tab');
    fileTab.dataset.filepath = filepath;
    fileTab.title = filepath;
    fileTab.textContent = filepath !== '' ? filepath.slice(filepath.lastIndexOf('/') + 1) : '<no file>';
    return fileTab;
};
const fileTabsArrowOnClick = (event, direction) => {
    const fileTabsInternalDiv = getFileTabsInternalDiv();
    const activeTabIndex = Array.from(fileTabsInternalDiv.children).findIndex(tab => tab.classList.contains('active'));
    if (direction === 'left' && activeTabIndex > 0) {
        fileTabsInternalDiv.children[activeTabIndex - 1].click();
    }
    else if (direction === 'right' && activeTabIndex < fileTabsInternalDiv.children.length - 1) {
        fileTabsInternalDiv.children[activeTabIndex + 1].click();
    }
    event.stopPropagation();
};
const createFileTabsArrow = (direction) => {
    const arrow = document.createElement('button');
    arrow.classList.add('file-tabs-arrow');
    arrow.id = `file-tabs-arrow-${direction}`;
    arrow.appendChild(createIcon(`keyboard_arrow_${direction}`));
    arrow.addEventListener('click', event => fileTabsArrowOnClick(event, direction));
    return arrow;
};
/**
 * @brief Updates the file tabs arrows, enabling or disabling them based on the
 * number of tabs and selected tab.
 */
const updateFileTabsArrows = () => {
    const fileTabs = getFileTabs();
    const fileTabsInternalDiv = getFileTabsInternalDiv();
    const activeFileTab = getActiveFileTab();
    const fileTabsLeftArrow = getFileTabsArrow('left');
    const fileTabsRightArrow = getFileTabsArrow('right');
    const fileTabsOverflow = fileTabs.scrollWidth < fileTabsInternalDiv.scrollWidth;
    fileTabsLeftArrow.disabled = !fileTabsOverflow || fileTabsInternalDiv.children[0] === activeFileTab;
    fileTabsRightArrow.disabled = !fileTabsOverflow || fileTabsInternalDiv.children[fileTabsInternalDiv.children.length - 1] === activeFileTab;
};
export { getAstContainer, getCodeContainer, getNodeInfoContainer, getContinueButton, getResizer, getFileTabs, getNodeElement, getNodeText, getFirstNodeCodeElement, getNodeCodeElements, getHighlightableElements, getMainCodeWrapper, getCodeLines, getActiveCodeElement, getFileCodeElement, getFileTab, getFileTabsInternalDiv, getActiveFileTab, getFileTabsArrow, createIcon, createNodeDropdown, createNodeDropdownButton, createNodeElement, createNodeInfoLine, createNodeInfoAlert, createCodeLines, createCodeElement, createCodeWrapper, createFileTab, createFileTabsArrow, updateFileTabsArrows, };
//# sourceMappingURL=components.js.map