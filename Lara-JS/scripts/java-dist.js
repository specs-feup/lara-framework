#!/usr/bin/env node

import fs from "fs";
import path from "path";
import yargs from "yargs";
import { hideBin } from "yargs/helpers";
import copyFolder from "./copy-folder.js";

const args = yargs(hideBin(process.argv))
  .scriptName("java-dist")
  .option("jsSourceFolder", {
    describe: "Path to the source folder",
    type: "string",
  })
  .option("jsDestinationFolder", {
    describe: "Path to the destination folder",
    type: "string",
  })
  .option("javaClassname", {
    describe: "Name of the output Java class",
    type: "string",
  })
  .option("javaPackageName", {
    describe: "Name of the output Java class' package",
    type: "string",
  })
  .option("javaDestinationFolder", {
    describe: "Path to the java class destination folder",
    type: "string",
  })
  .option("javaResourceNamespace", {
    describe: "Namespace of the resources",
    type: "string",
  })
  .help()
  .showHelpOnFail(true)
  .strict()
  .parse();

distributeAPIasJavaResources(
  copyFolder(args.jsSourceFolder, args.jsDestinationFolder, ".js"),
  args.jsDestinationFolder,
  args.javaClassname,
  args.javaPackageName,
  path.join(args.javaDestinationFolder, args.javaClassname + ".java"),
  args.javaResourceNamespace
);

/**
 * Generate the Java file with the resources
 *
 * @param {{[key: string]: string}} copiedFiles - Map from file name to relative path
 * @param {string} jsDestinationFolder - Path to the destination folder
 * @param {string} javaClassname - Name of the Java class
 * @param {string} javaPackageName - Name of the Java class' package
 * @param {string} javaDestinationFile - Path to the destination file
 * @param {string} javaResourceNamespace - Namespace of the resources
 */
function distributeAPIasJavaResources(
  copiedFiles,
  jsDestinationFolder,
  javaClassname,
  javaPackageName,
  javaDestinationFile,
  javaResourceNamespace = ""
) {
  const resources = {};
  const filesSet = new Set();
  const enumNames = new Set();
  let repeatedEnumNames = 0;

  copiedFiles.forEach((file) => {
    const fileName = path.basename(file);

    if (filesSet.has(fileName)) {
      throw Error(
        "Found duplicated file '" +
          fileName +
          "'. Check, for instance, if 'api' folder is clean"
      );
    }
    filesSet.add(fileName);

    resources[fileName] = path
      .relative(jsDestinationFolder, file)
      .toString()
      .replace(/\\/g, "/");
  });

  const resourcesCode =
    Object.entries(resources)
      .filter(([key, value]) => value !== "index.js")
      .map(([key, value]) => {
        let enumName = key
          .toUpperCase()
          .replace(/\./g, "_")
          .replace(/\-/g, "_");

        // Check for repeated enum names
        if (enumNames.has(enumName)) {
          console.error(
            "[PROBLEM] Repeated enum name '" +
              enumName +
              "'! Probably files were moved, recommended that 'api' folder is deleted"
          );
          enumName = enumName + "_" + ++repeatedEnumNames;
        }

        enumNames.add(enumName);
        return `    ${enumName}("${value}")`;
      })
      .join(",\n") + ";";

  const currentYear = new Date().getFullYear();

  const javaCode = `
/**
 * Copyright ${currentYear} SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package ${javaPackageName};

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * This file has been automatically generated.
 * 
 * @author Joao Bispo, Luis Sousa
 *
 */
public enum ${javaClassname} implements LaraResourceProvider {

${resourcesCode}

    private final String resource;

    private static final String WEAVER_PACKAGE = "${javaResourceNamespace}${
    javaResourceNamespace ? "/" : ""
  }";

    /**
     * @param resource
     */
    private ${javaClassname} (String resource) {
      this.resource = WEAVER_PACKAGE + getSeparatorChar() + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
`;

  fs.writeFileSync(javaDestinationFile, javaCode);
  console.log("File '" + javaDestinationFile + "' written");
}
