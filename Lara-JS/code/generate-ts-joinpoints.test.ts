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
    const outputFile = fs.openSync(outputPath, "w");

    try {
      generateEnums(
        [{ name: "StorageClass", entries: ["NONE", "PRIVATE_EXTERN", "STATIC"] }],
        outputFile,
      );
    } finally {
      fs.closeSync(outputFile);
    }

    try {
      expect(fs.readFileSync(outputPath, "utf8")).toContain(
        '  STATIC: "STATIC",',
      );
      expect(fs.readFileSync(outputPath, "utf8")).toContain(
        '  PRIVATE_EXTERN: "PRIVATE_EXTERN",',
      );
    } finally {
      fs.rmSync(outputDirectory, { recursive: true, force: true });
    }
  });
});
