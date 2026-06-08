/**
 * Data about a metric.
 */
export default class MetricResult {
  value: number;
  unit: string;

  constructor(value: number, unit: string) {
    this.value = value;
    this.unit = unit;
  }

  /**
   * @returns The value of the metric
   */
  getValue(): number {
    return this.value;
  }

  /**
   * @returns The unit of the metric value
   */
  getUnit(): string {
    return this.unit;
  }
}
