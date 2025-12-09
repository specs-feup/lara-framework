import { LaraJoinPoint } from "../../LaraJoinPoint.js";
import Metric from "../metrics/Metric.js";
import DseVariant from "./DseVariant.js";

/**
 * Performs Design-Space Exploration.
 *
 */
export default abstract class DseLoop {
  abstract execute(
    $scope: LaraJoinPoint,
    $measure: LaraJoinPoint,
    dseVariants: DseVariant,
    metrics: Metric[],
    outputFolder: string,
    numExec: number
  ): void;
}
