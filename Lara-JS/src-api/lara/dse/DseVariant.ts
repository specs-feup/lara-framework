import DseValues from "./DseValues.js";

/**
 * Represents a variant of the DSE.
 */
export default abstract class DseVariant {
  /**
   * @returns the type of this DseVariant.
   */
  abstract getType(): string;

  /**
   * @returns the names associated to this DseVariant.
   */
  abstract getNames(): string[];

  /**
   * @returns the DseValues associated to this DseVariant.
   */
  abstract getDseValues(): DseValues;
}
