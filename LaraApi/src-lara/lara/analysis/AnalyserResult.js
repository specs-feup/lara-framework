/**
 * Abstract class created as a model for every result of analyser
 */
export default class AnalyserResult {
    name;
    node; //do later
    message;
    fix;
    constructor(name, node, message, fix) {
        this.name = name;
        this.node = node;
        this.message = message;
        this.fix = fix;
    }
    analyse(startNode) {
        throw "Not implemented";
    }
    getName() {
        return this.name;
    }
    getNode() {
        return this.node;
    }
}
//# sourceMappingURL=AnalyserResult.js.map