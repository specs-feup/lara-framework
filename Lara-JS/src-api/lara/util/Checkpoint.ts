import lara.util.TimeUnits;
import lara.Io;

/**
 * Allows checkpointing of generic objects.
 */
var Checkpoint = function(checkpointName) {

	checkDefined(checkpointName, "checkpointName", "Checkpoint:new");
	this.checkpointName = checkpointName;
	this.interval = undefined;
	this.timeUnit = undefined;
	this.currentObject = undefined;
	this.timestamp = undefined;
};

/**
 * Monitors the given object. If there is a saved filed from a previous execution, returns the saved object instead.
 *
 * @return the object that will be monitored
 */
Checkpoint.prototype.monitor = function(object) {
	checkDefined(object, "object", "Checkpoint.monitor");
	
	// Check if saved data already exists
	var checkpointFilename = this._getCheckpointFile();
	if(Io.isFile(checkpointFilename)) {
		this.currentObject = Io.readJson(checkpointFilename);
	} else {
		this.currentObject = object;
	}
	
	return this.currentObject;
}

Checkpoint.prototype.save = function() {

	// If no interval, manual checking
	if(this.interval === undefined) {
		this._saveManual();
		return;
	}
	
	var systemClass = Java.type("java.lang.System");
	
	// Time interval is defined
	// If no timestamp yet, do nothing and record time
	if(this.timestamp === undefined) {	
		this.timestamp = systemClass.nanoTime();
		return;
	}
	
	// Get timestamp, check if passed interval
	var currentTime = systemClass.nanoTime();
	var passedTime = currentTime - this.timestamp;
	
	// If passed more time than the set interval, save object and update timestamp
	if(passedTime > this.timeUnit.toNanos(this.interval)) {
		this._saveManual();
		this.timestamp = currentTime;
	}
	
}

Checkpoint.prototype.setInterval = function(interval, timeUnit) {

	checkDefined(interval, "duration", "Checkpoint:setInterval");
	this.interval = interval;
	
	if(timeUnit === undefined) {
		this.timeUnit = new TimeUnits(TimerUnit.SECONDS);
	} else {
		this.timeUnit = new TimeUnits(timeUnit);
	}
}

Checkpoint.prototype.stop = function() {
	// Delete checkpoint
	Io.deleteFile(this._getCheckpointFile());
	
	// Clean state
	this.currentObject = undefined;
	this.timestamp = undefined;
}


/**
 * Saves the current object.
 */
Checkpoint.prototype._saveManual = function() {
	Io.writeJson(this._getCheckpointFile(), this.currentObject);
}

/**
 * @return the checkpoint file for the object being monitored
 */
Checkpoint.prototype._getCheckpointFile = function() {
	return "checkpoint_" + this.checkpointName + ".json";
}