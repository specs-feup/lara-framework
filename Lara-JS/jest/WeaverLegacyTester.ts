import JavaTypes, {
  JavaClasses,
} from "@specs-feup/lara/api/lara/util/JavaTypes.js";
import Weaver from "@specs-feup/lara/api/weaver/Weaver.js";
import fs from "fs";
import util from "util";
import { jest } from "@jest/globals";
import path from "path";

afterAll(() => {
  const javaWeaver = Weaver.getWeaverEngine();
  const javaDatastore = javaWeaver.getData().get();

  javaDatastore.set(
    JavaTypes.LaraiKeys.WORKSPACE_FOLDER,
    JavaTypes.FileList.newInstance()
  );

  javaWeaver.run(javaDatastore);
});

export class WeaverLegacyTester {
  protected readonly WORK_FOLDER: string = "weaver_test_output";

  private readonly basePackage: string;

  /**
   * Stores the original values of the datastore settings that were modified by the test
   */
  private readonly modifiedDatastoreSettings: Map<string, unknown> = new Map();
  /**
   * Whether to check the syntax of the woven code. Default is true.
   * TODO: This should be handled on the LARA level, but it is currently a Clava-specific setting.
   */
  protected checkWovenCodeSyntax: boolean;
  private _checkExpectedOutput: boolean;
  private srcPackage: string | null;
  private resultPackage: string | null;
  private resultsFile: string | null;
  private run: boolean;

  public constructor(basePackage: string) {
    this.basePackage = basePackage;
    this.checkWovenCodeSyntax = true;

    this.srcPackage = null;
    this.resultPackage = null;
    this.resultsFile = null;
    this.run = true;
    this._checkExpectedOutput = true;
  }

  public checkExpectedOutput(checkExpectedOutput: boolean) {
    this._checkExpectedOutput = checkExpectedOutput;

    return this;
  }

  public set(key: string, value: unknown = true) {
    const datastore = Weaver.getWeaverEngine().getData().get();
    const currentValue = datastore.get(key);

    if (value !== currentValue) {
      this.modifiedDatastoreSettings.set(key, currentValue);
      datastore.set(key, value);
    }

    return this;
  }

  /**
   *
   * @returns the previous value
   */
  public setCheckWovenCodeSyntax(checkWovenCodeSyntax: boolean) {
    this.checkWovenCodeSyntax = checkWovenCodeSyntax;

    return this;
  }

  public setResultPackage(resultPackage: string) {
    this.resultPackage = this.sanitizePackage(resultPackage);

    return this;
  }

  public setSrcPackage(srcPackage: string) {
    this.srcPackage = this.sanitizePackage(srcPackage);

    return this;
  }

  public doNotRun() {
    this.run = false;
    return this;
  }

  public setResultsFile(resultsFile: string): void {
    this.resultsFile = resultsFile;
  }

  private sanitizePackage(packageName: string): string {
    let sanitizedPackage: string = packageName;
    if (!sanitizedPackage.endsWith("/")) {
      sanitizedPackage += "/";
    }

    return sanitizedPackage;
  }

  private buildCodeResource(codeResourceName: string) {
    let filepath: string = this.basePackage;

    if (this.srcPackage != null) {
      filepath = path.join(filepath, this.srcPackage);
    }

    return path.join(filepath, codeResourceName);
  }

  public async test(
    laraResource: string,
    ...codeResources: string[]
  ): Promise<void> {
    if (!this.run) {
      console.info("Ignoring test, 'run' flag is not set");
      return;
    }

    let out = "";
    const log = jest.spyOn(global.console, "log");
    log.mockImplementation((data, ...args: unknown[]) => {
      if (data) {
        out += util.format(data, ...args);
      }
      out += "\n";
    });

    try {
      const javaFiles: JavaClasses.List<JavaClasses.File> =
        new JavaTypes.ArrayList();

      for (const codeResource of codeResources) {
        const javaFile = new JavaTypes.File(
          this.buildCodeResource(codeResource)
        );
        if (!fs.existsSync(javaFile.getAbsolutePath())) {
          throw new Error(
            `Code resource '${codeResource}' does not exist at '${javaFile.getAbsolutePath()}'.`
          );
        }
        javaFiles.add(javaFile);
      }

      const javaWeaver = Weaver.getWeaverEngine();
      const javaDatastore = javaWeaver.getData().get();

      javaDatastore.set(
        JavaTypes.LaraiKeys.WORKSPACE_FOLDER,
        JavaTypes.FileList.newInstance(javaFiles)
      );

      javaWeaver.run(javaDatastore);
      await import(path.join(this.basePackage, laraResource));
      javaWeaver.end();
    } finally {
      log.mockRestore();

      const datastore = Weaver.getWeaverEngine().getData().get();
      this.modifiedDatastoreSettings.forEach((value, key) => {
        datastore.set(key, value);
      });
      this.modifiedDatastoreSettings.clear();
    }

    // Do not check expected output
    if (!this._checkExpectedOutput) {
      return;
    }

    let expectedResource: string = this.basePackage;
    if (this.resultPackage != null) {
      expectedResource = path.join(expectedResource, this.resultPackage);
    }

    const actualResultsFile: string = this.resultsFile ?? laraResource + ".txt";

    expectedResource = path.join(expectedResource, actualResultsFile);

    if (!fs.existsSync(expectedResource)) {
      console.info(
        "Could not find resource '" +
          expectedResource +
          "'. Actual output:\n" +
          out
      );

      throw new Error("Expected outputs not found");
    }

    expect(WeaverLegacyTester.normalize(out)).toEqual(
      WeaverLegacyTester.normalize(
        fs
          .readFileSync(expectedResource, "utf8")
          .replaceAll(`/**** File '${this.WORK_FOLDER}/`, "/**** File '")
      )
    );
  }

  /**
   * Normalizes endlines
   */
  private static normalize(string: string): string {
    return JavaTypes.SpecsStrings.normalizeFileContents(string, true);
  }
}
