import { arrayFromArgs } from "../core/LaraCore.js";
import DseValues from "./DseValues.js";
import DseValuesList from "./DseValuesList.js";
import DseVariant from "./DseVariant.js";

type T = any;

/**
 * Associates a variable name to a DseValues.
 */
export default class VariableVariant extends DseVariant {
  protected variableNames: string[];
  protected dseValues: DseValues;

  constructor(variableNames: string | string[], dseValues: DseValues) {
    super();

    if (arguments.length < 2) {
      throw "VariableVariant: needs at least two arguments, a dseValues and variable names";
    }

    this.dseValues =
      dseValues instanceof DseValues
        ? dseValues
        : new DseValuesList(arrayFromArgs(dseValues) as T[]);

    this.variableNames =
      variableNames instanceof Array ? variableNames : [variableNames];

    // Verify that the number of variables is the same as the number of values per element
    if (this.dseValues.getNumValuesPerElement() !== this.variableNames.length) {
      throw "VariableVariant: the number of variables is not the same as the number of values per element";
    }
  }

  getType(): string {
    return "VariableVariant";
  }

  getNames(): string[] {
    return this.variableNames;
  }

  getDseValues(): DseValues {
    return this.dseValues;
  }
}
