import UnitModifier from "./UnitModifier.js";
/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class SiModifier extends UnitModifier {
    static _unitModifier = new UnitModifier("");
    static GIGA = SiModifier._unitModifier.newModifier("G", "giga", 1e9);
    static MEGA = SiModifier._unitModifier.newModifier("M", "mega", 1e6);
    static KILO = SiModifier._unitModifier.newModifier("k", "kilo", 1e3);
    static BASE = SiModifier._unitModifier.newModifier("", "base", 1);
    static MILLI = SiModifier._unitModifier.newModifier("m", "milli", 1e-3);
    static MICRO = SiModifier._unitModifier.newModifier("u", "micro", 1e-6);
    static NANO = SiModifier._unitModifier.newModifier("n", "nano", 1e-9);
    static getUnitModifier() {
        return SiModifier._unitModifier;
    }
    static convert(value, fromModifier, toModifier) {
        return SiModifier._unitModifier.convert(value, fromModifier, toModifier);
    }
    static checkModifier(modifier, source) {
        SiModifier._unitModifier.checkModifier(modifier, source);
    }
    static isValid(modifier) {
        return SiModifier._unitModifier.isValid(modifier);
    }
    static getModifierByName(name) {
        return SiModifier._unitModifier.getModifierByName(name);
    }
}
//# sourceMappingURL=SiModifier.js.map