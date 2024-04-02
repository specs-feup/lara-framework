import UnitWithModifier from "./UnitWithModifier.js";
import SiModifier from "./SiModifier.js";
/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class SiUnit extends UnitWithModifier {
    constructor(baseUnit, modifier) {
        super(SiModifier.getUnitModifier(), baseUnit, modifier);
    }
}
//# sourceMappingURL=SiUnit.js.map