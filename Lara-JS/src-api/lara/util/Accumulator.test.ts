import Accumulator from "./Accumulator.js";

describe("Accumulator", () => {
  describe("add", () => {
    it("returns size 1 when adding the same element twice", () => {
      const acc = new Accumulator();
      acc.add(["step1","step2"]);
      acc.add(["step1","step2"]);
      expect(Object.keys.length).toBe(1);
    });
  });


  describe("get", ()=>{
    it("should be 0 on empty", () => {
      const acc = new Accumulator;
      expect(acc.get("step1")).toBe(0);
    });
    it("is 0 for step1 after adding an array with a step1 and a step2 and 1 for tuple step1,step2 ", () => {
      const acc  = new Accumulator;
      acc.add("step1", "step2");
      expect(acc.get("step1")).toBe(0);
      expect(acc.get("step1", "step2")).toBe(1);
    });
  
  })


  describe("add and get", () => {
    it("get returns the correct value when we had multiple arguments, arrays or subchains", () => {
      const acc = new Accumulator();
      acc.add("step1", "step2");
      acc.add(["step1", "step2"]);
      acc.add(["step1", "step2.1"]);
      acc.add("step1");
      acc.add("step1");
      acc.add(["step1"]);

      expect(acc.get("step1", "step2")).toBe(2);
      expect(acc.get(["step1", "step2"])).toBe(2);
      expect(acc.get("step1")).toBe(3);
    });
  });


  describe("copy", () => {
    it("creates an Accumulator with same value", () =>{
      const acc = new Accumulator;
      acc.add("step1", "step2");
      const copy = acc.copy();
      expect(acc.value).toBe(copy.value);
    });
    it("creates an Accumulator same value for the same keys", () => {
      const acc = new Accumulator;
      acc.add("step1", "step2");
      acc.add("step1", "step2");
      acc.add("step1");
      const copy = acc.copy();
      expect(acc.get("step1", "step2")).toBe(copy.get("step1", "step2"));
      expect(acc.get("step1")).toBe(copy.get("step1"));
    });
  
  })


  describe("keys", () => {
    it("returns an array with the keys added", () =>{
      const acc = new Accumulator;
      acc.add("step1");
      acc.add("step1","step2");
      acc.add("step1");
      acc.add(["step1","step2"]);
      const res = [["step1"],["step1","step2"]];
      expect(acc.keys()[0]).toStrictEqual(["step1"]);
      expect(acc.keys()[1]).toStrictEqual(["step1", "step2"]);
    })
  })
});
