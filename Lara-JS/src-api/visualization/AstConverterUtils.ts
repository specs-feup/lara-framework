/**
 * @file AstConverterUtils.ts
 * @brief Utility functions for the specializations of GenericAstConverter.
 */

/**
 * @brief Adds indentation to the given code, with the exception of the first line.
 * 
 * @param code Code
 * @param indentation The indentation to use 
 * @returns The indented code
 */
const addIdentation = (code: string, indentation: number): string => {
  return code.split('\n').map((line, i) => i > 0 ? '   '.repeat(indentation) + line : line).join('\n');
};


/**
 * @brief Escapes the HTML special characters in the given text.
 * 
 * @param text Text to escape
 * @returns The escaped text
 */
const escapeHtml = (text: string): string => {
  const specialCharMap: { [char: string]: string } = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
  };
  
  return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
};

/**
 * @brief Returns the opening and closing span tags with the given attributes.
 * 
 * @param attrs Attributes to add to the span tag
 * @returns An array with the opening and closing span tags
 */
const getSpanTags = (...attrs: string[]): string[] => {
  return [`<span ${attrs.join(' ')}>`, '</span>'];
};

/**
 * @brief Returns the span tags for the code of the given node.
 * 
 * @param nodeId Node ID
 * @returns An array with the opening and closing span tags
 */
const getNodeCodeTags = (nodeId: string): string[] => {
  return getSpanTags('class="node-code"', `data-node-id="${nodeId}"`);
};

/**
 * @brief Returns the span tags for syntax highlighting of code of the given type..
 * 
 * @param type Code type
 * @returns An array with the opening and closing span tags
 */
const getSyntaxHighlightTags = (type: 'comment' | 'keyword' | 'literal' | 'string' | 'type'): string[] => {
  return getSpanTags(`class="${type}"`);
};

export { addIdentation, escapeHtml, getSpanTags, getNodeCodeTags, getSyntaxHighlightTags };