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

export { escapeHtml, replaceAfter };