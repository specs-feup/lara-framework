/**
 * Base class for iterators in Lara.
 *
 * @deprecated This class is not a true iterable object. Use the native JS implementations instead.
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Iterators_and_Generators
 */
export default class LaraIterator {
    jsIterator() {
        return new JsIterator(this);
    }
}
/**
 * Iterator that wraps a LaraIterator and implements the JS iterator interface.
 *
 * @deprecated This class is not a true iterable object. Use the native JS implementations instead.
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Iterators_and_Generators
 */
export class JsIterator {
    laraIterator;
    constructor(laraIterator) {
        this.laraIterator = laraIterator;
    }
    next() {
        if (this.laraIterator.hasNext()) {
            return { value: this.laraIterator.next(), done: false };
        }
        return { done: true };
    }
}
//# sourceMappingURL=LaraIterator.js.map