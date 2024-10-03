import lara.util.Accumulator;

aspectdef AccumulatorTest

	var acc = new Accumulator();
	
	// Add as tuple
	acc.add("step1", "step2");

	// Add as array
	acc.add(["step1", "step2"]);
	
	// Add with common subchain
	acc.add(["step1", "step2.1"]);
	
	// Add just subchain
	acc.add("step1");
	acc.add("step1");
	acc.add(["step1"]);
	
	// Get by tuple
	println("Tuple get:" + acc.get("step1", "step2"));
	
	// Get by array
	println("Array get:" + acc.get(["step1", "step2"]));
	
	// Use keys to print contents
	for(var key of acc.keys()) {
		println("key: " + key.join(", ") + " -> value: " + acc.get(key));
	}
	
	
end
