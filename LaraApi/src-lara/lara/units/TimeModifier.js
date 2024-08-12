import SiModifier from "./SiModifier.js";
/**
 * ! If you are ever in need of using this class, please PLEASE refactor it.
 * ! Just do it. I did not have the time to do it myself and did not want to break compatibility with the old Margot APIs.
 */
export default class TimeModifier extends SiModifier {
    static SECOND = TimeModifier._unitModifier.newModifier("s", "second", 1);
    static MINUTE = TimeModifier._unitModifier.newModifier("min", "minute", 60);
    static HOUR = TimeModifier._unitModifier.newModifier("h", "hour", 60 * 60);
    static DAY = TimeModifier._unitModifier.newModifier("d", "day", 60 * 60 * 24);
}
//# sourceMappingURL=TimeModifier.js.map