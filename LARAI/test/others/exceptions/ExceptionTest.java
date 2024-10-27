/**
 * Copyright 2015 SPeCS Research Group.
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

package others.exceptions;

import static org.junit.Assert.*;

import org.junit.Test;
import org.lara.interpreter.exception.LaraIException;

public class ExceptionTest {

    public static void main(String[] args) {
        new ExceptionTest().testLARAException();
    }

    @Test
    public void testLARAException() {
        try {
            testLARAIException(5);
        } catch (Exception cause) {
            assertEquals(LaraIException.class, cause.getClass());
            try {
                throw ((LaraIException) cause).generateRuntimeException();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testLARAIException(int level) {

        try {
            throw new NumberFormatException("the error was given in larai");
        } catch (Exception cause) {
            throw new LaraIException("file.lara", "execution", cause);
        }
    }
}
