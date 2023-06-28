laraImport("lara.pass.Pass");
laraImport("lara.pass.composition.PassApplyArg");

class Passes {
  /**
   * Applies a sequence of passes.
   *
   * @param {JoinPoint} $jp - Apply pass using this join point as the starting point
   * @param {...} args - An array or sequence of:
   * 	1) Pass instance;
   * 	2) Pass class;
   * 	3) function that accepts a $jp;
   * 	4) An array where the first element is 2) or 3), followed by arguments that are passed as arguments of the function or class constructor.
   */
  static apply($jp) {
    // Ensure it is an array
    const passesArray = arrayFromArgs(arguments, 1);

    const results = [];

    let argIndex = 0;
    for (const pass of passesArray) {
      argIndex++;

      // If not array, put inside array
      const passArray = !Array.isArray(pass) ? [pass] : pass;

      results.push(Passes.#applyPass($jp, passArray, argIndex));
    }

    return results;
  }

  static #applyPass($jp, passArray, index) {
    if (passArray.length === 0) {
      throw new Error(`Pass at position ${index} is an empty array`);
    }

    const pass = passArray[0];

    // Obtain type of pass argument
    const argType = Passes.getArgType(pass);

    if (argType === undefined) {
      throw new Error(
        `Pass at position ${index} is not a Pass class, instance or a function`
      );
    }

    const passArgs = passArray.slice(1);

    // Check if there are arguments for the pass
    if (passArgs.length > 0 && argType === PassApplyArg.PASS_INSTANCE) {
      println(
        `Pass at position ${index} is a Pass instance but has arguments, arguments will be ignored`
      );
    }

    // If an instance of a Pass, just call apply over the given $jp
    if (argType === PassApplyArg.PASS_INSTANCE) {
      return pass.apply($jp);
    }

    // If a Pass class, instantiate with args
    if (argType === PassApplyArg.PASS_CLASS) {
      return new pass(...passArgs).apply($jp);
    }

    // If function, call it
    if (argType === PassApplyArg.FUNCTION) {
      return pass($jp, ...passArgs);
    }

    throw new Error("Not implemented for pass argument of type " + argType);
  }

  static getArgType(applyArg) {
    // If an instance of a Pass, just call apply over the given $jp
    if (applyArg instanceof Pass) {
      return PassApplyArg.PASS_INSTANCE;
    }

    // Both Pass class and function will return true if tested for function, first test in class of Pass
    if (applyArg.prototype instanceof Pass) {
      return PassApplyArg.PASS_CLASS;
    }

    if (applyArg instanceof Function) {
      return PassApplyArg.FUNCTION;
    }

    return undefined;
  }
}
