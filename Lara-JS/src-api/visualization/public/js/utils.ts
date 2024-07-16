const escapeHtml = (text: string): string => {
  var specialCharMap: { [char: string]: string } = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
  };
  
  return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
}

const replaceAfter = (text: string, search: string, replace: string, start: number): string => {
  const index = text.indexOf(search, start);
  if (index === -1) {
    return text;
  }
  return text.slice(0, index) + replace + text.slice(index + search.length);
}

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

export { escapeHtml, replaceAfter, countChar, createIcon };