/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class UnitModifier {
    _factorTable = {};
    _validModifiers = new Set();
    _names = {};
    _namesToModifier = {};
    _base;
    constructor(base, baseModifier) {
        this._base = base;
        if (baseModifier !== undefined) {
            for (const modifier of baseModifier.values()) {
                this.newModifier(modifier, baseModifier._names[modifier], baseModifier._factorTable[modifier]);
            }
        }
    }
    getBase() {
        return this._base;
    }
    newModifier(modifier, name, factor) {
        // Add name
        this._names[modifier] = name;
        this._namesToModifier[name] = modifier;
        // Add factor
        this._factorTable[modifier] = factor;
        // Add modifier to the set
        this._validModifiers.add(modifier);
        return modifier;
    }
    convert(value, fromModifier, toModifier) {
        this.checkModifier(fromModifier, "UnitModifier.convert::fromModifier");
        this.checkModifier(toModifier, "UnitModifier.convert::toModifier");
        const fromFactor = this._factorTable[fromModifier];
        // test for undefined
        const toFactor = this._factorTable[toModifier];
        // test for undefined
        const conversionFactor = toFactor / fromFactor;
        return value / conversionFactor;
    }
    checkModifier(modifier, source) {
        if (!this.isValid(modifier)) {
            throw new Error(`${source}: ${modifier} is not a valid modifier. Valid modifiers are: ${this._validModifierString()}`);
        }
    }
    _validModifierString() {
        const stringValues = [];
        for (const modifier of this._validModifiers.values()) {
            const currentModifier = this._names[modifier] +
                (modifier.length === 0 ? "" : "(" + modifier + ")");
            stringValues.push(currentModifier);
        }
        return stringValues.join(", ");
    }
    isValid(modifier) {
        return this._validModifiers.has(modifier);
    }
    getModifierByName(name) {
        return this._namesToModifier[name];
    }
    values() {
        return Array.from(this._validModifiers.values());
    }
    normalize(modifier) {
        // Check if unit is specified by name
        const modifierByName = this.getModifierByName(modifier);
        if (modifierByName !== undefined) {
            return modifierByName;
        }
        return modifier;
    }
}
//# sourceMappingURL=UnitModifier.js.map