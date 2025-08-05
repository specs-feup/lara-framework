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

import org.lara.interpreter.weaver.ast.AstMethods;
import org.lara.interpreter.weaver.ast.DummyAstMethods;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.SpecsThreadLocal;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Interface for connecting the lara interpreter with the target language
 * weaver. A Weaver can be associated to an
 * application folder or to only one file. The interpreter creates a new weaver
 * instance for the application folder or
 * an instance for each file. The begin(File) function must return if the File
 * argument is a valid File/Folder.
 *
 * @author Tiago D.R. Carvalho
 */
public abstract class WeaverEngine {
    private EventTrigger eventTrigger;
    private final Lazy<File> temporaryWeaverFolder;
    private final Lazy<StoreDefinition> storeDefinition;
    private final Lazy<LanguageSpecification> langSpec;

    public WeaverEngine() {
        temporaryWeaverFolder = Lazy.newInstance(WeaverEngine::createTemporaryWeaverFolder);
        storeDefinition = Lazy.newInstance(this::buildStoreDefinition);

        langSpec = Lazy.newInstance(this::buildLangSpecs);
    }

    public Optional<DataStore> getData() {
        throw new NotImplementedException(this);
    }

    private static File createTemporaryWeaverFolder() {
        String folderName = "lara_weaver_" + UUID.randomUUID().toString();
        return SpecsIo.mkdir(SpecsIo.getTempFolder(), folderName);
    }

    private StoreDefinition buildStoreDefinition() {
        // String weaverName = getName().orElse("<unnamed weaver>");
        String weaverName = getName();

        return new StoreDefinitionBuilder(weaverName)
                // Add weaver custom keys
                .addKeys(getOptions().stream().map(WeaverOption::dataKey).collect(Collectors.toList()))
                .build();
    }

    /**
     * Starts execution of the weaver, for the given arguments
     *
     * @param sources   the files/directories with the source code
     * @param outputDir output directory for the generated file(s)
     * @param dataStore the dataStore containing the options for the weaver
     * @return true if executed without errors
     */
    public abstract boolean run(DataStore dataStore);

    /**
     * Get the list of available actions in the weaver
     *
     * @return list with all actions
     */
    public abstract List<String> getActions();

    /**
     * Returns the name of the join point model root
     *
     * @return then name of the join point model root
     */
    public abstract String getRoot();

    /**
     * Function that can be called from LARA code to retrieve the root join point
     *
     * @return
     */
    public abstract JoinPoint getRootJp();

    public Object getRootNode() {
        return getRootJp().getNode();
    }

    /**
     * Returns a list of options the weaver accepts
     *
     * @return
     */
    public abstract List<WeaverOption> getOptions();

    /**
     * The store definition for the options specific to this weaver
     *
     * @return
     */
    public StoreDefinition getStoreDefinition() {
        return storeDefinition.get();
    }

    public LanguageSpecification getLanguageSpecificationV2() {
        return langSpec.get();
    }

    /**
     * Builds and returns the language specification for this weaver.
     *
     * @return
     */
    protected abstract LanguageSpecification buildLangSpecs();

    /**
     * Returns a list of Gears associated to this weaver engine
     *
     * @return a list of implementations of {@link AGear} or null if no gears are
     *         available
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
     * Returns a list of classes that may be imported and used in LARA, including
     * the ones from the auto-generated code
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
     * @return the name of the Weaver. By default, returns the simple name of the
     *         class
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * The name of the weaver with the build number, if available.
     *
     * @return
     */
    public String getNameAndBuild() {
        var appName = getName();

        var implVersion = SpecsSystem.getBuildNumber();
        if (implVersion != null) {
            appName += " (build " + implVersion + ")";
        }

        return appName;

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

    /**
     * The languages supported by the weaver. These strings will be used to process
     * folders for LARA bundles.
     *
     * @return the languages supported by the weaver. By default, returns empty.
     */
    public Set<String> getLanguages() {
        return Collections.emptySet();
    }

    /**
     * Returns a temporary unique folder that is live while the weaver is running.
     *
     * @return
     */
    public File getTemporaryWeaverFolder() {
        return temporaryWeaverFolder.get();
    }

    /**
     * @return true if the temporary weaver folder has been created
     */
    public boolean hasTemporaryWeaverFolder() {
        return temporaryWeaverFolder.isInitialized();
    }

    /**
     * Thread-scope WeaverEngine
     */
    private static final SpecsThreadLocal<WeaverEngine> THREAD_LOCAL_WEAVER = new SpecsThreadLocal<>(
            WeaverEngine.class);

    public static WeaverEngine getThreadLocalWeaver() {
        return THREAD_LOCAL_WEAVER.get();
    }

    public void setWeaver() {
        // If already set, check the weaver.
        // If it is the same, just return. Otherwise, throw exception.
        if (WeaverEngine.isWeaverSet()) {
            if (getThreadLocalWeaver() == this) {
                return;
            }

            throw new RuntimeException("Trying to set a different thread-local weaver (" + this
                    + ") without removing the previous weaver (" + getThreadLocalWeaver() + ")");
        }

        THREAD_LOCAL_WEAVER.set(this);
    }

    public static boolean isWeaverSet() {
        return THREAD_LOCAL_WEAVER.isSet();
    }

    public static void removeWeaver() {
        THREAD_LOCAL_WEAVER.remove();
    }

    public void writeCode(File outputFolder) {
        throw new NotImplementedException(getClass().getSimpleName() + ".writeCode() not yet implemented!");
    }

    public String getDefaultAttribute(String joinPointType) {
        var jp = getLanguageSpecificationV2().getJoinPoint(joinPointType);
        if (jp == null) {
            throw new RuntimeException("Used unsupported join point '" + joinPointType + "'");
        }

        return getLanguageSpecificationV2().getJoinPoint(joinPointType).getDefaultAttribute().orElse(null);
    }

    /**
     * Pairs of labels-values that will populate the predefined list of the option
     * "External Dependencies".
     * <p>
     * Default implementation returns a list with experimental LARA packages.
     *
     * @param labelValuePairs
     * @return
     */
    public List<String> getPredefinedExternalDependencies() {
        return Arrays.asList("Experimental - SourceAction",
                "https://github.com/specs-feup/lara-framework.git?folder=experimental/SourceAction");
    }

    /**
     * @return an instance with basic functionality required of AST nodes
     */
    public AstMethods getAstMethods() {
        return new DummyAstMethods(this);
    }
}
