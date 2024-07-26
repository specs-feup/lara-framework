import { addFileCode, updateLines } from "./ast-import.js";
import { createFileTab, getActiveCodeElement, getActiveFileTab, getFileCodeElement, getFileTab, getFileTabs, getMainCodeWrapper } from "./components.js";

let selectedFilepath: string | null = null;

const addFile = (path: string, code: string): void => {
  addFileCode(code, path);
  
  const fileTab = createFileTab(path);
  fileTab.addEventListener('click', () => selectFile(path));

  const fileTabs = getFileTabs();
  fileTabs.appendChild(fileTab);
};

const clearFiles = (): void => {
  const codeWrapper = getMainCodeWrapper();
  if (!codeWrapper)
    throw new Error('Code container not initialized');

  const fileTabs = getFileTabs();
  fileTabs.innerHTML = '';
  codeWrapper.innerHTML = '';

  selectedFilepath = null;
};


const selectFile = (filepath: string): void => {
  const fileTab = getFileTab(filepath);
  if (!fileTab)
    throw new Error(`File "${filepath}" not found`);

  if (filepath !== selectedFilepath) {
    const activeFileTab = getActiveFileTab();
    if (activeFileTab)
      activeFileTab.classList.remove('active');
    fileTab.classList.add('active');

    const activeCode = getActiveCodeElement();
    if (activeCode)
      activeCode.classList.remove('active');

    const fileCodeElement = getFileCodeElement(filepath)!;
    fileCodeElement.classList.add('active');
    updateLines();

    selectedFilepath = filepath;
  }
};

export { addFile, clearFiles, selectFile };
