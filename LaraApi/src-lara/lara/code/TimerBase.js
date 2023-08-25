import TimeUnits from "../util/TimeUnits.js";
/**
 * Timer object, for timing sections of code.
 */
export default class TimerBase {
    timeUnits;
    filename;
    printUnit = true;
    print = true;
    afterJp = undefined;
    constructor(unit, filename) {
        this.timeUnits = new TimeUnits(unit);
        this.filename = filename;
    }
    setPrintUnit(printUnit) {
        this.printUnit = printUnit;
        return this;
    }
    setPrint(print) {
        this.print = print;
        return this;
    }
    getUnit() {
        return this.timeUnits;
    }
    /**
     * @returns The last join point that was inserted after the $end mark
     */
    getAfterJp() {
        return this.afterJp;
    }
    /**
     * Sets the join point that should be returned by .getAfterJp().
     */
    setAfterJp($afterJp) {
        this.afterJp = $afterJp;
    }
    /**
     * Verifies that join point start is not undefined, that it is inside a function.
     * Additionally, if $end is not undefined, checks if it is inside the same function as $start.
     *
     * [Requires] global attribute 'ancestor'.
     *
     * @returns True if $start is a valid join point for the 'time' function
     */
    // TODO: This function should receive LaraJoinPoints but they do not have the getAncestor method
    _timeValidate($start, $end, functionJpName = "function") {
        const $function = $start.getAncestor(functionJpName);
        if ($function === undefined) {
            console.log("Timer: tried to measure time at join point " +
                $start.joinPointType +
                ", but it is not inside a function");
            return false;
        }
        if ($end !== undefined) {
            const $endFunction = $end.getAncestor(functionJpName);
            if ($endFunction === undefined) {
                console.log("Timer: tried to end measuring time at joinpoit " +
                    $end.joinPointType +
                    ", but it is not inside a function");
                return false;
            }
            // TODO: Checking if it is the same function not implemented yet, requires attribute '$function.id'
        }
        return true;
    }
}
//# sourceMappingURL=TimerBase.js.map