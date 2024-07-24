import { addCode, updateLines } from "./ast-import.js";
import { createFileTab, getCodeContainer, getFileTabs } from "./components.js";

let selectedFilename: string | null = null;

const addFile = (name: string, code: string): void => {
  addCode(code, name);
  
  const fileTab = createFileTab(name, code);
  fileTab.addEventListener('click', () => selectFile(name));

  const fileTabs = getFileTabs();
  fileTabs.appendChild(fileTab);
}

const clearFiles = (): void => {
  const fileTabs = getFileTabs();
  fileTabs.innerHTML = '';

  const codeContainer = getCodeContainer();
  codeContainer.querySelector('pre')!.innerHTML = '';

  selectedFilename = null;
}


const selectFile = (filename: string): void => {
  const fileTabs = getFileTabs();

  if (filename !== selectedFilename) {
    const codeContainer = getCodeContainer();

    const selectedTab = fileTabs.querySelector(`.file-tab[data-filename="${filename}"]`)!;
    fileTabs.querySelector('.file-tab.active')?.classList.remove('active');
    selectedTab.classList.add('active');

    const fileCode = codeContainer.querySelector(`code[data-filename="${filename}"]`)!;
    const activeCode = codeContainer.querySelector('code.active');
    if (activeCode)
      activeCode.classList.remove('active');
    fileCode.classList.add('active');
    updateLines();

    selectedFilename = filename;
  }
}

export { addFile, clearFiles, selectFile };
