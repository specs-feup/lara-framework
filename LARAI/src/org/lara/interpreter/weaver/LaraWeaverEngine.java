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

import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;

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
        // System.out.println("Adding to " + API_NAME + "\n" + laraApis);
        addApis(API_NAME, laraApis);

        // Add weaver-specific APIs
        // addWeaverApis();
        var weaverApis = new ArrayList<ResourceProvider>();
        weaverApis.addAll(getAspectsAPI());
        // weaverApis.addAll(getNpmResources());
        weaverApis.addAll(getWeaverNpmResources());
        // System.out.println("Adding to " + getWeaverApiName() + "\n" + weaverApis);
        addApis(getWeaverApiName(), weaverApis);
    }

    @Override
    public List<ResourceProvider> getLaraApis() {
        return laraApis;
    }

    private List<ResourceProvider> buildLaraApis() {
        var laraAPIs = new ArrayList<ResourceProvider>();

        laraAPIs.addAll(LaraApis.getApis());
        laraAPIs.addAll(LaraCommonLang.getLaraCommonLangApi());
        // laraAPIs.addAll(getAspectsAPI());
        return laraAPIs;
    }

    @Override
    public List<ResourceProvider> getLaraCore() {
        return laraCore;
    }

    private List<ResourceProvider> buildLaraCore() {
        return new ArrayList<ResourceProvider>(getImportableScripts());
    }

    @Override
    public List<LaraResourceProvider> getNpmResources() {
        var npmResources = new ArrayList<LaraResourceProvider>();

        // LARA standard API
        npmResources.addAll(getLaraNpmResources());
        // npmResources.addAll(Arrays.asList(LaraApiJsResource.values()));

        // Weaver API
        npmResources.addAll(getWeaverNpmResources());

        return npmResources;
    }

    public List<LaraResourceProvider> getLaraNpmResources() {
        return Arrays.asList(LaraApiJsResource.values());
    }

    /**
     *
     * @return the APIs specific for this weaver implementation, excluding the standard LARA API. By default returns an
     *         empty list
     */
    protected List<LaraResourceProvider> getWeaverNpmResources() {
        return List.of();
    }
}
