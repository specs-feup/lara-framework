#!/usr/bin/env node

import fs from "fs";
import path from "path";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";
import { fileURLToPath } from "url";

if (fileURLToPath(import.meta.url) === process.argv[1]) {
  // This script is being executed directly
  const args = yargs(hideBin(process.argv))
    .scriptName("java-dist")
    .option("i", {
      alias: "inputFolder",
      describe: "Path to the source folder",
      type: "string",
    })
    .option("o", {
      alias: "outputFolder",
      describe: "Path to the destination folder",
      type: "string",
    })
    .option("e", {
      alias: "extension",
      describe: "Extension of the files to copy",
      type: "string",
    })
    .option("newExtension", {
      describe: "Change the extension of the copied files",
      type: "string",
      demandOption: false,
    })
    .help()
    .showHelpOnFail(true)
    .strict()
    .parse();

  copyFolder(
    args.inputFolder,
    args.outputFolder,
    args.extension,
    args.newExtension
  );
}

/**
 * Copied files will have .mjs extension.
 *
 * @param {string} sourceDir
 * @param {string} destinationDir
 * @param {RegExp} extension
 * @param {RegExp} targetExtension
 * @returns
 */
export default function copyFolder(
  sourceDir,
  destinationDir,
  extension,
  targetExtension = extension
) {
  const copiedFiles = [];

  const files = fs.readdirSync(sourceDir);

  for (const file of files) {
    const sourcePath = path.join(sourceDir, file);

    const fileStat = fs.statSync(sourcePath);

    if (fileStat.isDirectory()) {
      const newDestinationDir = path.join(destinationDir, file);

      fs.mkdirSync(newDestinationDir, { recursive: true });

      const subDirectoryCopiedFiles = copyFolder(
        sourcePath,
        newDestinationDir,
        extension,
        targetExtension
      );

      copiedFiles.push(...subDirectoryCopiedFiles);
    } else if (file.endsWith(extension)) {
      const targetFileName =
        file.substring(0, file.length - extension.length) + targetExtension;

      const destinationPath = path.join(destinationDir, targetFileName);

      fs.copyFileSync(sourcePath, destinationPath);
      console.log("Copied:", sourcePath, "->", destinationPath);

      copiedFiles.push(destinationPath);
    }
  }

  return copiedFiles;
}
