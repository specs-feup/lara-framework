import fs from "fs";
import path from "path";

/**
 * Copied files will have .mjs extension.
 *
 * @param {*} sourceDir
 * @param {*} destinationDir
 * @param {*} extension
 * @returns
 */
function copyFiles(
  sourceDir,
  destinationDir,
  extension,
  targetExtension = undefined
) {
  // Update node to 15+
  //targetExtension ??= extension;
  targetExtension = targetExtension !== undefined ? targetExtension : extension;
  const copiedFiles = [];

  const files = fs.readdirSync(sourceDir);

  for (const file of files) {
    //console.log("FILE: " + file);
    const sourcePath = path.join(sourceDir, file);

    const fileStat = fs.statSync(sourcePath);

    if (fileStat.isDirectory()) {
      const newDestinationDir = path.join(destinationDir, file);
      fs.mkdirSync(newDestinationDir, { recursive: true });
      const subDirectoryCopiedFiles = copyFiles(
        sourcePath,
        newDestinationDir,
        extension,
        targetExtension
      );
      copiedFiles.push(...subDirectoryCopiedFiles);
    } else if (file.endsWith(extension)) {
      const endIndex = file.length - extension.length;
      const fileWithoutExtension = file.substring(0, endIndex);
      const destinationPath = path.join(
        destinationDir,
        fileWithoutExtension + targetExtension
      );
      fs.copyFileSync(sourcePath, destinationPath);
      console.log("Copied:", sourcePath, "->", destinationPath);
      copiedFiles.push(destinationPath);
    }
  }

  return copiedFiles;
}

// Copy JS files to Java project folder

const jsSourceFolder = "api";
const jsDestinationFolder = "../LaraApi/src-lara/";

const copiedFiles = [];
copiedFiles.push(
  ...copyFiles(jsSourceFolder, jsDestinationFolder, ".js", ".js")
);
//copiedFiles.push(...copyFiles(jsSourceFolder, jsDestinationFolder, ".mjs"));

/*
const copiedFiles = [];

copyFiles(jsSourceFolder, jsDestinationFolder, ".js").then((result) =>
  copiedFiles.push(...result)
);
*/

//console.log(copiedFiles);

// Generate the Java file with the resources
const javaClassname = "LaraApiJsResource";
const javaDestinationFile =
  "../LaraApi/src-java/pt/up/fe/specs/lara/" + javaClassname + ".java";

const resources = {};
const filesSet = {};

copiedFiles.forEach((file) => {
  const fileName = path.basename(file);

  if (fileName in filesSet) {
    throw Error(
      "Found duplicated file '" +
        fileName +
        "'. Check, for instance, if 'api' folder is clean"
    );
  }
  filesSet[fileName] = 0;

  resources[fileName] = path
    .relative(jsDestinationFolder, file)
    .toString()
    .replace(/\\/g, "/");
});

/*
const resources = copiedFiles.map((file) =>
  path.relative(jsDestinationFolder, file).toString().replace(/\\/g, "/")
);
*/
//console.log(resources);

const enumNames = {};

const resourcesCode =
  Object.entries(resources)
    .filter(([key, value]) => value !== "index.js")
    .map(([key, value]) => {
      const enumName = key.toUpperCase().replace(/\./g, "_");

      // Check for repeated enum names
      if (enumNames[enumName] !== undefined) {
        console.log(
          "[PROBLEM] Repeated enum name '" +
            enumName +
            "'! Probably files where moved, recommended that 'api' folder is deleted"
        );
      } else {
        enumNames[enumName] = 0;
      }

      return `    ${enumName}("${value}")`;
    })
    .join(",\n") + ";";

/*
for (const key in resources) {
}
*/

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

package pt.up.fe.specs.lara;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * This file has been automatically generated.
 * 
 * @author Joao Bispo
 *
 */
public enum ${javaClassname} implements LaraResourceProvider {

${resourcesCode}

    private final String resource;

    /**
     * @param resource
     */
    private ${javaClassname} (String resource) {
        this.resource = resource;
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

//console.log(javaCode);

fs.writeFileSync(javaDestinationFile, javaCode);
console.log("File '" + javaDestinationFile + "' written");
