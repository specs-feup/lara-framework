/**
 * Copyright 2019 SPeCS.
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

package org.lara.interpreter.weaver.js;

import java.util.Collection;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

public interface JsEngine {

    ScriptEngine getEngine();

    ForOfType getForOfType();

    /**
     *
     * @return true, if you can pass a reference to 'this' from JS and modify it through a Bindings object.
     */
    boolean supportsModifyingThis();

    /**
     * Creates a new JavaScript array.
     *
     * @return a
     */
    Bindings newNativeArray();

    static JsEngine getEngine(JsEngineType type, Collection<Class<?>> forbiddenClasses) {
        switch (type) {
        case NASHORN:
            return new NashornEngine(forbiddenClasses);
        case GRAALVM_COMPAT:
            return new GraalvmJsEngine(forbiddenClasses, true);
        case GRAALVM:
            return new GraalvmJsEngine(forbiddenClasses);
        default:
            throw new NotImplementedException(type);
        }
    }

}
