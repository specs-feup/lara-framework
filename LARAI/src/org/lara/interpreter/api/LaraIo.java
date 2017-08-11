/**
 * Copyright 2017 SPeCS.
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

package org.lara.interpreter.api;

import java.io.File;
import java.util.List;

import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.utilities.StringLines;

/**
 * Input/output methods to be used in LARA aspects.
 * 
 * @author JoaoBispo
 *
 */
public class LaraIo {

    public static List<String> readLines(String filename) {
        return readLines(new File(filename));
    }

    public static List<String> readLines(File filename) {
        return StringLines.getLines(filename);
    }

    public static boolean deleteFile(String filename) {
        return deleteFile(new File(filename));
    }

    public static boolean deleteFile(File file) {
        return SpecsIo.delete(file);
    }
}
