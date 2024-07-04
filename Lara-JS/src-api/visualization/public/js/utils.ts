function escapeHtml(text: string): string {
  var specialCharMap: { [char: string]: string } = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#039;'
  };
  
  return text.replace(/[&<>"']/g, (match) => specialCharMap[match]);
}

export { escapeHtml };