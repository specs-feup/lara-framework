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

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.specs.generators.java.classtypes.JavaClass;

import pt.up.fe.specs.util.SpecsIo;

public abstract class GeneratorHelper {

    protected JavaAbstractsGenerator javaGenerator;

    protected GeneratorHelper(JavaAbstractsGenerator javaGenerator) {
        this.javaGenerator = javaGenerator;
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     * 
     * @param javaGenerator
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    public abstract JavaClass generate();

    protected static String ln() {
        return SpecsIo.getNewline();
    }
}
