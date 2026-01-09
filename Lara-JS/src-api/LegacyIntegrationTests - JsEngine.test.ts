import { WeaverLegacyTester } from "../jest/WeaverLegacyTester.js";
import path from "path";

/* eslint-disable jest/expect-expect */
describe("JsEngine", () => {
    function newTester() {
        return new WeaverLegacyTester(
            path.resolve("../LARAI/resources/larai/test/jsengine")
        ).setResultPackage("results");
    }

    it("Arrow", async () => {
        await newTester().test("ArrowTest.js");
    });

    it("Exception", async () => {
        let err = null;
        try {
            await newTester().test("ExceptionTest.js");
        } catch (error) {
            err = error;
        }

        expect(err).toBeInstanceOf(Error);
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        expect((err as any).message).toContain("throwing exception in bar()");
    });
});
