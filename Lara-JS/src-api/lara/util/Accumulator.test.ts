import Accumulator from "./Accumulator.js";

describe("Accumulator", () => {
  describe("add", () => {
    it("should return size 1 when adding an element", () => {
      const acc = new Accumulator();
      acc.add("step1");
      expect(acc.get("step1")).toBe(1);
    });
  });

  describe("add and get", () => {
    it("add the tuple (step1, step2)", () => {
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
});
