import Io from "../Io.js";
import JavaTypes from "./JavaTypes.js";
import TimeUnits, { TimerUnit } from "./TimeUnits.js";
/**
 * Allows checkpointing of generic objects.
 */
export default class Checkpoint {
    checkpointName;
    interval = undefined;
    currentObject = undefined;
    timestamp = undefined;
    constructor(checkpointName) {
        this.checkpointName = checkpointName;
    }
    /**
     * Monitors the given object. If there is a saved filed from a previous execution, returns the saved object instead.
     *
     * @returns The object that will be monitored
     */
    monitor(object) {
        // Check if saved data already exists
        const checkpointFilename = this.getCheckpointFile();
        if (Io.isFile(checkpointFilename)) {
            this.currentObject = Io.readJson(checkpointFilename);
        }
        else {
            this.currentObject = object;
        }
        return this.currentObject;
    }
    save() {
        // If no interval, manual checking
        if (this.interval === undefined) {
            this.saveManual();
            return;
        }
        const systemClass = JavaTypes.System;
        // Time interval is defined
        // If no timestamp yet, do nothing and record time
        if (this.timestamp === undefined) {
            this.timestamp = systemClass.nanoTime();
            return;
        }
        // Get timestamp, check if passed interval
        const currentTime = systemClass.nanoTime();
        const passedTime = currentTime - this.timestamp;
        // If passed more time than the set interval, save object and update timestamp
        if (passedTime > this.interval) {
            this.saveManual();
            this.timestamp = currentTime;
        }
    }
    setInterval(interval, timeUnit = TimerUnit.SECONDS) {
        this.interval = new TimeUnits(timeUnit).toNanos(interval);
    }
    stop() {
        // Delete checkpoint
        Io.deleteFile(this.getCheckpointFile());
        // Clean state
        this.currentObject = undefined;
        this.timestamp = undefined;
    }
    /**
     * Saves the current object.
     */
    saveManual() {
        Io.writeJson(this.getCheckpointFile(), this.currentObject);
    }
    /**
     * @returns The checkpoint file for the object being monitored
     */
    getCheckpointFile() {
        return `checkpoint_${this.checkpointName}.json`;
    }
}
//# sourceMappingURL=Checkpoint.js.map