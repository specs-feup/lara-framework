import lara.iterators.LineIterator;
import lara.Io;

aspectdef LineIteratorTest

	var stringContents = "Hello\nline2\n\nline 4";

	// LineIterator from String
	var stringLineIterator = new LineIterator(stringContents);
	var stringTest = "";
	var isFirstLine = true;
	while(stringLineIterator.hasNext()) {
		var line = stringLineIterator.next();
		if(isFirstLine) {
			isFirstLine = false;
		} else {
			stringTest += "\n";
		}
		
		stringTest += line;
	}
	
	checkTrue(stringTest === stringContents, "Expected string to be the same as stringContents: " + stringTest);
	
	// LineIterator from File
	var filename = "line_iterator_test.txt";
	var file = Io.writeFile(filename, stringContents);
	var fileLineIterator = new LineIterator(file);
	
	stringTest = "";
	isFirstLine = true;
	
	//var jsIterator = fileLineIterator.jsIterator();
	//println("JS ITERATOR:");
	//printObject(jsIterator);
	
	//println("Iterator next:" + jsIterator.next().value);
	//println("Symbol.iterator before: " + fileLineIterator[Symbol.iterator]);
//	fileLineIterator[Symbol.iterator] = function() {return new _JsIterator(fileLineIterator)};
//	println("Symbol.iterator after: " + fileLineIterator[Symbol.iterator]);
	// Use javascript iterator
	while(fileLineIterator.hasNext()) {
		var line = fileLineIterator.next();
		if(isFirstLine) {
			isFirstLine = false;
		} else {
			stringTest += "\n";
		}
		
		stringTest += line;	
	}
	
	checkTrue(stringTest === stringContents, "Expected file to be the same as stringContents: " + stringTest);

	
	Io.deleteFile(file);
end
