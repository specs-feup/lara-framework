const escapeHtml = (text) => {
    var specialCharMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
    };
    return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
};
const replaceAfter = (text, search, replace, start) => {
    const index = text.indexOf(search, start);
    if (index === -1) {
        return text;
    }
    return text.slice(0, index) + replace + text.slice(index + search.length);
};
export { escapeHtml, replaceAfter };
//# sourceMappingURL=utils.js.map