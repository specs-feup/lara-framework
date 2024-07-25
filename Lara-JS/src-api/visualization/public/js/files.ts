import { addCode, updateLines } from "./ast-import.js";
import { createFileTab, getCodeContainer, getFileTabs } from "./components.js";

let selectedFilepath: string | null = null;

const addFile = (path: string, code: string): void => {
  addCode(code, path);
  
  const fileTab = createFileTab(path);
  fileTab.addEventListener('click', () => selectFile(path));

  const fileTabs = getFileTabs();
  fileTabs.appendChild(fileTab);
}

const clearFiles = (): void => {
  const fileTabs = getFileTabs();
  fileTabs.innerHTML = '';

  const codeContainer = getCodeContainer();
  codeContainer.querySelector('pre')!.innerHTML = '';

  selectedFilepath = null;
}


const selectFile = (filepath: string): void => {
  const fileTabs = getFileTabs();

  if (filepath !== selectedFilepath) {
    const codeContainer = getCodeContainer();

    const selectedTab = fileTabs.querySelector(`.file-tab[data-filepath="${filepath}"]`)!;
    if (selectedTab === null)
      throw Error(`File "${filepath}" not found`);

    fileTabs.querySelector('.file-tab.active')?.classList.remove('active');
    selectedTab.classList.add('active');

    const fileCode = codeContainer.querySelector(`code[data-filepath="${filepath}"]`)!;
    const activeCode = codeContainer.querySelector('code.active');
    if (activeCode)
      activeCode.classList.remove('active');
    fileCode.classList.add('active');
    updateLines();

    selectedFilepath = filepath;
  }
}

export { addFile, clearFiles, selectFile };
