import lara.units.EnergyUnit;
import lara.units.SiModifier;

aspectdef EnergyUnitTest

	println("1J in uJ: " + (new EnergyUnit(SiModifier.MICRO)).convert(1, "J"));
	println("1GJ in MJ: " + (new EnergyUnit(SiModifier.MEGA)).convert(1, "GJ"));
	println("1kJ in J: " + (new EnergyUnit()).convert(1, "kJ"));

end
