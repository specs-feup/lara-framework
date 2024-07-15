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

const createLucideIcon = (name: string): HTMLImageElement => {
  const icon = document.createElement('img');
  icon.classList.add('icon');
  icon.src = `/svg/lucide-icons/${name}.svg`;
  icon.alt = `${name}-icon`;
  return icon;
}

export { escapeHtml, replaceAfter, createLucideIcon };