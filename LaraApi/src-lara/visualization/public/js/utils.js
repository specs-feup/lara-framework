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
const createLucideIcon = (name) => {
    const icon = document.createElement('img');
    icon.classList.add('icon');
    icon.src = `/svg/lucide-icons/${name}.svg`;
    icon.alt = `${name}-icon`;
    return icon;
};
export { escapeHtml, replaceAfter, countChar, createLucideIcon };
//# sourceMappingURL=utils.js.map