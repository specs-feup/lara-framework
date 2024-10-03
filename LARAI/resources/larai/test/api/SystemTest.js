laraImport("lara.System");

console.log("Logical cores working: " + (System.getNumLogicalCores() > 0));
console.log("Testing System.nanos()");
System.nanos();
