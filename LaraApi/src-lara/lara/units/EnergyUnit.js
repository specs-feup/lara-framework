import SiUnit from "./SiUnit.js";
import SiModifier from "./SiModifier.js";
/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class EnergyUnit extends SiUnit {
    constructor(siModifier = SiModifier.BASE) {
        // Parent constructor
        super("J", siModifier);
    }
    static joule() {
        return new SiUnit("J", SiModifier.BASE);
    }
    static milli() {
        return new SiUnit("J", SiModifier.MILLI);
    }
    static micro() {
        return new SiUnit("J", SiModifier.MICRO);
    }
    /**
     * Override that discards parameter 'unitHasBaseName' and always requires the suffix 'J'.
     */
    convert(value, unit) {
        return super.convert(value, unit, true);
    }
}
//# sourceMappingURL=EnergyUnit.js.map