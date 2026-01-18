import System from "@specs-feup/lara/api/lara/System.js";

console.log("Logical cores working: " + (System.getNumLogicalCores() > 0));
console.log("Testing System.nanos()");
System.nanos();
