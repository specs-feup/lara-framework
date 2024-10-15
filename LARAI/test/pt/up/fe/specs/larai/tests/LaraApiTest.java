/**
 * Copyright 2016 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package pt.up.fe.specs.larai.tests;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fe.specs.larai.GenericWeaverTester;
import pt.up.fe.specs.util.SpecsSystem;

public class LaraApiTest {

    @BeforeClass
    public static void setupOnce() {
        SpecsSystem.programStandardInit();
        GenericWeaverTester.clean();
    }

    @After
    public void tearDown() {
        GenericWeaverTester.clean();
    }

    private static GenericWeaverTester newTester() {
        return new GenericWeaverTester("larai/test/api/")
                .setResultPackage("results/");

    }

    @Test
    public void testAccumulator() {
        newTester().test("AccumulatorTest.js");
    }

    @Test
    public void testCheckpoint() {
        newTester().test("CheckpointTest.js");
    }

    @Test
    public void testIo() {
        newTester().test("IoTest.js");
    }

    @Test
    public void testReplacer() {
        newTester().test("ReplacerTest.js");
    }

    @Test
    public void testSystem() {
        newTester().test("SystemTest.js");
    }

    @Test
    public void testStrings() {
        newTester().test("StringsTest.js");
    }

    @Test
    public void testDataStore() {
        newTester().test("util/DataStoreTest.js");
    }

    @Test
    public void testLaraCore() {
        newTester().test("LaraCoreTest.js");
    }

    @Test
    public void testCsv() {
        newTester().test("CsvTest.js");
    }

    @Test
    public void testPlatforms() {
        newTester().test("PlatformsTest.js");
    }

    @Test
    public void testDseValues() {
        newTester().test("dse/DseValuesTest.js");
    }

    @Test
    public void testTimeUnit() {
        newTester().test("units/TimeUnitTest.js");
    }

    @Test
    public void testEnergyUnit() {
        newTester().test("units/EnergyUnitTest.js");
    }

    @Test
    public void testLineIterator() {
        newTester().test("iterators/LineIteratorTest.js");
    }

    @Test
    public void testLineInserter() {
        newTester().test("util/LineInserterTest.js");
    }

    @Test
    public void testTupleId() {
        newTester().test("util/TupleIdTest.js");
    }

    @Test
    public void testGraphsBasic() {
        newTester().test("graphs/BasicGraphTest.js");
    }

    @Test
    public void testActionAwareCache() {
        newTester().test("util/ActionAwareCacheTest.js");
    }

    @Test
    public void testStringSet() {
        newTester().test("util/StringSetTest.js");
    }

    @Test
    public void testPrintOnce() {
        newTester().test("util/PrintOnceTest.js");
    }

    @Test
    public void testJavaTypes() {
        newTester().test("util/JavaTypesTest.js");
    }

    @Test
    public void testRandom() {
        newTester().test("util/RandomTest.js");
    }
}
