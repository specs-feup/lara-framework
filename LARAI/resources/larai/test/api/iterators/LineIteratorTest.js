import LineIterator from "@specs-feup/lara/api/lara/iterators/LineIterator.js";
import Io from "@specs-feup/lara/api/lara/Io.js";

const stringContents = "Hello\nline2\n\nline 4";

// LineIterator from String
const stringLineIterator = new LineIterator(stringContents);
let stringTest = "";
let isFirstLine = true;
while (stringLineIterator.hasNext()) {
    const line = stringLineIterator.next();
    if (isFirstLine) {
        isFirstLine = false;
    } else {
        stringTest += "\n";
    }

    stringTest += line;
}

checkTrue(
    stringTest === stringContents,
    "Expected string to be the same as stringContents: " + stringTest
);

// LineIterator from File
const filename = "line_iterator_test.txt";
const file = Io.writeFile(filename, stringContents);
const fileLineIterator = new LineIterator(file);

stringTest = "";
isFirstLine = true;

// Use javascript iterator
while (fileLineIterator.hasNext()) {
    const line = fileLineIterator.next();
    if (isFirstLine) {
        isFirstLine = false;
    } else {
        stringTest += "\n";
    }

    stringTest += line;
}

checkTrue(
    stringTest === stringContents,
    "Expected file to be the same as stringContents: " + stringTest
);

Io.deleteFile(file);
