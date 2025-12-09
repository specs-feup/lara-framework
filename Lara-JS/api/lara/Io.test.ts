import { describe, it, expect, beforeAll, afterAll } from "bun:test";
import Io from "./Io.js";
import JavaTypes, { JavaClasses } from "./util/JavaTypes.js";

describe("Io", () => {
    
    let testFolder:any;
    
    beforeAll(() => {
        //TODO - create mock to avoid file creation
        testFolder = Io.mkdir("__ioTest__");
        Io.writeFile(Io.getPath(testFolder, "test1.txt"), "test1");
        Io.writeFile(Io.getPath(testFolder, "test2.txt"), "test2");
        Io.writeFile(Io.getPath(testFolder, "test3.doc"), "test3");
    })

    describe("getPaths with args", () => {
        it("returns the list of text files in a folder when we pass '.txt' in args", () => {
            
            const files: JavaClasses.File[] = Io.getPaths(testFolder, "*.txt");
	        expect(files.map(file => file.getName()).sort().join()).toBe("test1.txt,test2.txt");	
        
        })
    })
    describe("getPaths without args", () => {
        it("returns the list of files in the folder", () => {
              
            const files: JavaClasses.File[] = Io.getPaths(testFolder);
	        expect(files.map(file => file.getName()).sort().join()).toBe("test1.txt,test2.txt,test3.doc");	
        })
    })
    afterAll(() => {
        Io.deleteFolder(testFolder);
    })
})