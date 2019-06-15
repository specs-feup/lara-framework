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
import java.util.Set;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

public class GraalvmJsEngine implements JsEngine {

    private final ScriptEngine engine;

    public GraalvmJsEngine(Collection<Class<?>> blacklistedClasses) {

        Set<String> forbiddenClasses = blacklistedClasses.stream().map(Class::getName).collect(Collectors.toSet());

        Context.Builder contextBuilder = Context.newBuilder("js")
                // .allowAllAccess(true);
                .allowHostAccess(HostAccess.ALL)
                .allowCreateThread(true)
                .allowHostClassLookup(name -> !forbiddenClasses.contains(name));
        this.engine = GraalJSScriptEngine.create(null, contextBuilder);
    }

    public GraalvmJsEngine() {
        this(Collections.emptyList());
    }

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }

    @Override
    public ForOfType getForOfType() {
        return ForOfType.NATIVE;
    }
}
