/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.weaver.interf;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.lara.interpreter.profile.BasicWeaverProfiler;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.Preconditions;
import pt.up.fe.specs.util.providers.ResourceProvider;

/**
 * Interface for connecting the lara interpreter with the target language weaver. A Weaver can be associated to an
 * application folder or to only one file. The interpreter creates a new weaver instance for the application folder or
 * an instance for each file. The begin(File) function must return if the File argument is a valid File/Folder.
 *
 * @author Tiago D.R. Carvalho
 */
public abstract class WeaverEngine {

    private EventTrigger eventTrigger;
    private WeaverProfiler weaverProfiler = BasicWeaverProfiler.emptyProfiler();

    /**
     * Warns the lara interpreter if the weaver accepts a folder as the application or only one file at a time
     *
     * @return true if the weaver is able to work with several files, false if only works with one file
     * @deprecated this method is not called anymore as LaraI now assumes that the weaver always accepts a folder
     */
    @Deprecated
    public boolean handlesApplicationFolder() {
        return true;
    }

    /**
     * Set a file in the weaver if it is valid file type for the weaver.
     *
     * @param sourceDir
     *            the file/directory with the source code
     * @param outputDir
     *            output directory for the generated file(s)
     * @oaram dataStore the dataStore containing the options for the weaver
     * @return true if the file type is valid
     */
    // public boolean begin(File sourceDir, File outputDir, DataStore dataStore);
    public abstract boolean begin(List<File> sources, File outputDir, DataStore dataStore);

    /**
     * Get the list of available actions in the weaver
     *
     * @return list with all actions
     */
    public abstract List<String> getActions();

    /**
     * Closes the weaver and specifies the output directory location if the weaver generates new file(s)
     *
     * @return if close was successful
     */
    public abstract boolean close();

    /**
     *
     *
     * @return an instance of the join point root/program
     */

    /**
     *
     * Return a JoinPoint instance of the language root
     *
     * @return interface implementation for the join point root/program
     */
    public abstract JoinPoint select();

    /**
     * Returns the name of the join point model root
     *
     * @return then name of the join point model root
     */
    public abstract String getRoot();

    /**
     * Returns a list of options the weaver accepts
     *
     * @return
     */
    // default
    public abstract List<WeaverOption> getOptions()
    // {
    // return Collections.emptyList();
    // }
    ;

    /**
     * The Language Specification associated to this weaver. This specification is required for the LARA compiler
     * (larac)
     *
     */
    public abstract LanguageSpecification getLanguageSpecification();

    /**
     * Returns a list of Gears associated to this weaver engine
     *
     * @return a list of implementations of {@link AGear} or null if no gears are available
     */
    public abstract List<AGear> getGears();

    /**
     * Returns a list of classes that may be imported and used in LARA.
     *
     * @return
     */
    public List<Class<?>> getImportableClasses() {
        return Collections.emptyList();
    }

    /**
     * Returns a list of classes that may be imported and used in LARA, including the ones from the auto-generated code
     * and the weaver-developer-defined.
     *
     * @return
     */
    public List<Class<?>> getAllImportableClasses() {
        return getImportableClasses();
    }

    /**
     * Returns a list with the resources for javascript files.
     *
     * @return
     */
    public List<ResourceProvider> getImportableScripts() {
        return Collections.emptyList();
    }

    /**
     * The name of the Weaver.
     *
     * @return
     */
    public Optional<String> getName() {
        return Optional.empty();
    }

    /**
     * Return a list of resources that are lara files
     *
     * @return
     */
    public List<ResourceProvider> getAspectsAPI() {
        return Collections.emptyList();
    }

    public EventTrigger getEventTrigger() {
        return eventTrigger;
    }

    public void setEventTrigger(EventTrigger eventTrigger) {
        this.eventTrigger = eventTrigger;
    }

    public boolean hasListeners() {
        return eventTrigger != null && eventTrigger.hasListeners();
    }

    public abstract boolean implementsEvents();

    public WeaverProfiler getWeaverProfiler() {
        return weaverProfiler;
    }

    /**
     * Use this method if you intend to use your own weaver profiler by extending class {@link WeaverProfiler}
     * 
     * @return
     */
    protected void setWeaverProfiler(WeaverProfiler weaverProfiler) {
        this.weaverProfiler = weaverProfiler;
    }
    //
    // protected void associateJoinPoint(JoinPoint joinPoint) {
    // joinPoint.setWeaverEngine(this);
    // }

    /**
     * An image representing the icon of the program, that will appear in the upper-left corner.
     * 
     * @return by default, returns null
     */
    public ResourceProvider getIcon() {
        return null;
    }

    /**
     * The names of the weaver. These strings will be used to process folders for LARA bundles.
     * 
     * @return the names of the weaver. By default, returns the class name in lower-case, and without the suffix
     *         "weaver", if one is present
     */
    public Set<String> getWeaverNames() {
        String weaverName = getClass().getSimpleName().toLowerCase();
        if (weaverName.endsWith("weaver")) {
            weaverName = weaverName.substring(0, weaverName.length() - "weaver".length());
        }

        return new HashSet<>(Arrays.asList(weaverName));
    }

    /**
     * The languages supported by the weaver. These strings will be used to process folders for LARA bundles.
     * 
     * @return the languages supported by the weaver. By default, returns empty.
     */
    public Set<String> getLanguages() {
        return Collections.emptySet();
    }

    private static ThreadLocal<WeaverEngine> WEAVER = new ThreadLocal<>();

    private static void setWeaverStatic(WeaverEngine aWeaver) {
        Preconditions.checkArgument(WEAVER.get() == null,
                "Tried to set weaver but there is already a weaver present in this thread");
        WEAVER.set(aWeaver);
    }

    private static void removeWeaverStatic() {
        Preconditions.checkNotNull(WEAVER.get(), "Tried to get weaver, but there is not weaver set");
        WEAVER.remove();
    }

    protected static WeaverEngine getWeaverStatic() {
        WeaverEngine weaver = WEAVER.get();
        Preconditions.checkNotNull(weaver, "Tried to get weaver, but there is not weaver set");
        return weaver;
    }

    public void setWeaver() {
        setWeaverStatic(this);
    }

    public boolean isWeaverSet() {
        return WEAVER.get() != null;
    }

    public void removeWeaver() {
        removeWeaverStatic();
    }

}
