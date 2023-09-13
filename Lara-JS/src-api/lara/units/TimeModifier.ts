import lara.units.UnitModifier;
import lara.units.SiModifier;

/**
 * @class
 */
var TimeModifier = {};

TimeModifier._unitModifier = new UnitModifier("s", SiModifier.getUnitModifier(), TimeModifier);

TimeModifier.SECOND = TimeModifier._unitModifier.newModifier("s", "second", 1);
TimeModifier.MINUTE = TimeModifier._unitModifier.newModifier("min", "minute", 60);
TimeModifier.HOUR = TimeModifier._unitModifier.newModifier("h", "hour", 60 * 60);
TimeModifier.DAY = TimeModifier._unitModifier.newModifier("d", "day", 60 * 60 * 24);


TimeModifier.getUnitModifier = function() {
	return TimeModifier._unitModifier;
}
