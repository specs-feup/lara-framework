import { arrayFromArgs } from "../core/LaraCore.js";
import DseValues from "./DseValues.js";
import DseValuesList from "./DseValuesList.js";
import DseVariant from "./DseVariant.js";
/**
 * Associates a variable name to a DseValues.
 */
export default class VariableVariant extends DseVariant {
    variableNames;
    dseValues;
    constructor(variableNames, dseValues) {
        super();
        if (arguments.length < 2) {
            throw "VariableVariant: needs at least two arguments, a dseValues and variable names";
        }
        this.dseValues =
            dseValues instanceof DseValues
                ? dseValues
                : new DseValuesList(arrayFromArgs(dseValues));
        this.variableNames =
            variableNames instanceof Array ? variableNames : [variableNames];
        // Verify that the number of variables is the same as the number of values per element
        if (this.dseValues.getNumValuesPerElement() !== this.variableNames.length) {
            throw "VariableVariant: the number of variables is not the same as the number of values per element";
        }
    }
    getType() {
        return "VariableVariant";
    }
    getNames() {
        return this.variableNames;
    }
    getDseValues() {
        return this.dseValues;
    }
}
//# sourceMappingURL=VariableVariant.js.map