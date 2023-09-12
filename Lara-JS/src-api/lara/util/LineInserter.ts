import LineIterator from "../iterators/LineIterator.js";
import { JavaClasses } from "./JavaTypes.js";

/**
 * Helps inserting lines.
 *
 */
export default class LineInserter {
  // TODO: Use system newline? Allow user to change newline?
  newLine = "\n";

  /**
   * Sets the new line.
   *
   * @param newLine - the new line to use.
   */
  setNewLine(newLine: string) {
    this.newLine = newLine;
  }

  /**
   *
   * @param contents - The contents where lines will be inserted.
   * @param linesToInsert - Maps line numbers to strings to insert.
   *
   * @returns the contents with the lines inserted.
   */
  add(
    contents: string | JavaClasses.File,
    linesToInsert: Record<number, string>
  ) {
    const lineIterator = new LineIterator(contents);

    let newContents = "";
    let currentLine = 0;
    while (lineIterator.hasNext()) {
      const line = lineIterator.next();
      currentLine++;

      // Check if there is a mapping for the current line
      const toInsert = linesToInsert[currentLine];
      if (toInsert !== undefined) {
        newContents += toInsert + this.newLine;
      }

      // Insert old content
      newContents += line + this.newLine;
    }

    return newContents;
  }
}
