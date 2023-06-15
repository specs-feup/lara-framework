/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter.weaver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lara.interpreter.api.WeaverApis;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import larai.JsLaraCompatibilityResource;
import pt.up.fe.specs.lara.JsApiResource;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.ResourceProvider;

public abstract class LaraWeaverEngine extends WeaverEngine {

    private final Lazy<List<ResourceProvider>> laraApis;
    private final Lazy<List<ResourceProvider>> laraCore;

    public LaraWeaverEngine() {
        laraApis = Lazy.newInstance(this::buildLaraApis);
        laraCore = Lazy.newInstance(this::buildLaraCore);
    }

    @Override
    public List<ResourceProvider> getLaraApis() {
        return laraApis.get();
    }

    private List<ResourceProvider> buildLaraApis() {
        var laraAPIs = new ArrayList<ResourceProvider>();
        laraAPIs.addAll(LaraApis.getApis());
        laraAPIs.addAll(WeaverApis.getApis());
        laraAPIs.addAll(LaraCommonLang.getLaraCommonLangApi());
        laraAPIs.addAll(getAspectsAPI());

        return laraAPIs;
    }

    @Override
    public List<ResourceProvider> getLaraCore() {
        return laraCore.get();
    }

    private List<ResourceProvider> buildLaraCore() {
        var coreScripts = new ArrayList<ResourceProvider>();
        coreScripts.addAll(Arrays.asList(JsLaraCompatibilityResource.values()));
        coreScripts.addAll(Arrays.asList(JsApiResource.values()));

        return coreScripts;
    }
}
