/**
 * Copyright 2023 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.utils.LaraResourceProvider;
import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.lara.LaraApiJsResource;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.lara.commonlang.LaraCommonLang;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.providers.ResourceProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class LaraWeaverEngine extends WeaverEngine {

    private static final String API_NAME = "@specs-feup/lara";

    private final List<ResourceProvider> laraApis;
    private final List<ResourceProvider> laraCore;

    private LaraWeaverState state;
    private boolean hasRun;

    public LaraWeaverEngine() {
        laraApis = buildLaraApis();
        laraCore = buildLaraCore();
        state = null;
        hasRun = false;

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

        return run(sources, outputDir, dataStore);
    }

    private boolean run(List<File> sources, File outputDir, DataStore dataStore) {
        hasRun = true;

        // Initialize state
        state = new LaraWeaverState(outputDir, dataStore);

        var success = begin(sources, outputDir, dataStore);
        if (!success) {
            return false;
        }

        // Register gears
        registerGears();

        return true;
    }

    private void registerGears() {
        var eventTrigger = new EventTrigger();
        eventTrigger.registerReceivers(getGears());
        setEventTrigger(eventTrigger);
    }

    /**
     * After the weaver has run at least once, can restart the weaver with the given files, outputdir and options.
     *
     * @param sources
     * @param outputDir
     * @param dataStore
     * @return
     */
    public boolean restart(List<File> sources, File outputDir, DataStore dataStore) {
        if (!hasRun) {
            return run(sources, outputDir, dataStore);
        }

        // Re-initialize state
        state.close();
        state = new LaraWeaverState(outputDir, dataStore);

        // Close weaver
        close();

        // Update workspace
        dataStore.set(LaraiKeys.WORKSPACE_FOLDER, FileList.newInstance(sources));

        var success = begin(sources, outputDir, dataStore);
        if (!success) {
            return false;
        }

        // Re-apply gears
        registerGears();

        return true;
    }

    /**
     * Overload that receives only the sources.
     *
     * @param sources
     * @return
     */
    public boolean restart(List<File> sources) {
        return restart(sources, getLaraWeaverState().getOutputDir(), getLaraWeaverState().getData());
    }

    /**
     * Overload that creates temporary files, to ease integration with JS.
     *
     * @param filenames
     * @param codes
     * @return
     */
    public boolean restart(String[] filenames, String[] codes) {

        if (filenames.length != codes.length) {
            throw new RuntimeException("Expected length of filenames and codes to be the same: " + filenames.length + " vs " + codes.length);
        }

        // Create files in temporary folder
        var files = new ArrayList<File>();
        var tempFolder = SpecsIo.getTempFolder();
        for (int i = 0; i < filenames.length; i++) {
            var tempFile = new File(tempFolder, filenames[i]);
            SpecsIo.write(tempFile, codes[i]);
            tempFile.deleteOnExit();
        }

        // Call restart
        return restart(files);
    }

    /**
     * Closes the weaver and ends execution.
     */
    public boolean end() {
        hasRun = false;

        state.close();
        state = null;

        // Close weaver
        return close();
    }

    @Override
    public Optional<DataStore> getData() {
        if (state == null) {
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
     * @param sources   the file/directory with the source code
     * @param outputDir output directory for the generated file(s)
     * @param dataStore the dataStore containing the options for the weaver
     * @return true if executed without errors
     */
    protected abstract boolean begin(List<File> sources, File outputDir, DataStore dataStore);

    /**
     * Closes the weaver and specifies the output directory location if the weaver generates new file(s)
     *
     * @return if close was successful
     */
    protected abstract boolean close();

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
     * empty list
     */
    protected List<LaraResourceProvider> getWeaverNpmResources() {
        return List.of();
    }


    /**
     * @param name
     * @return
     */
    public Class<?> getClass(String name) {
        try {
            return getLaraWeaverState().getClassLoader().loadClass(name);
            //return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find class", e);
        }
    }

}
