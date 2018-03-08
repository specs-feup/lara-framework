/**
 * Copyright 2018 SPeCS.
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

package pt.up.fe.specs.lara.unit;

import java.io.File;

/**
 * Represents a single LaraUnit test.
 * 
 * @deprecated
 * @author JoaoBispo
 *
 */
// public class LaraUnitHarness implements Closeable {
@Deprecated
public class LaraUnitHarness {

    private final File laraTest;

    public LaraUnitHarness(File laraTest) {
        this.laraTest = laraTest;
    }

    // @Override
    // public void close() throws IOException {
    // SpecsIo.delete(laraTest);
    // }

    // public static List<LaraUnitHarness> test() {
    // return Collections.emptyList();
    // }

}
