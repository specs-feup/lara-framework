import LineInserter from "@specs-feup/lara/api/lara/util/LineInserter.js";
import Io from "@specs-feup/lara/api/lara/Io.js";

const stringContents = "Hello\nline2\n\nline 4";
const linesToInsert = { 1: "// Inserted line at 1", 3: "// Inserted line at 3" };

const lineInserter = new LineInserter();
console.log(
    "Insert from string:\n" + lineInserter.add(stringContents, linesToInsert)
);

const filename = "line_iterator_test.txt";
const file = Io.writeFile(filename, stringContents);
console.log("Insert from file:\n" + lineInserter.add(file, linesToInsert));
