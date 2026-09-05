import { WeaverLegacyTester } from "../vitest/WeaverLegacyTester.ts";
import path from "path";

/* oxlint-disable vitest/expect-expect */
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
        // oxlint-disable-next-line typescript/no-explicit-any
        expect((err as any).message).toContain("throwing exception in bar()");
    });
});
