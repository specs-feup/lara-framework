import { describe, it } from "bun:test";
import { WeaverLegacyTester } from "../bun/WeaverLegacyTester.js";
import path from "path";

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
