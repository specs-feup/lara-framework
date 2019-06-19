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

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import pt.up.fe.specs.tools.lara.exception.DefaultLARAException;

/**
 * @deprecated uses Nashorn classes, should be replaced with GraalvmJsEngine
 * @author JoaoBispo
 *
 */
@Deprecated
public class NashornEngine implements JsEngine {

    private static final String NEW_ARRAY = "[]"; // Faster

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

    @Override
    public boolean supportsModifyingThis() {
        return true;
    }

    @Override
    public Bindings newNativeArray() {
        try {
            return (Bindings) engine.eval(NEW_ARRAY);
        } catch (ScriptException e) {
            throw new DefaultLARAException("Could not create new array ", e);
        }
    }

    /**
     * Based on this site: http://programmaticallyspeaking.com/nashorns-jsobject-in-context.html
     *
     * @return
     */
    @Override
    public Object getUndefined() {
        try {
            ScriptObjectMirror arrayMirror = (ScriptObjectMirror) engine.eval("[undefined]");
            return arrayMirror.getSlot(0);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String stringify(Object object) {
        ScriptObjectMirror json = (ScriptObjectMirror) eval("JSON");
        return json.callMember("stringify", object).toString();
    }

    @Override
    public Object eval(String script) {
        try {
            return getEngine().eval(script);
        } catch (ScriptException e) {
            throw new RuntimeException("Exception while evaluation code '" + script + "'", e);
        }
    }

    @Override
    public void put(Bindings var, String member, Object value) {
        var.put(member, value);
    }

    @Override
    public Object eval(String script, Object scope) {
        try {
            return engine.eval(script, (Bindings) scope);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

}
