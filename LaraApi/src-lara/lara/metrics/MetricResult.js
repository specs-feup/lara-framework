/**
 * Data about a metric.
 */
export default class MetricResult {
    value;
    unit;
    constructor(value, unit) {
        this.value = value;
        this.unit = unit;
    }
    /**
     * @returns The value of the metric
     */
    getValue() {
        return this.value;
    }
    /**
     * @returns The unit of the metric value
     */
    getUnit() {
        return this.unit;
    }
}
//# sourceMappingURL=MetricResult.js.map