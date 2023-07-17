export default class IdGenerator {
    idCounter = new Map();
    next(key = "") {
        const currentId = this.idCounter.get(key);
        if (currentId) {
            this.idCounter.set(key, currentId + 1);
        }
        else {
            this.idCounter.set(key, 1);
        }
        return `${key}${this.idCounter.get(key) ?? ""}`;
    }
}
//# sourceMappingURL=IdGenerator.js.map