laraImport("lara.Platforms");

console.log("Is Windows? " + Platforms.isWindows());
Platforms.setLinux();
console.log("Is Windows? " + Platforms.isWindows());
console.log("Is Linux? " + Platforms.isLinux());
