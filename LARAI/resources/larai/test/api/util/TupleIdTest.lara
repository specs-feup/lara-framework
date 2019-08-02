import lara.util.TupleId;

aspectdef TupleIdTest

	var tupleId = new TupleId();
	
	println(tupleId.getId("a", "b"));
	println(tupleId.getId("a", "b", "c"));
	println(tupleId.getId("a", "b"));
	println(tupleId.getId("a", "hasOwnProperty", "d"));
	println(tupleId.getId("a", "hasOwnProperty", "d"));
	println(tupleId.getId("a", "hasOwnProperty"));
	
	
	println("Tuples:");
	var tuples = tupleId.getTuples();
	for(var key in tuples) {
		println(key + " -> " + tuples[key]);
	}
	
end
