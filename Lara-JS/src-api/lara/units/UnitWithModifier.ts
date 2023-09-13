import Unit from "./Unit.js";
import UnitModifier from "./UnitModifier.js";

/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class UnitWithModifier extends Unit {
  private _unitModifier;
  private _baseUnit: string;
  private _modifier: string;

  constructor(unitModifier: UnitModifier, baseUnit: string, modifier?: string) {
    super();

    this._unitModifier = unitModifier;

    if (modifier == undefined) {
      modifier = unitModifier.getBase();
    }

    this._unitModifier.checkModifier(modifier, "UnitWithModifier::modifier");

    this._baseUnit = baseUnit;
    this._modifier = modifier;
  }

  getName() {
    return this._modifier + this._baseUnit;
  }

  /**
   * @param unit - Unit of the value.
   */
  convert(value: number, unit: string, unitHasBaseName: boolean = false): number {
    const fromModifier = this._extractModifier(unit, unitHasBaseName);

    return this._unitModifier.convert(value, fromModifier, this._modifier);
  }

  private _extractModifier(unit: string, unitHasBaseName: boolean) {
    let currentModifier = unit;

    // Check that unit ends with the baseUnit
    if (unitHasBaseName) {
      if (currentModifier.endsWith(this._baseUnit)) {
        currentModifier = currentModifier.substring(
          0,
          currentModifier.length - this._baseUnit.length
        );
      }
    }

    currentModifier = this._unitModifier.normalize(currentModifier);

    this._unitModifier.checkModifier(
      currentModifier,
      "UnitWithModifier._extractModifier::unit"
    );

    return currentModifier;
  }
}
