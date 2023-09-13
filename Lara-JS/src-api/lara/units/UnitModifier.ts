/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class UnitModifier {
  private _factorTable: Record<string, number> = {};
  private _validModifiers: Set<string> = new Set();
  private _names: Record<string, string> = {};
  private _namesToModifier: Record<string, string> = {};
  private _base;

  constructor(base: string, baseModifier?: UnitModifier) {
    this._base = base;

    if (baseModifier !== undefined) {
      for (const modifier of baseModifier.values()) {
        this.newModifier(
          modifier,
          baseModifier._names[modifier],
          baseModifier._factorTable[modifier]
        );
      }
    }
  }

  getBase(): string {
    return this._base;
  }

  newModifier(modifier: string, name: string, factor: number) {
    // Add name
    this._names[modifier] = name;
    this._namesToModifier[name] = modifier;

    // Add factor
    this._factorTable[modifier] = factor;

    // Add modifier to the set
    this._validModifiers.add(modifier);

    return modifier;
  }

  convert(value: number, fromModifier: string, toModifier: string) {
    this.checkModifier(fromModifier, "UnitModifier.convert::fromModifier");
    this.checkModifier(toModifier, "UnitModifier.convert::toModifier");

    const fromFactor = this._factorTable[fromModifier];
    // test for undefined

    const toFactor = this._factorTable[toModifier];
    // test for undefined

    const conversionFactor = toFactor / fromFactor;

    return value / conversionFactor;
  }

  checkModifier(modifier: string, source: string): void {
    if (!this.isValid(modifier)) {
      throw new Error(
        `${source}: ${modifier} is not a valid modifier. Valid modifiers are: ${this._validModifierString()}`
      );
    }
  }

  private _validModifierString(): string {
    const stringValues = [];
    for (const modifier of this._validModifiers.values()) {
      const currentModifier =
        this._names[modifier] +
        (modifier.length === 0 ? "" : "(" + modifier + ")");
      stringValues.push(currentModifier);
    }

    return stringValues.join(", ");
  }

  isValid(modifier: string): boolean {
    return this._validModifiers.has(modifier);
  }

  getModifierByName(name: string) {
    return this._namesToModifier[name];
  }

  values(): string[] {
    return Array.from(this._validModifiers.values());
  }

  normalize(modifier: string): string {
    // Check if unit is specified by name
    const modifierByName = this.getModifierByName(modifier);

    if (modifierByName !== undefined) {
      return modifierByName;
    }

    return modifier;
  }
}
