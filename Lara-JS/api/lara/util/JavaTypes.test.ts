import { describe, it, expect } from "bun:test";
import JavaTypes from "./JavaTypes.js";

describe("JavaTypes", () => {
  describe("getLaraI", () => {
    it("returns a JavaType", () => {
      const javaClass = JavaTypes.LaraI;
      expect(javaClass).toBeDefined();
    });

    it("returns a JavaType with the given name", () => {
      const javaClass = JavaTypes.LaraI;
      expect(typeof javaClass).toBe("function");
    });
  });

  describe("getJavaSpecsSystem", () => {
    it("creates an object of the given type", () => {
      const javaClass = JavaTypes.SpecsStrings;
      const SpecsStrings = new javaClass();
      expect(SpecsStrings).toBeDefined();
      expect(typeof SpecsStrings).toBe("object");
      expect((SpecsStrings as any).getClass().getName()).toBe(
        "pt.up.fe.specs.util.SpecsStrings"
      );
    });
  });
});
