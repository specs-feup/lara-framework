import TimeUnit from "@specs-feup/lara/api/lara/units/TimeUnit.js";

console.log("10ms in us: " + TimeUnit.micro().convert(10, "ms"));
console.log("1 day in hours: " + TimeUnit.hour().convert(1, "days"));
