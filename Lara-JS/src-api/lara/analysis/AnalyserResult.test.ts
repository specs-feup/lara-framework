import {
    AnalyserResult,
} from "./AnalyserResult";

describe("AnalyserResult", () => {
    describe("getName", () => {
        it ("should return the name of the AnalyserResult", () => {
            const analyserResultTest = new AnalyserResult("correct", "test", "test", undefined);
            expect(analyserResultTest.getName()).toBe("correct");
        })
    });
    describe("getNode", () => {
        it ("should return the node of the AnalyserResult", () => {
            const analyserResultTest = new AnalyserResult("test", "test", "test", undefined);
            expect(analyserResultTest.getNode()).toBe("correct");
        });
    });
});