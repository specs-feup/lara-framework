const escapeHtml = (text: string): string => {
  var specialCharMap: { [char: string]: string } = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
  };
  
  return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
}

export { escapeHtml };