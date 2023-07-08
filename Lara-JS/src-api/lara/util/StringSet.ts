/**
 * ~Based on this code: https://stackoverflow.com/questions/4343746/is-there-a-data-structure-like-the-java-set-in-javascript
 *
 * Changed the name of functions 'contains' to the name used in ECMAScript 6 ('has').
 * 'remove' has not been changed to 'delete' because in LARA 'delete' is a keyword and cannot be used as a function name
 * Also, tweaked the functionality of 'add' and 'delete' to behave similarly to ECMAScript 6 Set.~
 *
 * Implementation changed to use a standard javascript Set instead of a custom implementation.
 *
 * @deprecated Use a standard Set instead
 */
export default class StringSet {
  private set: Set<string> = new Set<string>();

  /**
   *
   * @param args - Objects that will be transformed to Strings and used as the initial values of the set.
   */
  constructor(...args: string[]) {
    for (const arg of args) {
      this.add(arg);
    }
  }

  /**
   * Implement the generator. StringSet can be used e.g. in `for...of`
   *
   * @see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Iterators_and_Generators}
   * @see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Generator}
   * @see {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/yield*}
   */
  *[Symbol.iterator]() {
    yield* this.set;
  }

  /**
   *
   * @returns A new copy of the set
   */
  copy(): StringSet {
    const newStringSet = new StringSet();

    for (const value of this.values()) {
      newStringSet.add(value);
    }

    return newStringSet;
  }

  /**
   * Add an element to the set
   *
   * @param str - Element to be added
   * @returns The element `str` itself
   */
  add(str: string): string {
    this.set.add(str);
    return str;
  }

  /**
   * Check if element exists in the set
   *
   * @param str - Element to be checked
   * @returns True if exists, false otherwise
   */
  has(str: string) {
    return this.set.has(str);
  }

  /**
   * Remove an element from the set if it exists
   *
   * @param str - Element to be removed
   * @returns True if the element `str` existed and was removed, false
   * otherwise
   */
  remove(str: string): boolean {
    return this.set.delete(str);
  }

  /**
   *
   * @returns A list of the elements in the set
   */
  values(): string[] {
    return Array.from(this.set.values());
  }

  /**
   *
   * @returns True if the set is empty, false otherwise
   */
  isEmpty(): boolean {
    return this.set.size === 0;
  }

  /**
   *
   * @returns A comma seperated list of the values in this set,
   * delimited by brackets to denote a set like data-structure
   */
  toString(): string {
    return "{" + this.values().join(", ") + "}";
  }

  /**
   * Stores in this set the union of it with another another set
   */
  union(otherSet: StringSet): this {
    otherSet.values().forEach((value) => {
      this.add(value);
    });

    return this;
  }

  /**
   * Stores in this set the intersection of it with another another set
   */
  intersection(otherSet: StringSet): this {
    for (const el of this) {
      if (!otherSet.has(el)) {
        this.remove(el);
      }
    }

    return this;
  }

  /**
   * Stores in this set the difference of it with another another set (i.e.
   * `this - otherSet`). Notice that this is not equivalent to `otherSet - thisSet`.
   */
  difference(otherSet: StringSet): this {
    for (const el of otherSet) {
      if (this.has(el)) {
        this.remove(el);
      }
    }

    return this;
  }

  /**
   *
   * @param setA -
   * @param setB -
   * @returns A new set with the union of sets A and B
   */
  static union(setA: StringSet, setB: StringSet): StringSet {
    return setA.copy().union(setB);
  }

  /**
   *
   * @param setA -
   * @param setB -
   * @returns A new set with the insersection of sets A and B
   */
  static intersection(setA: StringSet, setB: StringSet): StringSet {
    return setA.copy().intersection(setB);
  }

  /**
   *
   * @param setA -
   * @param setB -
   * @returns A new set with the difference of sets A and B, i.e.
   * `A - B`. Note that is different from `B - A`.
   */
  static difference(setA: StringSet, setB: StringSet): StringSet {
    return setA.copy().difference(setB);
  }
}
