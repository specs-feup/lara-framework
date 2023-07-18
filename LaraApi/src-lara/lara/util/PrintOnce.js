export default class PrintOnce {
    static messagesSet = new Set();
    static message(message) {
        if (message === undefined) {
            return;
        }
        if (this.messagesSet.has(message)) {
            return;
        }
        this.messagesSet.add(message);
        console.log(message);
    }
}
//# sourceMappingURL=PrintOnce.js.map