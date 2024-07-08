const escapeHtml = (text) => {
    var specialCharMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
    };
    return text.replace(/[&<>]/g, (match) => specialCharMap[match]);
};
export { escapeHtml };
//# sourceMappingURL=utils.js.map