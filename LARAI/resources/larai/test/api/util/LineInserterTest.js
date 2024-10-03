import lara.util.LineInserter;
import lara.Io;

aspectdef LineInserterTest

	var stringContents = "Hello\nline2\n\nline 4";
	var linesToInsert = {1: "// Inserted line at 1", 3: "// Inserted line at 3"};
	
	var lineInserter = new LineInserter();
	println("Insert from string:\n" + lineInserter.add(stringContents, linesToInsert));

	var filename = "line_iterator_test.txt";
	var file = Io.writeFile(filename, stringContents);
	println("Insert from file:\n" + lineInserter.add(file, linesToInsert));

end
