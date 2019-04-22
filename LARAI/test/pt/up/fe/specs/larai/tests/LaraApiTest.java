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
        newTester().test("AccumulatorTest.lara");
    }

    @Test
    public void testCheckpoint() {
        newTester().test("CheckpointTest.lara");
    }

    @Test
    public void testIo() {
        newTester().test("IoTest.lara");
    }

    @Test
    public void testReplacer() {
        newTester().test("ReplacerTest.lara");
    }

    @Test
    public void testSystem() {
        newTester().test("SystemTest.lara");
    }

    @Test
    public void testStrings() {
        newTester().test("StringsTest.lara");
    }

    @Test
    public void testThis() {
        newTester().test("ThisTest.lara");
    }

    @Test
    public void testDataStore() {
        newTester().test("util/DataStoreTest.lara");
    }

    @Test
    public void testLaraCore() {
        newTester().test("LaraCoreTest.lara");
    }

    @Test
    public void testCsv() {
        newTester().test("CsvTest.lara");
    }

    @Test
    public void testPlatforms() {
        newTester().test("PlatformsTest.lara");
    }

    @Test
    public void testDseValues() {
        newTester().test("dse/DseValuesTest.lara");
    }

    @Test
    public void testTimeUnit() {
        newTester().test("units/TimeUnitTest.lara");
    }

    @Test
    public void testEnergyUnit() {
        newTester().test("units/EnergyUnitTest.lara");
    }

    @Test
    public void testLineIterator() {
        newTester().test("iterators/LineIteratorTest.lara");
    }

    @Test
    public void testLineInserter() {
        newTester().setStack().test("util/LineInserterTest.lara");
    }

    @Test
    public void testCallInFunction() {
        newTester().test("CallInFunction.lara");
    }
}
