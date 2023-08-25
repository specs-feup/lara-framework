import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import TimeUnits, { TimerUnit } from "../util/TimeUnits.js";

/**
 * Timer object, for timing sections of code.
 */
export default abstract class TimerBase<T extends LaraJoinPoint> {
  timeUnits: TimeUnits;
  filename: string | undefined;
  protected printUnit: boolean = true;
  protected print: boolean = true;
  private afterJp: T | undefined = undefined;

  constructor(unit?: TimerUnit, filename?: string) {
    this.timeUnits = new TimeUnits(unit);
    this.filename = filename;
  }

  /**
   * Times the code of a given section.
   *
   * @param $start - Starting point of the time measure
   * @param prefix - Message that will appear before the time measure. If undefined, empty string will be used.
   * @param $end - Ending point of the time measure. If undefined, measure is done around starting point.
   * @returns Name of the variable that contains the value of the elapsed time.
   */
  abstract time($start: T, prefix?: string, $end?: T): string | undefined;

  setPrintUnit(printUnit: boolean) {
    this.printUnit = printUnit;
    return this;
  }

  setPrint(print: boolean) {
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
  setAfterJp($afterJp: T | undefined) {
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
  protected _timeValidate(
    $start: any,
    $end?: any,
    functionJpName: string = "function"
  ) {
    const $function = $start.getAncestor(functionJpName);

    if ($function === undefined) {
      console.log(
        "Timer: tried to measure time at join point " +
          $start.joinPointType +
          ", but it is not inside a function"
      );
      return false;
    }

    if ($end !== undefined) {
      const $endFunction = $end.getAncestor(functionJpName);

      if ($endFunction === undefined) {
        console.log(
          "Timer: tried to end measuring time at joinpoit " +
            $end.joinPointType +
            ", but it is not inside a function"
        );
        return false;
      }

      // TODO: Checking if it is the same function not implemented yet, requires attribute '$function.id'
    }

    return true;
  }
}
