import Io from "./Io.js";
import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";

describe("Io", () => {

    describe("getPaths with args", () => {
        it("returns the list of text files in a folder when we pass '.txt' in args", () => {
            
            
            //TODO - create mock to avoid file creation
            
            const testFolder = Io.mkdir("__ioTest__");
            const testFile1= Io.writeFile(Io.getPath(testFolder, "test1.txt"), "test1");
	        const testFile2= Io.writeFile(Io.getPath(testFolder, "test2.txt"), "test2");
	        const testFile3= Io.writeFile(Io.getPath(testFolder, "test3.doc"), "test3");
            const files: JavaClasses.File[] = Io.getPaths(testFolder, "*.txt");
	        expect(files.map(file => file.getName()).sort().join()).toBe("test1.txt,test2.txt");	
            Io.deleteFolder(testFolder);
        
        })
    })
    describe("getPaths without args", () => {
        it("returns the list of files in the folder", () => {
              
            const testFolder = Io.mkdir("__ioTest__");
	        const testFile1: JavaClasses.File = Io.writeFile(Io.getPath(testFolder, "test1.txt"), "test1");
	        const testFile2: JavaClasses.File = Io.writeFile(Io.getPath(testFolder, "test2.txt"), "test2");
	        const testFile3: JavaClasses.File = Io.writeFile(Io.getPath(testFolder, "test3.doc"), "test3");
            const files: JavaClasses.File[] = Io.getPaths(testFolder);
	        expect(files.map(file => file.getName()).sort().join()).toBe("test1.txt,test2.txt,test3.doc");	
            Io.deleteFolder(testFolder);
        })
    })
});