import {
    AnalyserResult,
} from "./AnalyserResult.js";

import {
    Fix,
} from "./Fix.js"

class CheckResult {
    analyserResult: AnalyserResult;
    
    constructor(name: string, node: any, message: string, fix: Fix|undefined) {
        this.analyserResult= new AnalyserResult(name, node, message, fix);
    }

    performFix(){
        if (this.analyserResult.fix == undefined){
            return;
        }
        this.analyserResult.fix.execute();
    }
    getMessage(){
        return this.analyserResult.message;
    }
}
