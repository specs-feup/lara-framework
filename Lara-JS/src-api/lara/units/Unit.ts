/**
 * Base class that represents a unit of what is being measured in ANTAREX, e.g., time or energy.
 *
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default abstract class Unit {
  /**
   * Returns the name of the unit.
   *
   * This should not be called directly.
   */
  abstract getName(): string;

  /**
   * Converts the value, in the provided unit, to the unit this instance represents.
   *
   */
  abstract convert(value: number, unit: string): number;
}
