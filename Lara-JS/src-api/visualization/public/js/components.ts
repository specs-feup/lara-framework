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

const createFileTab = (filename: string, code: string) => {
	const fileTab = document.createElement('div');
	fileTab.classList.add('file-tab');
	fileTab.dataset.filename = filename;
	fileTab.textContent = filename;

	return fileTab;
};

export {
  getAstContainer,
  getCodeContainer,
  getContinueButton,
  getResizer,
  getFileTabs,
  createFileTab,
};