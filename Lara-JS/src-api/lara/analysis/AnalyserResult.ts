import { 
    Fix,
} from "./Fix.js";


/**
 * Abstract class created as a model for every result of analyser
 * @class 
 */

export class AnalyserResult {
    name: string;
    node: unknown; //do later
    message: string;
    fix: Fix|undefined;

    constructor(name: string, node: any, message: string, fix: Fix|undefined) {
        this.name = name;
        this.node = node;
        this.message = message;
        this.fix = fix;
    }

    analyse(startNode: any) {
        // Not implemented
    }

    getName() {
        return this.name;
    }

    getNode() {
        return this.node;
    }

}