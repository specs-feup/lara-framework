import { LaraJoinPoint } from "../../LaraJoinPoint.js";

/**
 * Class that measures the energy spent when executing a section of code.
 */
export default class EnergyBase<T extends LaraJoinPoint> {
  filename: string;
  printUnit: boolean = true;
  print: boolean = true;

  constructor(filename: string) {
    this.filename = filename;
  }

  /**
   * If true, suffixes 'J' to the end of the value.
   *
   */
  setPrintUnit(printUnit: boolean) {
    this.printUnit = printUnit;
    return this;
  }

  getPrintUnit() {
    return "J";
  }

  setPrint(print: boolean) {
    this.print = print;
    return this;
  }

  private warn(message: string) {
    console.log("[EnergyBase Warning]", message);
  }

  /**
   * Verifies that join point start is not undefined, that it is inside a function.
   * Additionally, if $end is not undefined, checks if it is inside the same function as $start.
   *
   * [Requires] global attribute 'ancestor'.
   *
   * @returns True if $start is a valid join point for the 'measure' function
   */
  // TODO: This function should receive LaraJoinPoints but they do not have the getAncestor method
  protected measureValidate($start?: any, $end?: any, functionJpName: string = "function") {
    if ($start === undefined) {
      this.warn("Energy: $start join point is undefined");
      return false;
    }

    if (typeof $start !== "object") {
      this.warn(
        "Energy: $start should be an object, it is a " +
          typeof $start +
          " instead"
      );
      return false;
    }

    const $function = $start.getAncestor(functionJpName);

    if ($function === undefined) {
      console.log(
        "Energy: tried to measure energy at joinpoit " +
          $start +
          ", but it is not inside a function"
      );
      return false;
    }

    if ($end !== undefined) {
      const typeofEnd = typeof $end;
      if (typeofEnd !== "object") {
        this.warn(
          "Energy: $end should be an object, it is a " + typeofEnd + " instead"
        );
        return false;
      }

      const $endFunction = $end.getAncestor(functionJpName);

      if ($endFunction === undefined) {
        console.log(
          "Energy: tried to end measuring energy at joinpoit " +
            $end +
            ", but it is not inside a function"
        );
        return false;
      }

      // TODO: Checking if it is the same function not implemented yet, requires attribute '$function.id'
    }

    return true;
  }
}
