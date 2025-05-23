import EnergyUnit from "@specs-feup/lara/api/lara/units/EnergyUnit.js";
import SiModifier from "@specs-feup/lara/api/lara/units/SiModifier.js";

console.log("1J in uJ: " + new EnergyUnit(SiModifier.MICRO).convert(1, "J"));
console.log("1GJ in MJ: " + new EnergyUnit(SiModifier.MEGA).convert(1, "GJ"));
console.log("1kJ in J: " + new EnergyUnit().convert(1, "kJ"));
