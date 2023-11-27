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
import org.lara.interpreter.weaver.utils.LaraResourceProvider;

import larai.JsLaraCompatibilityResource;
import pt.up.fe.specs.lara.LaraApiJsResource;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.util.providers.ResourceProvider;

public abstract class LaraWeaverEngine extends WeaverEngine {

    private static final String API_NAME = "lara-js";

    private final List<ResourceProvider> laraApis;
    private final List<ResourceProvider> laraCore;

    public LaraWeaverEngine() {
        laraApis = buildLaraApis();
        laraCore = buildLaraCore();

        // Add LARA APIs
        addApis(API_NAME, laraApis);

        // Add weaver-specific APIs
        addWeaverApis();
    }

    @Override
    public List<ResourceProvider> getLaraApis() {
        return laraApis;
    }

    private List<ResourceProvider> buildLaraApis() {
        var laraAPIs = new ArrayList<ResourceProvider>();

        laraAPIs.addAll(LaraApis.getApis());
        laraAPIs.addAll(WeaverApis.getApis());
        laraAPIs.addAll(LaraCommonLang.getLaraCommonLangApi());
        // laraAPIs.addAll(getAspectsAPI());
        return laraAPIs;
    }

    @Override
    public List<ResourceProvider> getLaraCore() {
        return laraCore;
    }

    private List<ResourceProvider> buildLaraCore() {
        var coreScripts = new ArrayList<ResourceProvider>();
        coreScripts.addAll(Arrays.asList(JsLaraCompatibilityResource.values()));
        coreScripts.addAll(getImportableScripts());

        return coreScripts;
    }

    @Override
    public List<LaraResourceProvider> getNpmResources() {
        return Arrays.asList(LaraApiJsResource.values());
    }
}
