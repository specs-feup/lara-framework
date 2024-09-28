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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;

import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.lara.LaraApiJsResource;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.providers.ResourceProvider;

public abstract class LaraWeaverEngine extends WeaverEngine {

    private static final String API_NAME = "@specs-feup/lara";

    private final List<ResourceProvider> laraApis;
    private final List<ResourceProvider> laraCore;

    private LaraWeaverState state;

    public LaraWeaverEngine() {
        laraApis = buildLaraApis();
        laraCore = buildLaraCore();
        state = null;

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
    public boolean run(DataStore dataStore) {
        File outputDir = dataStore.get(LaraiKeys.OUTPUT_FOLDER);
        List<File> sources = dataStore.get(LaraiKeys.WORKSPACE_FOLDER).getFiles();

        // Initialize state
        state = new LaraWeaverState(dataStore);

        return begin(sources, outputDir, dataStore);
    }

    /**
     * TODO: Needs similar treatement as begin()/run()
     */
    public void closeTop() {
        state.close();
    }

    @Override
    public Optional<DataStore> getData() {
        if(state == null) {
            return Optional.empty();
        }

        return Optional.of(state.getData());
    }

    public Optional<LaraWeaverState> getLaraWeaverStateTry() {
        return Optional.ofNullable(state);
    }

    public LaraWeaverState getLaraWeaverState() {
        return getLaraWeaverStateTry().orElseThrow(() -> new RuntimeException("No LARA weaver state defined"));
    }

    /**
     * This method will be called at the end of method run()
     *
     * @param sources
     *            the file/directory with the source code
     * @param outputDir
     *            output directory for the generated file(s)
     * @param dataStore
     *            the dataStore containing the options for the weaver
     * @return true if executed without errors
     */
    public abstract boolean begin(List<File> sources, File outputDir, DataStore dataStore);

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
     * The preferred way of distributing APIs is now through NPM modules, for that override this method.
     *
     * @return the APIs specific for this weaver implementation, excluding the standard LARA API. By default returns an
     *         empty list
     */
    protected List<LaraResourceProvider> getWeaverNpmResources() {
        return List.of();
    }


    /**
     *
     * @param name
     * @return
     */
    public Class<?> getClass(String name) {
        try {
            return  getLaraWeaverState().getClassLoader().loadClass(name);
            //return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class", e);
        }
    }

}
