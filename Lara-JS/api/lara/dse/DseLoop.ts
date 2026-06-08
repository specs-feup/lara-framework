import { LaraJoinPoint } from "../../LaraJoinPoint.ts";
import Metric from "../metrics/Metric.ts";
import DseVariant from "./DseVariant.ts";

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
