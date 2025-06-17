import { ClavaWeaverTester } from "./LegacyIntegrationTestsHelpers.test.js";
import path from "path";

/* eslint-disable jest/expect-expect */
describe("Weaver", () => {
    function newTester() {
        return new ClavaWeaverTester(
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
