const addIdentation = (code: string, indentation: number): string => {
  return code.split('\n').map((line, i) => i > 0 ? '   '.repeat(indentation) + line : line).join('\n');
};

const escapeHtml = (text: string): string => {
  const specialCharMap: { [char: string]: string } = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
  };
  
  return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
};

const getSpanTags = (...attrs: string[]): string[] => {
  return [`<span ${attrs.join(' ')}>`, '</span>'];
};

const getNodeCodeTags = (nodeId: string): string[] => {
  return getSpanTags('class="node-code"', `data-node-id="${nodeId}"`);
};

const getSyntaxHighlightTags = (type: 'comment' | 'keyword' | 'literal' | 'string' | 'type'): string[] => {
  return getSpanTags(`class="${type}"`);
};

export { addIdentation, escapeHtml, getSpanTags, getNodeCodeTags, getSyntaxHighlightTags };