import Clava from "@specs-feup/clava/api/clava/Clava.js";
import ClavaJoinPoints from "@specs-feup/clava/api/clava/ClavaJoinPoints.js";

/**
 * Registers the source code that will be used by the weaver for the purpose of this test suite.
 *
 * @param code - String containing the source code.
 */
export function registerSourceCode(code: string): void {
  registerSourceCodes({ "dummyFile.cpp": code });
}

/**
 *  Registers the source code that will be used by the weaver for the purpose of this test suite.
 *
 * @param codes - Record that maps file names to the corresponding source code.
 */
export function registerSourceCodes(codes: Record<string, string>): void {
  beforeAll(() => {
    Clava.getProgram().push();
    const program = Clava.getProgram();

    for (const key in codes) {
      const sourceFile = ClavaJoinPoints.fileWithSource(key, codes[key]);
      program.addFile(sourceFile);
    }

    program.rebuild();
  });

  afterAll(() => {
    Clava.getProgram().pop();
  });
}
