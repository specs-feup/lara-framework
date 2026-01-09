import LocalFolder from "@specs-feup/lara/api/lara/util/LocalFolder.js";

const localFolder = new LocalFolder("./");

console.log("FileList:" + localFolder.getFileList());
console.log("FileList ANT:" + localFolder.getFileList("ant"));
