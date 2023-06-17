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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.lara.interpreter.profile.BasicWeaverProfiler;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.weaver.ast.AstMethods;
import org.lara.interpreter.weaver.ast.DummyAstMethods;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.dsl.JoinPointFactory;
import org.lara.language.specification.dsl.LanguageSpecificationV2;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitionBuilder;

import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import pt.up.fe.specs.util.io.ResourceCollection;
import pt.up.fe.specs.util.io.ResourcesAsFiles;
import pt.up.fe.specs.util.lazy.Lazy;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.utilities.SpecsThreadLocal;

/**
 * Interface for connecting the lara interpreter with the target language weaver. A Weaver can be associated to an
 * application folder or to only one file. The interpreter creates a new weaver instance for the application folder or
 * an instance for each file. The begin(File) function must return if the File argument is a valid File/Folder.
 *
 * @author Tiago D.R. Carvalho
 */
public abstract class WeaverEngine {

    private final static String MSG_WRONG_WEAVER_EXTENDED = "Your weaver should extend LaraWeaverEngine instead of WeaverEngine. If you are using WeaverGenerator, make sure it is updated and run it again";

    private final static String APIS_FOLDER_SUFFIX = "_apis";
    private final static String LARA_CORE_FOLDER_SUFFIX = "_lara_core";

    // private final static String CHECKSUM_FILENAME = "checksum.txt";

    // private final static ThreadLocal<Map<String, File>> API_FOLDERS = ThreadLocal.withInitial(() -> new HashMap<>());
    private final static ThreadLocal<ResourcesAsFiles> API_FOLDERS = ThreadLocal
            .withInitial(() -> new ResourcesAsFiles());

    private EventTrigger eventTrigger;
    private WeaverProfiler weaverProfiler = BasicWeaverProfiler.emptyProfiler();
    private final Lazy<File> temporaryWeaverFolder;
    private final Lazy<StoreDefinition> storeDefinition;
    private final Lazy<LanguageSpecificationV2> langSpec;
    // private final Lazy<File> apisFolder;

    private JsEngine scriptEngine;

    private final Lazy<ResourceCollection> laraApis;
    private final Lazy<ResourceCollection> laraCore;

    public WeaverEngine() {
        temporaryWeaverFolder = Lazy.newInstance(WeaverEngine::createTemporaryWeaverFolder);
        storeDefinition = Lazy.newInstance(this::buildStoreDefinition);

        scriptEngine = null;

        // langSpec = Lazy.newInstance(() -> JoinPointFactory.fromOld(this.getLanguageSpecification()));
        langSpec = Lazy.newInstance(this::buildLangSpecsV2);
        // apisFolder = Lazy.newInstance(() -> buildFolder(APIS_FOLDER_SUFFIX, getLaraApis()));

        laraApis = Lazy.newInstance(() -> new ResourceCollection(getApiFoldername(APIS_FOLDER_SUFFIX),
                SpecsSystem.getBuildNumber() != null, getLaraApis()));
        laraCore = Lazy.newInstance(() -> new ResourceCollection(getApiFoldername(LARA_CORE_FOLDER_SUFFIX),
                SpecsSystem.getBuildNumber() != null, getLaraCore()));
    }

    /**
     * TODO: remove after {@link LanguageSpecification} has been dealt with
     * 
     * @return
     */
    protected LanguageSpecificationV2 buildLangSpecsV2() {
        return JoinPointFactory.fromOld(this.getLanguageSpecification());
    }

    public JsEngine getScriptEngine() {
        if (scriptEngine == null) {
            throw new RuntimeException("Java script engine has not been set for weaver: " + getName());
        }

        return scriptEngine;
    }

    public void setScriptEngine(JsEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public boolean hasScriptEngine() {
        return this.scriptEngine != null;
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
     * Function that can be called from LARA code to retrieve the root join point
     *
     * @return
     */
    public JoinPoint getRootJp() {
        return select();
    }

    public Object getRootNode() {
        return getRootJp().getNode();
    }

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
     * The store definition for the options specific to this weaver
     *
     * @return
     */
    public StoreDefinition getStoreDefinition() {
        return storeDefinition.get();
    }

    /**
     * The Language Specification associated to this weaver. This specification is required for the LARA compiler
     * (larac)
     * 
     * @deprecated please use getLanguageSpecificationV2() instead
     * 
     */
    @Deprecated
    public abstract LanguageSpecification getLanguageSpecification();

    public LanguageSpecificationV2 getLanguageSpecificationV2() {
        return langSpec.get();
    }

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
    public String getName() {
        return "<unnamed weaver>";
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
    // public Set<String> getWeaverNames() {
    // String weaverName = getClass().getSimpleName().toLowerCase();
    // if (weaverName.endsWith("weaver")) {
    // weaverName = weaverName.substring(0, weaverName.length() - "weaver".length());
    // }
    //
    // return new HashSet<>(Arrays.asList(weaverName));
    // }

    /**
     * The languages supported by the weaver. These strings will be used to process folders for LARA bundles.
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
     *
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

    public boolean executeUnitTestMode(DataStore dataStore) {
        SpecsLogs.msgInfo("Unit testing mode not implemented yet for this weaver");
        return false;
    }

    public void writeCode(File outputFolder) {
        throw new NotImplementedException(getClass().getSimpleName() + ".writeCode() not yet implemented!");
    }

    public String getDefaultAttribute(String joinPointType) {
        // var langSpec = getLanguageSpecification();
        //
        // var artifact = langSpec.getArtifacts().getArtifact(joinPointType);
        // System.out.println(
        // "ART TYPE: " + artifact.getClazz() + "; " + artifact.getTooltip() + "; " + artifact.getDefault());
        // for (var art : artifact.getAttribute()) {
        // System.out.println("ATTR NAME: " + art.getName());
        // }
        // return getLanguageSpecification().getArtifacts().getArtifact(joinPointType).getDefault();

        var jp = getLanguageSpecificationV2().getJoinPoint(joinPointType);
        if (jp == null) {
            throw new RuntimeException("Used unsupported join point '" + joinPointType + "'");
        }

        return getLanguageSpecificationV2().getJoinPoint(joinPointType).getDefaultAttribute().orElse(null);
    }

    /**
     * Pairs of labels-values that will populate the predefined list of the option "External Dependencies".
     * <p>
     * Default implementation returns a list with experimental LARA packages.
     * 
     * @param labelValuePairs
     * @return
     */
    public List<String> getPredefinedExternalDependencies() {
        return Arrays.asList("Experimental - SourceAction",
                "https://github.com/specs-feup/lara-framework.git?folder=experimental/SourceAction",
                "Experimental - Mutation",
                "https://github.com/specs-feup/lara-framework.git?folder=experimental/Mutation");
    }

    /**
     * 
     * @return an instance with basic functionality required of AST nodes
     */
    public AstMethods getAstMethods() {
        return new DummyAstMethods(this);
    }

    public List<ResourceProvider> getLaraApis() {
        throw new RuntimeException(MSG_WRONG_WEAVER_EXTENDED);
    }

    public List<ResourceProvider> getLaraCore() {
        throw new RuntimeException(MSG_WRONG_WEAVER_EXTENDED);
    }

    public File getApisFolder() {
        return API_FOLDERS.get().getApiFolder(laraApis.get());
        // return getApiFolder(APIS_FOLDER_SUFFIX, getLaraApis());
    }

    public File getLaraCoreFolder() {
        return API_FOLDERS.get().getApiFolder(laraCore.get());
        // return getApiFolder(LARA_CORE_FOLDER_SUFFIX, getLaraCore());
    }

    private String getApiFoldername(String suffix) {
        return getNameAndBuild().replace(' ', '_') + suffix;
    }

}
