import AnalyserResult from "./AnalyserResult.js";
export default class CheckResult {
    analyserResult;
    constructor(name, node, message, fix) {
        this.analyserResult = new AnalyserResult(name, node, message, fix);
    }
    performFix() {
        if (this.analyserResult.fix == undefined) {
            return;
        }
        this.analyserResult.fix.execute();
    }
    getMessage() {
        return this.analyserResult.message;
    }
}
//# sourceMappingURL=CheckResult.js.map