import lara.Csv;

aspectdef CsvTest

	var csvContents = "name, col1, col2, col3\n" + 
					  "line1, 1, 2, 3\n"+
					  "line2, 2, 4, 8";

	printObject(Csv.parse(csvContents));
end

