laraImport("lara.util.Checkpoint");
laraImport("lara.Io");
laraImport("lara.System");
laraImport("lara.util.TimeUnits");


const checkpoint = new Checkpoint("test_checkpoint");

let object = {};
object.aNumber = 10;
object.aString = "Hello";
object.anObject = {};
object.anObject.anotherNumber = 100;
object.anArray = [1, 2, 3, 5, 7, 11];

let checkedObject = checkpoint.monitor(object);

// No interval set, perform immediate checkpoint
checkpoint.save();

// Check that checkpoint file exists
console.log("checkpoint exists: " + Io.isFile(checkpoint.getCheckpointFile()));

// Change number
checkedObject.aNumber = 20;

// Set a minum interval of 1 second
checkpoint.setInterval(1, TimerUnit.SECONDS);

// Save immediatly, there should be no changes due to the interval
checkpoint.save();

console.log("Is 10? " + Io.readJson(checkpoint.getCheckpointFile()).aNumber);

// Sleep for 1 second
System.sleep(1000);

// Save again
checkpoint.save();

// Check that number is saved
console.log("Is 20? " + Io.readJson(checkpoint.getCheckpointFile()).aNumber);

// Print saved object
printObject(Io.readJson(checkpoint.getCheckpointFile()));

// Stop checkpoint
checkpoint.stop();
