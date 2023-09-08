/**
 * Utility methods related to execution of scripts in a LARA environment.
 */
class Script {
  static #scriptOutput = {};

  /**
   * Sets the output of the script, which will be written to disk when using the flag -r
   *
   * @param {any} value - the output of this script
   */
  static setOutput(value) {
    Script.#scriptOutput = value;
  }

  /**
   * Reads the value set by Script.setOutput()
   *
   * @return {any} the value set by Script.setOutput()
   */
  static getOutput() {
    return Script.#scriptOutput;
  }

  /**
   * Returns the input values passed using the flag -av
   *
   * @return {Object} an object with the input arguments passed by command line
   */
  static getInput() {
    return laraArgs;
  }
}
