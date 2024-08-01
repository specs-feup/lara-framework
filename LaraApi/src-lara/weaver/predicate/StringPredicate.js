import JpPredicate from "./JpPredicate.js";
export default class StringPredicate extends JpPredicate {
    name;
    constructor(name) {
        super();
        this.name = name;
    }
    jpName() {
        return this.name;
    }
    isLaraJoinPoint() {
        return false;
    }
    isInstance(jp) {
        return jp.instanceOf(this.name);
    }
}
//# sourceMappingURL=StringPredicate.js.map