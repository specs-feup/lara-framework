import IdGenerator, { IdGeneratorClass } from "./IdGenerator.js";

describe("IdGenerator", () => {
  describe("IdGenerator", () => {
    it("should generate ids with the correct sequence", () => {
      const id1 = IdGenerator.next("test");
      const id2 = IdGenerator.next("test");
      expect(id1).toBe("test0");
      expect(id2).toBe("test1");
    });
  });

  describe("IdGeneratorClass", () => {
    let idGenerator: IdGeneratorClass;

    beforeEach(() => {
      idGenerator = new IdGeneratorClass();
    });

    it("should generate a unique id without a key", () => {
      const id1 = idGenerator.next();
      const id2 = idGenerator.next();
      expect(id1).not.toEqual(id2);
    });

    it("should generate a unique id with a key", () => {
      const id1 = idGenerator.next("test");
      const id2 = idGenerator.next("test");
      expect(id1).not.toEqual(id2);
    });

    it("should generate ids with the correct key", () => {
      const id = idGenerator.next("test");
      expect(id.startsWith("test")).toBe(true);
    });

    it("should generate ids with the correct sequence", () => {
      const id1 = idGenerator.next("test");
      const id2 = idGenerator.next("test");
      expect(id1).toBe("test0");
      expect(id2).toBe("test1");
    });
  });
});
