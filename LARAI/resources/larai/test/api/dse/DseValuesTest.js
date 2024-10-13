import DseValuesList from "@specs-feup/lara/api/lara/dse/DseValuesList.js";
import DseValuesSet from "@specs-feup/lara/api/lara/dse/DseValuesSet.js";
import VariableVariant from "@specs-feup/lara/api/lara/dse/VariableVariant.js";

function dseValueTester(name, dseValues) {
    console.log(name + " size: " + dseValues.getNumElements());
    console.log(
        name + " num values per element: " + dseValues.getNumValuesPerElement()
    );

    const values = [];
    while (dseValues.hasNext()) {
        values.push(dseValues.next());
    }

    console.log(name + " values: " + values.join());

    dseValues.reset();
    console.log(name + " hasNext: " + dseValues.hasNext());
}

const valuesList = new DseValuesList(1, 4, 5);
dseValueTester("List", valuesList);

const valuesList2 = new DseValuesList(2, 8, 10);
const valuesSet = new DseValuesSet(valuesList, valuesList2);
dseValueTester("Set", valuesSet);

const varVariant = new VariableVariant(["a", "b"], valuesSet);
