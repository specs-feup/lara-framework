import { LaraJoinPoint } from "../../LaraJoinPoint.ts";
import ProcessExecutor from "../util/ProcessExecutor.ts";
import Metric from "./Metric.ts";
import MetricResult from "./MetricResult.ts";

/**
 * Measures the size of the compiled executable.
 */
export default class FileSizeMetric extends Metric {
  constructor() {
    super("File Size");
  }

  // oxlint-disable-next-line typescript/no-unused-vars
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
