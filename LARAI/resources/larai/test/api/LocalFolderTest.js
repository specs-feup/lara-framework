laraImport("lara.util.LocalFolder");

const localFolder = new LocalFolder("./");

console.log("FileList:" + localFolder.getFileList());
console.log("FileList ANT:" + localFolder.getFileList("ant"));
