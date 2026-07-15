import Tool from "../tool/Tool.ts";

export default abstract class BenchmarkCompilationEngine extends Tool {
    constructor(name: string, disableWeaving: boolean = false) {
        super(name, disableWeaving);
    }
}