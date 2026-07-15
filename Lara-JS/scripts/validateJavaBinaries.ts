import fs from "node:fs";
import path from "node:path";

const remediation =
  "Run this weaver's Gradle build or installDist task before packaging.";

export function validateJavaBinaries(): void {
  const javaBinariesPath = path.resolve("java-binaries");

  if (!fs.existsSync(javaBinariesPath)) {
    fail(`Java distribution is missing at '${javaBinariesPath}'.`);
  }

  const javaBinariesStats = fs.lstatSync(javaBinariesPath);
  if (javaBinariesStats.isSymbolicLink()) {
    fail(
      `Java distribution at '${javaBinariesPath}' is a symbolic link, whose contents npm will not package.`,
    );
  }

  if (!javaBinariesStats.isDirectory()) {
    fail(`Java distribution at '${javaBinariesPath}' is not a directory.`);
  }

  const libPath = path.join(javaBinariesPath, "lib");
  if (!fs.existsSync(libPath) || !fs.lstatSync(libPath).isDirectory()) {
    fail(`Java library directory is missing at '${libPath}'.`);
  }

  const hasJar = fs
    .readdirSync(libPath, { withFileTypes: true })
    .some((entry) => entry.isFile() && entry.name.endsWith(".jar"));

  if (!hasJar) {
    fail(`Java library directory at '${libPath}' contains no JAR files.`);
  }
}

function fail(message: string): never {
  throw new Error(`${message} ${remediation}`);
}
