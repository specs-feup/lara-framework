const escapeHtml = (text) => {
    var specialCharMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, (match) => specialCharMap[match]);
};
export { escapeHtml };
//# sourceMappingURL=utils.js.map