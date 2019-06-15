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
import java.util.Collections;

import javax.script.ScriptEngine;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

/**
 * @deprecated uses Nashorn classes, should be replaced with GraalvmJsEngine
 * @author JoaoBispo
 *
 */
@Deprecated
public class NashornEngine implements JsEngine {

    private final NashornScriptEngine engine;

    public NashornEngine(Collection<Class<?>> blacklistedClasses) {
        // The presence of a ClassFilter, even if empty, disables Java reflection
        if (blacklistedClasses.isEmpty()) {
            this.engine = (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine();
        } else {
            this.engine = (NashornScriptEngine) new NashornScriptEngineFactory()
                    .getScriptEngine(new RestrictModeFilter(blacklistedClasses));
        }

    }

    public NashornEngine() {
        this(Collections.emptyList());
    }

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }

    @Override
    public ForOfType getForOfType() {
        return ForOfType.FOR_EACH;
    }

}
