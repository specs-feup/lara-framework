import SiUnit from "./SiUnit.js";
import TimeModifier from "./TimeModifier.js";
/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class TimeUnit extends SiUnit {
    constructor(timeModifier) {
        super("s", timeModifier);
    }
    /**
     * Factory methods to create TimeUnits.
     * */
    static nano = function () {
        return new TimeUnit(TimeModifier.NANO);
    };
    static micro = function () {
        return new TimeUnit(TimeModifier.MICRO);
    };
    static milli = function () {
        return new TimeUnit(TimeModifier.MILLI);
    };
    static second = function () {
        return new TimeUnit(TimeModifier.BASE);
    };
    static minute = function () {
        return new TimeUnit(TimeModifier.MINUTE);
    };
    static hour = function () {
        return new TimeUnit(TimeModifier.HOUR);
    };
    static day = function () {
        return new TimeUnit(TimeModifier.DAY);
    };
    /**
     * Override that adds support to time-specific units (days, hours, minutes). Discards parameter 'unitHasBaseName'.
     */
    convert(value, unit) {
        // Time-specific units
        if (unit === "minute" || unit === "minutes") {
            return super.convert(value, "minute", true);
        }
        if (unit === "hour" || unit === "hours") {
            return super.convert(value, "hour", true);
        }
        if (unit === "day" || unit === "days") {
            return super.convert(value, "day", true);
        }
        // For SI modifiers, require base name
        return super.convert(value, unit, true);
    }
}
//# sourceMappingURL=TimeUnit.js.map