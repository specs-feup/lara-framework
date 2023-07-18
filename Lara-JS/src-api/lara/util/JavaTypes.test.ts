import java from "java";
import JavaTypes from "./JavaTypes.js";

describe("JavaTypes", () => {
  beforeAll(() => {
    java.classpath.push("../../ClavaWeaver.jar");
  });
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
      expect((SpecsStrings as any).getClassSync().getNameSync()).toBe(
        "pt.up.fe.specs.util.SpecsStrings"
      );
    });
  });
});
