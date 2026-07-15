import fs from "node:fs";
import os from "node:os";
import path from "node:path";
import { validateJavaBinaries } from "../scripts/validateJavaBinaries.ts";

const originalWorkingDirectory = process.cwd();
let packageRoot: string;

beforeEach(() => {
  packageRoot = fs.mkdtempSync(path.join(os.tmpdir(), "lara-java-binaries-"));
  process.chdir(packageRoot);
});

afterEach(() => {
  process.chdir(originalWorkingDirectory);
  fs.rmSync(packageRoot, { force: true, recursive: true });
});

describe("validateJavaBinaries", () => {
  it("rejects a missing Java distribution", () => {
    expect(() => validateJavaBinaries()).toThrow("Java distribution is missing");
  });

  it("rejects a symbolic link", () => {
    const target = path.join(packageRoot, "distribution");
    fs.mkdirSync(target);
    fs.symlinkSync(
      target,
      path.join(packageRoot, "java-binaries"),
      process.platform === "win32" ? "junction" : "dir",
    );

    expect(() => validateJavaBinaries()).toThrow("is a symbolic link");
  });

  it("rejects a non-directory distribution", () => {
    fs.writeFileSync(path.join(packageRoot, "java-binaries"), "not a directory");

    expect(() => validateJavaBinaries()).toThrow("is not a directory");
  });

  it("rejects a missing Java library directory", () => {
    fs.mkdirSync(path.join(packageRoot, "java-binaries"));

    expect(() => validateJavaBinaries()).toThrow(
      "Java library directory is missing",
    );
  });

  it("rejects a Java library directory without JAR files", () => {
    const libPath = path.join(packageRoot, "java-binaries", "lib");
    fs.mkdirSync(libPath, { recursive: true });
    fs.writeFileSync(path.join(libPath, "README.txt"), "not a JAR");

    expect(() => validateJavaBinaries()).toThrow("contains no JAR files");
  });

  it("accepts a materialized Java distribution containing JAR files", () => {
    const libPath = path.join(packageRoot, "java-binaries", "lib");
    fs.mkdirSync(libPath, { recursive: true });
    fs.writeFileSync(path.join(libPath, "README.txt"), "metadata");
    fs.writeFileSync(path.join(libPath, "weaver.jar"), "JAR contents");

    expect(() => validateJavaBinaries()).not.toThrow();
  });
});
