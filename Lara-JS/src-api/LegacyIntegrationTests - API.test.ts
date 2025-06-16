import { ClavaWeaverTester } from "./LegacyIntegrationTestsHelpers.test.js";
import path from "path";

/* eslint-disable jest/expect-expect */
describe("ApiTest", () => {
    function newTester() {
        return new ClavaWeaverTester(
            path.resolve("../LARAI/resources/larai/test/api")
        ).setResultPackage("results");
    }

    it("Accumulator", async () => {
        await newTester().test("AccumulatorTest.js");
    });

    it("Checkpoint", async () => {
        await newTester().test("CheckpointTest.js");
    });

    it("Csv", async () => {
        await newTester().test("CsvTest.js");
    });

    it("Io", async () => {
        await newTester().test("IoTest.js");
    });

    it("LaraCore", async () => {
        await newTester().test("LaraCoreTest.js");
    });

    it.failing("LocalFolder", async () => {
        await newTester().test("LocalFolderTest.js");
    });

    it("Platforms", async () => {
        await newTester().checkExpectedOutput(false).test("PlatformsTest.js");
    });

    it("Replacer", async () => {
        await newTester().test("ReplacerTest.js");
    });

    it("Strings", async () => {
        await newTester().test("StringsTest.js");
    });

    it("System", async () => {
        await newTester().test("SystemTest.js");
    });
});

describe("ApiTest - DSE", () => {
    function newTester() {
        return new ClavaWeaverTester(
            path.resolve("../LARAI/resources/larai/test/api/dse")
        ).setResultPackage("../results/dse");
    }

    it("DseValues", async () => {
        await newTester().test("DseValuesTest.js");
    });
});

describe("ApiTest - Graphs", () => {
    function newTester() {
        return new ClavaWeaverTester(
            path.resolve("../LARAI/resources/larai/test/api/graphs")
        ).setResultPackage("../results/graphs");
    }

    it("BasicGraph", async () => {
        await newTester().test("BasicGraphTest.js");
    });
});

describe("ApiTest - Iterators", () => {
    function newTester() {
        return new ClavaWeaverTester(
            path.resolve("../LARAI/resources/larai/test/api/iterators")
        ).setResultPackage("../results/iterators");
    }

    it("LineIterator", async () => {
        await newTester()
            .checkExpectedOutput(false)
            .test("LineIteratorTest.js");
    });
});

describe("ApiTest - Units", () => {
    function newTester() {
        return new ClavaWeaverTester(
            path.resolve("../LARAI/resources/larai/test/api/units")
        ).setResultPackage("../results/units");
    }

    it("EnergyUnit", async () => {
        await newTester().test("EnergyUnitTest.js");
    });

    it("TimeUnit", async () => {
        await newTester().test("TimeUnitTest.js");
    });
});

describe("ApiTest - Util", () => {
    function newTester() {
        return new ClavaWeaverTester(
            path.resolve("../LARAI/resources/larai/test/api/util")
        ).setResultPackage("../results/util");
    }

    it("DataStore", async () => {
        await newTester().test("DataStoreTest.js");
    });

    it("JavaTypes", async () => {
        await newTester().test("JavaTypesTest.js");
    });

    it("LineInserter", async () => {
        await newTester().test("LineInserterTest.js");
    });

    it("PrintOnce", async () => {
        await newTester().test("PrintOnceTest.js");
    });

    it("Random", async () => {
        await newTester().test("RandomTest.js");
    });

    it("StringSet", async () => {
        await newTester().test("StringSetTest.js");
    });

    it("TupleId", async () => {
        await newTester().test("TupleIdTest.js");
    });
});
