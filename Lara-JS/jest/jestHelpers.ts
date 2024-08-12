import Clava from "clava-js/api/clava/Clava.js";
import ClavaJoinPoints from "clava-js/api/clava/ClavaJoinPoints.js";

/**
 * Registers the source code that will be used by the weaver for the purpose of this test suite.
 * 
 * @param code - String containing the source code.
 */
export function registerSourceCode(code: string): void {
  beforeAll(() => {
    Clava.getProgram().push();
    const program = Clava.getProgram();
    const sourceFile = ClavaJoinPoints.fileWithSource("dummyFile.cpp", code);
    program.addFile(sourceFile);
    program.rebuild();
  });

  afterAll(() => {
    Clava.getProgram().pop();
  });
}
