import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import ProcessExecutor from "../util/ProcessExecutor.js";
import MetricResult from "./MetricResult.js";

/**
 * Reprents a metric we want to extract.
 *
 */
export default abstract class Metric<T extends LaraJoinPoint = LaraJoinPoint> {
  name: string;

  constructor(name: string) {
    this.name = name;
  }

  getName(): string {
    return this.name;
  }

  /**
   * Instruments code so that when executed, it produces information about the metric to measure.
   * @param $start - join point representing the start point of the instrumentation
   * @param $end - join point representing the end point of the instrumentation
   */
  abstract instrument<U extends T, V extends T>($start: U, $end: V): void;

  /**
   * Extract metric.
   *
   * @param processExecutor - Instance after program execution
   * @returns Metric after program execution
   */
  abstract report(processExecutor: ProcessExecutor): MetricResult;

  /**
   * @returns A string with the LARA import of this Metric.
   */
  abstract getImport(): string;

  /**
   * @returns A string with the current unit of this metric.
   */
  abstract getUnit(): string;
}
