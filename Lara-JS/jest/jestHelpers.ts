import Weaver from "@specs-feup/lara/api/weaver/Weaver.js";
import JavaTypes from "@specs-feup/lara/api/lara/util/JavaTypes.js";

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
  beforeEach(() => {
    const javaWeaver = Weaver.getWeaverEngine();
    const javaDatastore = javaWeaver.getData().get();

    javaWeaver.run(Object.keys(codes), Object.values(codes), javaDatastore);
  });

  afterEach(() => {
    Weaver.getWeaverEngine().end();
  });

  afterAll(() => {
    const javaWeaver = Weaver.getWeaverEngine();
    const javaDatastore = javaWeaver.getData().get();

    javaDatastore.set(
      JavaTypes.LaraiKeys.WORKSPACE_FOLDER,
      JavaTypes.FileList.newInstance()
    );

    javaWeaver.run(javaDatastore);
  });
}
