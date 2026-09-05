import fs from "fs";
import os from "os";
import path from "path";
import { generateEnums } from "../scripts/generate-ts-joinpoints.ts";

describe("generateEnums", () => {
  it("preserves the enum values from the language specification", () => {
    const outputDirectory = fs.mkdtempSync(
      path.join(os.tmpdir(), "lara-build-interfaces-"),
    );
    const outputPath = path.join(outputDirectory, "Joinpoints.ts");

    try {
      const outputFile = fs.openSync(outputPath, "w");
      try {
        generateEnums(
          [
            {
              name: "StorageClass",
              entries: [
                { name: "NONE", value: "NONE" },
                { name: "PRIVATE_EXTERN", value: "PRIVATE_EXTERN" },
                { name: "STATIC", value: "STATIC" },
              ],
            },
            {
              name: "AccessSpecifier",
              entries: [
                { name: "DEFAULT", value: "DEFAULT" },
                { name: "STATIC", value: "static" },
              ],
            },
          ],
          outputFile,
        );
      } finally {
        fs.closeSync(outputFile);
      }

      const output = fs.readFileSync(outputPath, "utf8");
      expect(output).toContain('  STATIC: "STATIC",');
      expect(output).toContain('  PRIVATE_EXTERN: "PRIVATE_EXTERN",');
      expect(output).toContain('export const AccessSpecifier = {');
      expect(output).toContain('  STATIC: "static",');
    } finally {
      fs.rmSync(outputDirectory, { recursive: true, force: true });
    }
  });
});
