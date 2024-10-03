import lara.Io;
import lara.util.Replacer;

aspectdef ReplacerTest

	var replacer1 = new Replacer("a template <string_to_replace>");
	println("Replacer 1: " + replacer1.replaceAll("<string_to_replace>", "string").getString());

	var tempTemplate = "temp_template.txt";
	Io.writeFile(tempTemplate, "another template <string_to_replace>");

	var replacer2 = new Replacer(Io.getPath(tempTemplate));
	replacer2.replaceAll("<string_to_replace>", "string");
	println("Replacer 2: " + replacer2.getString());
	
	var replacer3 = Replacer.fromFilename(tempTemplate);
	replacer3.replaceAll("<string_to_replace>", "string again");
	println("Replacer 3: " + replacer3.getString());
	
	var string4 = (new Replacer("<string1>, <string2>"))
						.replaceAll("<string1>", "first string")
						.replaceAll("<string2>", "second string")
						.getString();
	
	println("Replacer 4: " + string4);
	
	Io.deleteFile(tempTemplate);
	
end

