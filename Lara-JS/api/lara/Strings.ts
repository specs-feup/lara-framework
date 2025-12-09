import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";

/**
 *  Utility methods related to Strings.
 */
export default class Strings {
  /**
   * Taken from here: https://stackoverflow.com/questions/154059/how-do-you-check-for-an-empty-string-in-javascript
   *
   * @returns true if the given string is blank or contains only white-space
   *
   * @deprecated Use JS native methods instead. You can check the link above for more information.
   */
  static isEmpty(string: string) {
    const realString: string = string.toString();
    return realString.length === 0 || !realString.trim();
  }

  /**
   * Equivalent to JS 'replace'.
   *
   * @param string - the string to process
   * @param oldSequence - the sequence we want to replace. To replace all occurrences, use a Regex with the 'g' modifier (e.g., /<regex>/g). Regexes in string format automatically assumes the 'g' modifier.
   * @param newSequence - the new value that will be used instead of the old value
   *
   * @returns the string after the replacement is done
   *
   * @deprecated Use the JS 'replace' method instead
   */
  static replacer(
    string: string,
    oldSequence: string | RegExp,
    newSequence: string
  ) {
    if (typeof oldSequence === "string") {
      oldSequence = new RegExp(oldSequence, "g");
    }

    return string.replace(oldSequence, newSequence);
  }

  /**
   * Escapes HTML code.
   *
   * @returns String with escaped code
   */
  static escapeHtml(html: string) {
    return JavaTypes.ApacheStrings.escapeHtml(html);
  }

  /**
   * Escapes JSON content.
   *
   * @returns String with escaped code
   */
  static escapeJson(jsonContents: string): string {
    return JavaTypes.SpecsStrings.escapeJson(jsonContents);
  }

  /**
   * Iterates over the given string, line-by-line, looking for the given prefix. If found, returns the contents of the line after the prefix.
   *
   * @returns
   */
  static extractValue(
    prefix: string,
    contents: string,
    errorOnUndefined = false
  ): string | undefined {
    const lines = new JavaTypes.StringLines(contents);

    while (lines.hasNextLine()) {
      const line = lines.nextLine().trim();

      if (!line.startsWith(prefix)) {
        continue;
      }

      return line.substring(prefix.length);
    }

    if (errorOnUndefined) {
      throw (
        "Could not extract a value with prefix '" +
        prefix +
        "' from output '" +
        contents +
        "'"
      );
    }

    return undefined;
  }

  static asLines(string?: string): JavaClasses.List<string> | undefined {
    if (string === undefined) {
      return undefined;
    }

    return JavaTypes.StringLines.getLines(string.toString());
  }

  /**
   * @param value - The value to convert to Json.
   *
   * @returns A string representing the given value in the Json format.
   *
   * @deprecated Use the JS 'JSON.stringify' method instead
   */
  static toJson(value: object): string {
    return JSON.stringify(value);
  }

  /**
   * Returns a random unique identifier.
   */
  static uuid(): string {
    let uuid = JavaTypes.Uuid.randomUUID().toString();

    // Regular expression and 'g' is needed to replace all occurences
    uuid = Strings.replacer(uuid, /-/g, "_");
    return "id_" + uuid;
  }

  /**
   *  Normalizes a given string:
   * 1) Replaces \\r\\n with \\n
   * 2) Trims lines and removes empty lines
   */
  static normalize(string: string) {
    return JavaTypes.SpecsStrings.normalizeFileContents(string, true);
  }

  /**
   * Converts a given object to a XML string.
   *
   * @param object - The object to serialize to XML.
   *
   * @returns The XML representation of the object.
   */
  static toXml(object: object): string {
    return JavaTypes.XStreamUtils.toString(object);
  }

  /**
   * Converts a given XML string to an object.
   *
   * @param xmlString - The XML representation of the object.
   *
   * @returns The deserialized object.
   */
  static fromXml(xmlString: string): object {
    return JavaTypes.XStreamUtils.from(xmlString, JavaTypes.Object.class);
  }
}
