import Weaver from "./Weaver.js";
/**
 * Utility methods related to execution of scripts in a LARA environment.
 *
 * @deprecated Nothing uses this
 */
export default class Script {
    static #scriptOutput = {};
    /**
     * Sets the output of the script, which will be written to disk when using the flag -r
     *
     * @param value - the output of this script
     */
    static setOutput(value) {
        Script.#scriptOutput = value;
    }
    /**
     * Reads the value set by Script.setOutput()
     *
     * @returns The value set by Script.setOutput()
     */
    static getOutput() {
        return Script.#scriptOutput;
    }
    /**
     * Returns the input values passed using the flag -av
     *
     * @returns An object with the input arguments passed by command line
     */
    static getInput() {
        return Weaver.laraArgs;
    }
}
//# sourceMappingURL=Script.js.map