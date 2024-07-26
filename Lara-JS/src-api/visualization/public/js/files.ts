/**
 * @file files.ts
 * @brief Functions for handling files in the visualization.
 */

import { addFileCode, updateLines } from "./ast-import.js";
import { createFileTab, getActiveCodeElement, getActiveFileTab, getFileCodeElement, getFileTab, getFileTabs, getMainCodeWrapper } from "./components.js";

let selectedFilepath: string | null = null;

/**
 * @brief Adds a new file, with the respective file tab and (hidden) code, to the visualization.
 * 
 * @param path Path of the file
 * @param code File code
 */
const addFile = (path: string, code: string): void => {
  addFileCode(code, path);
  
  const fileTab = createFileTab(path);
  fileTab.addEventListener('click', () => selectFile(path));

  const fileTabs = getFileTabs();
  fileTabs.appendChild(fileTab);
};

/**
 * @brief Clears all files from the code container.
 */
const clearFiles = (): void => {
  const codeWrapper = getMainCodeWrapper();
  if (!codeWrapper)
    throw new Error('Code container not initialized');

  const fileTabs = getFileTabs();
  fileTabs.innerHTML = '';
  codeWrapper.innerHTML = '';

  selectedFilepath = null;
};

/**
 * @brief Selects a file, by making its code visible in the code container.
 * @param filepath 
 */
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
