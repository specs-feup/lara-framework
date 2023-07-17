#!/usr/bin/env node

import fs from "fs";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";
import { convertSpecification } from "./convert-joinpoint-specification.js";

import { generateJoinpoints, generateEnums } from "./generate-ts-joinpoints.js";

function buildLaraJoinPoint(inputFileName, outputFileName) {
  console.log("Hello from build-LaraJoinPoint.js");
  console.log("inputFile:", inputFileName);
  console.log("outputFile:", outputFileName);

  const jsonSpecification = fs.readFileSync(inputFileName, "utf8");
  const specification = convertSpecification(JSON.parse(jsonSpecification));

  // Create output file if it doesn't exist
  const outputFile = fs.openSync(outputFileName, "w");
  fs.writeSync(
    outputFile,
    `//////////////////////////////////////////////////////
// This file is generated by build-LaraJoinPoint.js //
//////////////////////////////////////////////////////

/* eslint-disable @typescript-eslint/ban-types */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-return */

import JavaTypes from "lara-js/src-api/lara/util/JavaTypes.js";\n\n`
  );

  generateJoinpoints(specification.joinpoints, outputFile);
  generateEnums(specification.enums, outputFile);

  generateJoinpointWrapper(specification.joinpoints, outputFile);

  fs.closeSync(outputFile);
}

function generateJoinpointWrapper(joinpoints, outputFile) {
  fs.writeSync(
    outputFile,
    `\nexport type JoinpointMapperType = { [key: string]: typeof LaraJoinPoint };\n
let JoinpointMappers: JoinpointMapperType[];\n`
  );

  fs.writeSync(
    outputFile,
    `\nexport function registerJoinpointMapper(mapper: JoinpointMapperType): void {
  JoinpointMappers.push(mapper);
}\n`
  );

  fs.writeSync(
    outputFile,
    `\nexport function wrapJoinPoint(obj: any): any {
  if (obj === undefined) {
    return obj;
  }

  if (obj instanceof LaraJoinPoint) {
    return obj;
  }

  if (typeof obj !== "object") {
    return obj;
  }

  if (Array.isArray(obj)) {
    return obj.map(wrapJoinPoint);
  }

  if (!JavaTypes.isJavaObject(obj)) {
    console.log("Given Java join point is not a Java class: " + typeof obj);
    return obj;
  }

  const isJavaJoinPoint = JavaTypes.JoinPoint.isJoinPoint(obj);
  if (!isJavaJoinPoint) {
    throw new Error(
      // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
      \`Given Java join point is a Java class but is not a JoinPoint: \${obj.getClass()}\`
    );
  }

  for (const mapper of JoinpointMappers) {
    if (mapper[obj.getType() as string]) {
      return new mapper[obj.getType() as string](obj);
    }
  }
  throw new Error(
    "No mapper found for join point type: " + (obj.getType() as string)
  );
}\n`
  );

  fs.writeSync(
    outputFile,
    `\nexport function unwrapJoinPoint(obj: any): any {
  if (obj instanceof LaraJoinPoint) {
    return obj._javaObject;
  }

  return obj;
}\n`
  );
}

const args = yargs(hideBin(process.argv))
  .scriptName("build-LaraJoinPoint")
  .option("i", {
    alias: "input",
    describe: "Path to JSON config file",
    type: "string",
  })
  .option("o", {
    alias: "output",
    describe: "Path to the output file",
    type: "string",
  })
  .help()
  .showHelpOnFail(true)
  .strict()
  .parse();

buildLaraJoinPoint(args.input, args.output);
