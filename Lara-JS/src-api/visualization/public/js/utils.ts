const countChar = (str: string, char: string): number => {
  let count = 0;
  for (const c of str) {
    if (c === char)
      count++;
  }
  return count;
}

const createIcon = (name: string): HTMLElement => {
  const icon = document.createElement('span');
  icon.classList.add('icon', 'material-symbols-outlined');
  icon.textContent = name;
  
  return icon;
}

export { countChar, createIcon };