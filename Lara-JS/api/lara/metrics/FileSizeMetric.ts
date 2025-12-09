import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import ProcessExecutor from "../util/ProcessExecutor.js";
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
  instrument($start: LaraJoinPoint, $end: LaraJoinPoint = $start) {}

  // Override
  report(processExecutor: ProcessExecutor) {
    const exeFile = processExecutor.getExecutableFile();
    const fileSize =
      exeFile !== undefined
        ? (exeFile.length as unknown as () => number)()
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
