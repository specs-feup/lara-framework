import { WeaverLegacyTester } from "../vitest/WeaverLegacyTester.ts";
import path from "path";

/* oxlint-disable vitest/expect-expect */
describe("Weaver", () => {
    function newTester() {
        return new WeaverLegacyTester(
            path.resolve("../LARAI/resources/larai/test/weaver")
        )
            .setResultPackage("results");
    }

    it("WeaverOptions", async () => {
        await newTester().test("WeaverOptionsTest.js");
    });

    it("Weaver", async () => {
        await newTester().test("WeaverTest.js");
    });
});
