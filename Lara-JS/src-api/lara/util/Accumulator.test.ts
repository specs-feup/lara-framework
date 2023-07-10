import Accumulator from "./Accumulator.js";

describe("Accumulator", () => {
    
    describe("add", () => {
        const acc = new Accumulator();
        it("should return size 1 when adding an element", () => {
            acc.add("step1");
            expect(acc.get("step1")).toBe(1);
        })
    });
    
    describe("add and get", ()=>{
        const acc = new Accumulator();

        it("add the tuple (step1, step2)", () => {
        acc.add("step1", "step2");

        acc.add(["step1", "step2"]);

        acc.add(["step1", "step2.1"]);

        acc.add("step1");
        acc.add("step1");
        acc.add(["step1"]);

        expect(acc.get("step1", "step2")).toBe(2);
        expect(acc.get(["step1", "step2"])).toBe(2);
        expect(acc.get("step1")).toBe(3);
        expect(acc.get("step1")).toBe(3);
        expect(acc.get("step1")).toBe(3);
    })
})
})

