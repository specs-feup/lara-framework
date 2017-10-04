aspectdef LaraCoreTest

	// Single element
	println(arrayFromArgsTest("Hello").length);

	// Several elements
	println(arrayFromArgsTest("Hello", "World").length);
	
	// Single array
	println(arrayFromArgsTest(["Hello", "World"]).length);

	// Single element after 1
	println(arrayFromArgsAfterOneTest("Hello").length);
		
	// Several elements after 1
	println(arrayFromArgsAfterOneTest("Hello", "World").length);
	
end


function arrayFromArgsTest() {
	return arrayFromArgs(arguments);
}

function arrayFromArgsAfterOneTest() {
	return arrayFromArgs(arguments, 1);
}