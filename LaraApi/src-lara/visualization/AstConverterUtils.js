const addIdentation = (code, indentation) => {
    return code.split('\n').map((line, i) => i > 0 ? '   '.repeat(indentation) + line : line).join('\n');
};
const escapeHtml = (text) => {
    const specialCharMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
    };
    return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
};
const getSpanTags = (...attrs) => {
    return [`<span ${attrs.join(' ')}>`, '</span>'];
};
const getNodeCodeTags = (nodeId) => {
    return getSpanTags('class="node-code"', `data-node-id="${nodeId}"`);
};
export { addIdentation, escapeHtml, getSpanTags, getNodeCodeTags };
//# sourceMappingURL=AstConverterUtils.js.map