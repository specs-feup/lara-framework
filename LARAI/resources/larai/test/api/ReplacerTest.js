laraImport("lara.Io");
laraImport("lara.util.Replacer");

const replacer1 = new Replacer("a template <string_to_replace>");
console.log(
    "Replacer 1: " +
        replacer1.replaceAll("<string_to_replace>", "string").getString()
);

const tempTemplate = "temp_template.txt";
Io.writeFile(tempTemplate, "another template <string_to_replace>");

const replacer2 = new Replacer(Io.getPath(tempTemplate));
replacer2.replaceAll("<string_to_replace>", "string");
console.log("Replacer 2: " + replacer2.getString());

const replacer3 = Replacer.fromFilename(tempTemplate);
replacer3.replaceAll("<string_to_replace>", "string again");
console.log("Replacer 3: " + replacer3.getString());

const string4 = new Replacer("<string1>, <string2>")
    .replaceAll("<string1>", "first string")
    .replaceAll("<string2>", "second string")
    .getString();

console.log("Replacer 4: " + string4);

Io.deleteFile(tempTemplate);
