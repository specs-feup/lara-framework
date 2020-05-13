/**
 * Copyright 2015 SPeCS.
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

package larac;

import org.lara.language.specification.dsl.LanguageSpecificationV2;

import larac.utils.output.Output;
import pt.up.fe.specs.tools.lara.exception.BaseException;

public class LARACLauncher {

    /**
     * The main function where the aspect file is parsed and the output files are created
     * 
     * @param args
     *            The input arguments of the program
     */
    public static void main(String args[]) {

        LARACLauncher.exec(args);
    }

    /**
     * Execute LaraC with the input arguments
     * 
     * @param args
     *            array containing the input arguments
     */
    public static int exec(String args[]) {

        return LARACLauncher.exec(args, new Output());
    }

    /**
     * Execute LaraC with the input arguments and a user-defined output (typically used when calling LaraC recursively)
     * 
     * @param args
     *            array containing the input arguments
     * @param output
     *            user-defined output, containing a user-defined verbose level
     */
    public static int exec(String args[], Output output) {
        // Create a new instance of lara with the standard output and generate
        // the AST
        try {
            final LaraC lara = new LaraC(args, output);
            if (lara.isReadyToParse()) {
                return lara.compileAndSave();
            }
            return 1;
        } catch (BaseException e) {
            throw e.generateRuntimeException();
        }
    }

    /**
     * Execute LaraC with the input arguments and a user-defined output (typically used when calling LaraC recursively)
     * 
     * @param args
     *            array containing the input arguments
     * @param output
     *            user-defined output, containing a user-defined verbose level
     */
    public static int exec(String args[], LanguageSpecificationV2 langSpec, Output output) {
        // Create a new instance of lara with the standard output and generate
        // the AST
        try {
            final LaraC lara = new LaraC(args, langSpec, output);
            if (lara.isReadyToParse()) {
                return lara.compileAndSave();
            }
            return 1;
        } catch (BaseException e) {
            throw e.generateRuntimeException();
        }
    }

}
