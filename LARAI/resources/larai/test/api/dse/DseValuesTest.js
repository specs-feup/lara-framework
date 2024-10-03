import lara.dse.DseValuesList;
import lara.dse.DseValuesSet;
import lara.dse.VariableVariant;

aspectdef DseValuesTest

	var valuesList = new DseValuesList(1, 4, 5);
	dseValueTester("List", valuesList);

	var valuesList2 = new DseValuesList(2, 8, 10);
	var valuesSet = new DseValuesSet(valuesList, valuesList2);
	dseValueTester("Set", valuesSet);
	
	var varVariant = new VariableVariant(["a", "b"], valuesSet);
	
end

function dseValueTester(name, dseValues) {
	println(name + " size: " + dseValues.getNumElements());
	println(name + " num values per element: " + dseValues.getNumValuesPerElement());
	
	var values = [];
	while(dseValues.hasNext()) {
		values.push(dseValues.next());
	}
	
	println(name + " values: " + values.join());
	
	dseValues.reset();
	println(name + " hasNext: " + dseValues.hasNext());
}