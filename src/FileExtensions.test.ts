import { jest } from "@jest/globals";
import { isValidFileExtension, FileExtensions } from "./FileExtensions";

describe("FileExtensions", () => {
  describe("isValidFileExtension", () => {
    it("should return true for valid file extensions", () => {
      expect(isValidFileExtension(FileExtensions.JS)).toBe(true);
      expect(isValidFileExtension(FileExtensions.MJS)).toBe(true);
      expect(isValidFileExtension(FileExtensions.CJS)).toBe(true);
    });

    it("should return false for invalid file extensions", () => {
      expect(isValidFileExtension(".txt")).toBe(false);
      expect(isValidFileExtension(".html")).toBe(false);
      expect(isValidFileExtension(".css")).toBe(false);
    });
  });
});
