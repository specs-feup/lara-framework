#!/usr/bin/env node

import fs from "fs";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";
import { convertSpecification } from "./convert-joinpoint-specification.js";

import { generateJoinpoints, generateEnums } from "./generate-ts-joinpoints.js";

async function buildInterfaces(
  inputFileName,
  laraJoinPointSpecificationImportPath,
  outputFileName
) {
  console.log("Hello from build-interfaces.js");
  console.log("inputFile:", inputFileName);
  console.log(
    "LaraJoinPointSpecificationFile: ",
    laraJoinPointSpecificationImportPath
  );
  console.log("outputFile:", outputFileName);

  const { default: laraJsonSpecification } = await import(
    laraJoinPointSpecificationImportPath,
    {
      assert: {
        type: "json",
      },
    }
  );
  const jsonSpecification = fs.readFileSync(inputFileName, "utf8");

  const laraSpecification = convertSpecification(
    laraJsonSpecification,
    undefined
  );
  const specification = convertSpecification(
    JSON.parse(jsonSpecification),
    laraSpecification
  );

  // Create output file if it doesn't exist
  const outputFile = fs.openSync(outputFileName, "w");
  fs.writeSync(
    outputFile,
    `///////////////////////////////////////////////////
// This file is generated by build-interfaces.js //
///////////////////////////////////////////////////

/* eslint-disable @typescript-eslint/ban-types */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-return */

import {
  LaraJoinPoint,
  type JoinpointMapperType,
  registerJoinpointMapper,
  wrapJoinPoint,
  unwrapJoinPoint,
} from "lara-js/api/LaraJoinPoint.js";\n\n`
  );

  generateJoinpoints(specification.joinpoints, outputFile);
  generateEnums(specification.enums, outputFile);

  generateMappers(specification.joinpoints, specification.enums, outputFile);

  fs.closeSync(outputFile);
}

function generateMappers(joinpoints, enums, outputFile) {
  fs.writeSync(outputFile, `const JoinpointMapper: JoinpointMapperType = {\n`);
  for (const jp of joinpoints) {
    fs.writeSync(outputFile, `  ${jp.originalName}: ${jp.name},\n`);
  }
  fs.writeSync(outputFile, `};\n`);

  fs.writeSync(
    outputFile,
    `\nlet registered = false;
if (!registered) {
  registerJoinpointMapper(JoinpointMapper);
  registered = true;
}\n`
  );
}

const args = yargs(hideBin(process.argv))
  .scriptName("lara-build-interfaces")
  .option("i", {
    alias: "input",
    describe: "Path to JSON config file",
    type: "string",
  })
  .option("l", {
    alias: "lara",
    describe: "Path to JSON config file that describes LaraJoinPoint",
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

await buildInterfaces(args.input, args.lara, args.output);
