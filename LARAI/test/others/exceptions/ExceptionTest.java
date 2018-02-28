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
import org.lara.interpreter.exception.ApplyException;
import org.lara.interpreter.exception.AspectDefException;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.exception.PointcutExprException;
import org.lara.interpreter.exception.SelectException;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

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
            if (level <= 0) {
                throw new NumberFormatException("the error was given in larai");
            }

            testAspectDefException(level);
        } catch (Exception cause) {
            throw new LaraIException("file.lara", "execution", cause);
        }
    }

    private static void testAspectDefException(int level) {

        try {
            if (level == 1) {
                throw new NullPointerException();
            }
            testPointcutException(level);
            testApplyException(level);

        } catch (Exception e) {
            throw new AspectDefException("main", "file.lara", 2, 4, e);
        }
    }

    private static void testPointcutException(int level) {

        try {
            if (level == 2) {
                throw new NotImplementedException("select is not implemented");
            }

            testSelectException(level);

        } catch (Exception e) {
            String[] chain = { "file", "function", "body", "var" };
            throw new PointcutExprException("select1", chain, 2, e);

        }
    }

    private static void testSelectException(int level) {
        try {
            if (level == 3) {
                throw new NullPointerException("join point list was null");
            }
        } catch (Exception e) {
            throw new SelectException("function", "body", e);
        }

    }

    private static void testApplyException(int level) {

        try {

            throw new RuntimeException("Action was not applied");

        } catch (Exception e) {
            throw new ApplyException("apply1", "select1", null, e);
        }
    }
}
