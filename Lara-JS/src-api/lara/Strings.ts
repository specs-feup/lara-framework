import lara._JavaTypes;
import lara.Check;
//import lara.Io;

/**
 *  Utility methods related to Strings.
 *
 * @class
 */
var Strings = {};

/**
 * Taken from here: https://stackoverflow.com/questions/154059/how-do-you-check-for-an-empty-string-in-javascript
 *
 * @return true if the given string is blank or contains only white-space
 */
Strings.isEmpty = function(string) {
	var realString = string.toString();
	return (realString.length === 0 || !realString.trim());
	//return (string.length === 0 || !string.trim());
}

/**
 * Equivalent to JS 'replace'.
 *
 * @param {String} string - the string to process
 * @param {String|Regex} oldSequence - the sequence we want to replace. To replace all occurrences, use a Regex with the 'g' modifier (e.g., /<regex>/g). Regexes in string format automatically assumes the 'g' modifier.
 * @param {String} newSequence - the new value that will be used instead of the old value
 *
 * @return the string after the replacement is done
 */
Strings.replacer = function(string, oldSequence, newSequence) {
	//_JavaTypes.getType(_JavaTypes.LaraApiTools);
	//return LaraApiTools.replacer(string, oldSequence, newSequence);
	//return _JavaTypes.getLaraApiTools().replacer(string, oldSequence, newSequence);
	
	if(isString(oldSequence)) {
		oldSequence = new RegExp(oldSequence, 'g');
	}
	
	Check.isRegex(oldSequence, "Strings.replacer()");
	
	// Using JS function defined in LaraCode.js, in order to be able to use the function 'replace' (which is a reserved keyword in LARA)
	return stringReplacer(string, oldSequence, newSequence);
}

/**
 * Escapes HTML code.
 *
 * @return String with escaped code
 */
Strings.escapeHtml = function(html) {
	return ApacheStrings.escapeHtml(html);
}

/**
 * Escapes JSON content.
 *
 * @return {string} String with escaped code
 */
Strings.escapeJson = function(jsonContents) {
	return SpecsStrings.escapeJson(jsonContents);
}

/**
 * Iterates over the given string, line-by-line, looking for the given prefix. If found, returns the contents of the line after the prefix.
 *
 * @returns {string} 
 */
Strings.extractValue = function(prefix, contents, errorOnUndefined) {
	checkType(prefix, "string", "Strings.extractValue::prefix");
	checkType(contents, "string", "Strings.extractValue::contents");
	
	if(errorOnUndefined === undefined) {
		errorOnUndefined = false;
	}
	
	var lines = StringLines.newInstance(contents);
	while(lines.hasNextLine()) {
		var line = lines.nextLine().trim();
		
		if(!line.startsWith(prefix)) {
			continue;
		}
		
		return line.substring(prefix.length);
	}

	if(errorOnUndefined) {
		throw "Could not extract a value with prefix '" + prefix + "' from output '" + contents + "'";
	}
	
	return undefined;	
}

Strings.asLines = function(string) {
	if(string === undefined) {
		return undefined;
	}
	
	return StringLines.getLines(string.toString());
}


/**
 * @param {*} value - The value to convert to Json.
 * 
 * @return {string} A string representing the given value in the Json format.
 */
Strings.toJson = function(value) {
	return JSON.stringify(value);
}

/**
 * Returns a random unique identifier.
 */
Strings.uuid = function() {
	var uuid = _JavaTypes.getUuid().randomUUID().toString();
	
	// Regular expression and 'g' is needed to replace all occurences
	uuid = Strings.replacer(uuid, /-/g, "_");
	return "id_" + uuid;
}

/**
 *  Normalizes a given string:
 * 1) Replaces \r\n with \n
 * 2) Trims lines and removes empty lines
 */
Strings.normalize = function(string) {
	return _JavaTypes.getSpecsStrings().normalizeFileContents(string, true);
}


/**
 * @param {Object|J#java.io.File} original - The original text
 * @param {Object|J#java.io.File} revised - The revised text
 */
 /*
Strings.diff = function(original, revised) {
	
	// Covert to String
	var originalString = Check.isJavaFile(original) ? Io.readFile(original) : original.toString();
	var revisedString = Check.isJavaFile(revised) ? Io.readFile(revised) : revised.toString();

	return _JavaTypes.getJavaDiff().getDiff(originalString, revisedString);
}
*/


// * Comparison is done line-by-line, and 
// Compare strings line-by-line

/**
 * Converts a given object to a XML string.
 *
 * @param {Object} object - The object to serialize to XML.
 * 
 * @return {String} The XML representation of the object.
 */
Strings.toXml = function(object) {
	return _JavaTypes.getXStreamUtils().toString(object);
}

/**
 * Converts a given XML string to an object.
 *
 * @param {String} xmlString - The XML representation of the object.
 * 
 * @return {Object} The deserialized object. 
 */
Strings.fromXml = function(xmlString) {
	return _JavaTypes.getXStreamUtils().from(xmlString, _JavaTypes.getType("java.lang.Object").class);
}
