/**
 * @deprecated Not relevant anymore.
 */
export default class Debug {
    /**
     * Collects the properties of an object that are of type function.
     * Taken from here: https://stackoverflow.com/questions/5842654/how-to-get-an-objects-methods
     *
     * @param obj - An object
     *
     * @deprecated Use the recommendations in this post https://stackoverflow.com/questions/39544789/get-class-methods-in-typescript
     */
    static getFunctions(obj) {
        const res = [];
        for (const m in obj) {
            if (typeof obj[m] == "function") {
                res.push(m);
            }
        }
        return res;
    }
    /**
     * Collects the properties of an object that are of type object.
     *
     * @deprecated Use the recommendations in this post https://stackoverflow.com/questions/39544789/get-class-methods-in-typescript
     */
    static getObjects(obj) {
        const res = [];
        for (const m in obj) {
            if (typeof obj[m] == "object") {
                res.push(m);
            }
        }
        return res;
    }
}
//# sourceMappingURL=Debug.js.map