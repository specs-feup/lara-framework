/**
 * Based on this code: https://stackoverflow.com/questions/4343746/is-there-a-data-structure-like-the-java-set-in-javascript
 *
 * Changed the name of functions 'contains' to the name used in ECMAScript 6 ('has').
 * 'remove' has not been changed to 'delete' because in LARA 'delete' is a keyword and cannot be used as a function name
 * Also, tweaked the functionality of 'add' and 'delete' to behave similarly to ECMAScript 6 Set.
 *
 * @param {Object...} [args=[]] - Objects that will be transformed to Strings and used as the initial values of the set.
 */
export default class StringSet {
    setObj: { [key: string]: any } = {};
    val = {};

    constructor(...args: string[]) {
        for (const arg of args) {
            this.add(arg.toString());
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
        yield* Object.keys(this.setObj);
    }

    /**
     * @param {StringSet} s
     * @returns {Boolean} True, if `s` is an instance of StringSet
     */
    static isStringSet(s: any): boolean {
        return s instanceof StringSet;
    }

    /**
     *
     * @returns {StringSet} A new copy of the set
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
     * @param {String} str Element to be added
     * @returns The element `str` itself
     */
    add(str: string): string {
        this.setObj[str] = this.val;
        return str;
    }

    /**
     * Check if element exists in the set
     * @param {String} str Element to be checked
     * @returns True if exists, false otherwise
     */
    has(str: string) {
        return this.setObj[str] === this.val;
    }

    /**
     * Remove an element from the set if it exists
     * @param {String} str Element to be removed
     * @returns {Boolean} True if the element `str` existed and was removed, false
     * otherwise
     */
    remove(str: string): boolean {
        const hasValue = this.has(str);
        delete this.setObj[str];
        return hasValue;
    }

    /**
     *
     * @returns {String[]} A list of the elements in the set
     */
    values(): string[] {
        const values = [];
        for (const i in this.setObj) {
            if (this.setObj[i] === this.val) {
                values.push(i);
            }
        }
        return values;
    }

    /**
     *
     * @returns {Boolean} True if the set is empty, false otherwise
     */
    isEmpty(): boolean {
        for (const i in this.setObj) {
            return false;
        }

        return true;
    }

    /**
     *
     * @returns {String} A comma seperated list of the values in this set,
     * delimited by brackets to denote a set like data-structure
     */
    toString(): string {
        return "{" + this.values().join(", ") + "}";
    }

    /**
     * Stores in this set the union of it with another another set
     * @param {StringSet} otherSet
     * @returns {this}
     */
    union(otherSet: StringSet): this {
        if (!StringSet.isStringSet(otherSet)) {
            throw new Error("Invalid argument: must be instance of StringSet");
        }

        // for every element in the other set, add it to this set
        for (const el of otherSet) {
            this.add(el);
        }

        // return self object for chaining
        return this;
    }

    /**
     * Stores in this set the intersection of it with another another set
     * @param {StringSet} otherSet
     * @returns {this}
     */
    intersection(otherSet: StringSet): this {
        if (!StringSet.isStringSet(otherSet)) {
            throw new Error("Invalid argument: must be instance of StringSet");
        }

        // for every element in this set that does not exist in the other set,
        // remove it from this set
        for (const el of this) {
            if (!otherSet.has(el)) {
                this.remove(el);
            }
        }

        // return self object for chaining
        return this;
    }

    /**
     * Stores in this set the difference of it with another another set (i.e.
     * `this - otherSet`). Notice that is not equivalent to `otherSet - this`.
     * @param {StringSet} otherSet
     * @returns {this}
     */
    difference(otherSet: StringSet): this {
        if (!StringSet.isStringSet(otherSet)) {
            throw new Error("Invalid argument: must be instance of StringSet");
        }

        for (const el of otherSet) {
            if (this.has(el)) {
                this.remove(el);
            }
        }

        return this;
    }

    /**
     *
     * @param {StringSet} setA
     * @param {StringSet} setB
     * @returns {StringSet} A new set with the union of sets A and B
     */
    static union(setA: StringSet, setB: StringSet): StringSet {
        if (!StringSet.isStringSet(setA) || !StringSet.isStringSet(setB))
            throw new Error(
                "Invalid arguments: setA and setB must be instances of StringSet"
            );

        const setNew = setA.copy();
        return setNew.union(setB);
    }

    /**
     *
     * @param {StringSet} setA
     * @param {StringSet} setB
     * @returns {StringSet} A new set with the insersection of sets A and B
     */
    static intersection(setA: StringSet, setB: StringSet): StringSet {
        if (!StringSet.isStringSet(setA) || !StringSet.isStringSet(setB))
            throw new Error(
                "Invalid arguments: setA and setB must be instances of StringSet"
            );

        const setNew = setA.copy();
        return setNew.intersection(setB);
    }

    /**
     *
     * @param {StringSet} setA
     * @param {StringSet} setB
     * @returns {StringSet} A new set with the difference of sets A and B, i.e.
     * `A - B`. Note that is different from `B - A`.
     */
    static difference(setA: StringSet, setB: StringSet): StringSet {
        if (!StringSet.isStringSet(setA) || !StringSet.isStringSet(setB))
            throw new Error(
                "Invalid arguments: setA and setB must be instances of StringSet"
            );

        const setNew = setA.copy();
        return setNew.difference(setB);
    }
}