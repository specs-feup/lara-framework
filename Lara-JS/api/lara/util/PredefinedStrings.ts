import { arrayFromArgs } from "../core/LaraCore.js";

/**
 * Represents a set of predefined strings.
 */
export default class PredefinedStrings {
  private _name: string;
  private _strict: boolean;
  private _valuesSet: Set<string> = new Set();

  constructor(name: string, strict: boolean, ...values: string[]) {
    this._name = name;
    this._strict = strict;

    values = arrayFromArgs(values) as string[];
    for (const value of values) {
      this._valuesSet.add(value.toString());
    }
  }

  /**
   * @returns Available values.
   */

  values(): string[] {
    return Array.from(this._valuesSet.values());
  }

  /**
   * @returns True if the given String is a valid value.
   */
  isValid(value: string): boolean {
    return this._valuesSet.has(value);
  }

  test(value: string): boolean {
    if (!this.isValid(value)) {
      const message = `Invalid ${
        this._name
      } '${value}'. Available values: ${this.values().join(", ")}`;
      if (this._strict) {
        throw message;
      }

      console.log(message);
      return false;
    }

    return true;
  }

  parse(...args: string[]): string[] {
    const argsArray = arrayFromArgs(args) as string[];

    // Clear benchmarks
    const parsedValues: string[] = [];
    for (const arg of argsArray) {
      if (!this.test(arg)) {
        continue;
      }

      parsedValues.push(arg);
    }

    return parsedValues;
  }
}
