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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import pt.up.fe.specs.tools.lara.exception.DefaultLARAException;

public class GraalvmJsEngine implements JsEngine {

    private static final String NEW_ARRAY = "[]"; // Faster

    private final GraalJSScriptEngine engine;
    private final boolean nashornCompatibility;

    public GraalvmJsEngine(Collection<Class<?>> blacklistedClasses) {
        this(blacklistedClasses, false);
    }

    public GraalvmJsEngine(Collection<Class<?>> blacklistedClasses, boolean nashornCompatibility) {

        Set<String> forbiddenClasses = blacklistedClasses.stream().map(Class::getName).collect(Collectors.toSet());

        Context.Builder contextBuilder = Context.newBuilder("js")
                // .allowAllAccess(true)
                .allowHostAccess(HostAccess.ALL)
                // .allowIO(true)
                // .allowCreateThread(true)
                // .allowNativeAccess(true)
                // .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowHostClassLookup(name -> !forbiddenClasses.contains(name));

        if (nashornCompatibility) {
            contextBuilder.allowExperimentalOptions(true).option("js.nashorn-compat", "true");
        }

        this.engine = GraalJSScriptEngine.create(null, contextBuilder);
        this.nashornCompatibility = nashornCompatibility;
    }

    public GraalvmJsEngine() {
        this(Collections.emptyList());
    }

    @Override
    public GraalJSScriptEngine getEngine() {
        return engine;
    }

    @Override
    public ForOfType getForOfType() {
        return ForOfType.NATIVE;
    }

    @Override
    public boolean supportsModifyingThis() {
        return true;
        // if (nashornCompatibility) {
        // return true;
        // }
        //
        // return false;
    }

    public Value eval(String code) {
        return engine.getPolyglotContext().eval("js", code);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> evalOld(String code) {

        try {
            return (Map<String, Object>) engine.eval(code);
        } catch (ScriptException e) {
            throw new DefaultLARAException("Could not execute code: '" + code + "'", e);
        }
    }

    public Bindings newNativeArray() {
        return new GenericBindings(evalOld(NEW_ARRAY));
        // try {
        // Map<String, Object> array = (Map<String, Object>) engine.eval(NEW_ARRAY);
        //
        // return new GenericBindings(array);
        // } catch (ScriptException e) {
        // throw new DefaultLARAException("Could not create new array ", e);
        // }
    }

    /**
     * Based on this site: http://programmaticallyspeaking.com/nashorns-jsobject-in-context.html
     *
     * @return
     */
    @Override
    public Object getUndefined() {
        var array = engine.getPolyglotContext().eval("js", "[undefined]");

        return array.getArrayElement(0);
    }

    @Override
    public String stringify(Object object) {
        Value json = eval("JSON");

        return json.invokeMember("stringify", object).toString();
        // return json.invokeMember("stringify", asValue(object).asProxyObject()).toString();
    }

    @Override
    public Value getBindings() {
        return engine.getPolyglotContext().getBindings("js");
        // return engine.getPolyglotContext().getPolyglotBindings();
    }

    @Override
    public void put(Bindings object, String member, Object value) {
        Value valueObject = asValue(object);
        valueObject.putMember(member, value);
    }

    /**
     * This implementation is slow, since Graal does not support sharing Contexts between engines.
     * 
     * <p>
     * A new engine will be created, and the contents of the given scope will be converted to code and loaded into the
     * new engine, before executing the code.
     */
    @Override
    public Object eval(String code, Object scope) {
        GraalvmJsEngine newEngine = new GraalvmJsEngine();
        Value scopeValue = asValue(scope);

        // Add scope code
        for (String key : scopeValue.getMemberKeys()) {
            newEngine.eval(key + " = " + stringify(scopeValue.getMember(key)));
        }

        // Execute new code
        return newEngine.eval(code);

        // newEngine.getPolyglotContext().
        // var newScriptContext = new SimpleScriptContext();
        // newScriptContext.setBindings(scope, ScriptContext.ENGINE_SCOPE);
        // try {
        // return newEngine.eval(code, newScriptContext);
        // } catch (ScriptException e) {
        // throw new RuntimeException(e);
        // }
        // newEngine.getEngine().setBindings(scope, ScriptContext.ENGINE_SCOPE);
        // return newEngine.eval(code);
        // Bindings previousBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        // engine.setBindings(scope, ScriptContext.ENGINE_SCOPE);
        // Value result = eval(code);
        // engine.setBindings(previousBindings, ScriptContext.ENGINE_SCOPE);
        // return result;
    }

    public Value asValue(Object object) {
        return engine.getPolyglotContext().asValue(object);
    }
}
