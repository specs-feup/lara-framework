export var TimerUnit;
(function (TimerUnit) {
    TimerUnit[TimerUnit["NANOSECONDS"] = 1] = "NANOSECONDS";
    TimerUnit[TimerUnit["MICROSECONDS"] = 2] = "MICROSECONDS";
    TimerUnit[TimerUnit["MILLISECONDS"] = 3] = "MILLISECONDS";
    TimerUnit[TimerUnit["SECONDS"] = 4] = "SECONDS";
    TimerUnit[TimerUnit["MINUTES"] = 5] = "MINUTES";
    TimerUnit[TimerUnit["HOURS"] = 6] = "HOURS";
    TimerUnit[TimerUnit["DAYS"] = 7] = "DAYS";
})(TimerUnit || (TimerUnit = {}));
const timerUnitData = {
    [TimerUnit.NANOSECONDS]: {
        cppTimeUnit: "nanoseconds",
        unitString: "ns",
        magnitudeFactorSeconds: 1000000000,
        magnitudeFactorNanoseconds: 1,
    },
    [TimerUnit.MICROSECONDS]: {
        cppTimeUnit: "microseconds",
        unitString: "us",
        magnitudeFactorSeconds: 1000000,
        magnitudeFactorNanoseconds: 1000,
    },
    [TimerUnit.MILLISECONDS]: {
        cppTimeUnit: "milliseconds",
        unitString: "ms",
        magnitudeFactorSeconds: 1000,
        magnitudeFactorNanoseconds: 1000000,
    },
    [TimerUnit.SECONDS]: {
        cppTimeUnit: "seconds",
        unitString: "s",
        magnitudeFactorSeconds: 1,
        magnitudeFactorNanoseconds: 1000000000,
    },
    [TimerUnit.MINUTES]: {
        cppTimeUnit: "minutes",
        unitString: "minutes",
        magnitudeFactorSeconds: "1 / 60",
        magnitudeFactorNanoseconds: 60000000000,
    },
    [TimerUnit.HOURS]: {
        cppTimeUnit: "hours",
        unitString: "hours",
        magnitudeFactorSeconds: "1 / 3600",
        magnitudeFactorNanoseconds: 3600000000000,
    },
    [TimerUnit.DAYS]: {
        cppTimeUnit: undefined,
        unitString: "days",
        magnitudeFactorSeconds: "1 / 86400",
        magnitudeFactorNanoseconds: 86400000000000,
    },
};
export default class TimeUnits {
    #unit;
    constructor(unit = TimerUnit.MILLISECONDS) {
        this.#unit = unit;
    }
    get unit() {
        return this.#unit;
    }
    set unit(unit) {
        this.#unit = unit;
    }
    getUnitsString() {
        return timerUnitData[this.#unit].unitString;
    }
    getCppTimeUnit() {
        return timerUnitData[this.#unit].cppTimeUnit;
    }
    getMagnitudeFactorFromSeconds() {
        return timerUnitData[this.#unit].magnitudeFactorSeconds;
    }
    getMagnitudeFactorFromNanoseconds() {
        return timerUnitData[this.#unit].magnitudeFactorNanoseconds;
    }
    toNanos(duration) {
        return duration * this.getMagnitudeFactorFromNanoseconds();
    }
}
//# sourceMappingURL=TimeUnits.js.map