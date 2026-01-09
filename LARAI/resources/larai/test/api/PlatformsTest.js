import Platforms from "@specs-feup/lara/api/lara/Platforms.js";

console.log("Is Windows? " + Platforms.isWindows());
Platforms.setLinux();
console.log("Is Windows? " + Platforms.isWindows());
console.log("Is Linux? " + Platforms.isLinux());
