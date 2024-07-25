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
const createFileTab = (filepath) => {
    const fileTab = document.createElement('div');
    fileTab.classList.add('file-tab');
    fileTab.dataset.filepath = filepath;
    fileTab.textContent = filepath ?? '<no file>';
    return fileTab;
};
export { getAstContainer, getCodeContainer, getContinueButton, getResizer, getFileTabs, createFileTab, };
//# sourceMappingURL=components.js.map