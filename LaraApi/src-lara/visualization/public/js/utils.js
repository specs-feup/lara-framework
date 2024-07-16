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
const countChar = (str, char) => {
    let count = 0;
    for (const c of str) {
        if (c === char)
            count++;
    }
    return count;
};
const createIcon = (name) => {
    const icon = document.createElement('span');
    icon.classList.add('icon', 'material-symbols-outlined');
    icon.textContent = name;
    return icon;
};
export { escapeHtml, replaceAfter, countChar, createIcon };
//# sourceMappingURL=utils.js.map