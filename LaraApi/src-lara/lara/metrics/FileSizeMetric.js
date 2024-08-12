import Metric from "./Metric.js";
import MetricResult from "./MetricResult.js";
/**
 * Measures the size of the compiled executable.
 */
export default class FileSizeMetric extends Metric {
    constructor() {
        super("File Size");
    }
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    instrument($start, $end = $start) { }
    // Override
    report(processExecutor) {
        const exeFile = processExecutor.getExecutableFile();
        const fileSize = exeFile !== undefined
            ? exeFile.length()
            : -1;
        return new MetricResult(fileSize, this.getUnit());
    }
    getImport() {
        return "lara.metrics.FileSizeMetric";
    }
    getUnit() {
        return "bytes";
    }
}
//# sourceMappingURL=FileSizeMetric.js.map