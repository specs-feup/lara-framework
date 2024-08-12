/**
 * Copyright 2016 SPeCS.
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

package org.lara.interpreter.generator.stmt;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.api.LaraIo;
import org.lara.interpreter.exception.AspectDefException;
import org.lara.interpreter.exception.JavaImportException;
import org.lara.interpreter.exception.ScriptImportException;
import org.lara.interpreter.exception.UserException;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.utils.ExceptionUtils;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.MessageConstants;
import org.lara.interpreter.utils.WeaverSpecification;
import org.lara.interpreter.weaver.LaraExtension;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.utils.FilterExpression;
import org.lara.language.specification.dsl.LanguageSpecificationV2;

import larai.LaraI;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.tools.lara.logging.LaraLog;
import pt.up.fe.specs.tools.lara.trace.CallStackTrace;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;
import utils.LARASystem;

public class ImportProcessor {

    private static final Class<?>[] CLASSES_TO_IMPORT = { LARASystem.class, Stage.class, AspectDefException.class,
            ExceptionUtils.class, UserException.class, FilterExpression.class, JoinPoint.class, LaraIo.class };

    private final Interpreter interpreter;
    private final Set<String> loadedSources;

    public static ImportProcessor newInstance(Interpreter interpreter) {
        return new ImportProcessor(interpreter);
    }

    private ImportProcessor(Interpreter interpreter) {
        this.interpreter = interpreter;
        this.loadedSources = new HashSet<>();
    }

    /**
     * Import the scripts and classes in the jar and the included folders
     *
     * @param laraInt
     * @param cx
     * @param scope
     */
    public void importScriptsAndClasses() {
        interpreter.out().println("Importing internal scripts:");

        /* Load weaver API scripts */
        LaraI laraI = interpreter.getLaraI();
        MasterWeaver weaver = laraI.getWeaver();
        WeaverEngine engine = weaver.getEngine();

        // Import all scripts in the new 'core' folder as modules (.mjs)
        var newFilesToImport = engine.getApiManager().getNpmCoreFiles();
        newFilesToImport.stream().filter(file -> LaraExtension.isValidExtension(file))
                .forEach(source -> evaluateImport(SpecsIo.read(source), source.getAbsolutePath(), false,
                        JsFileType.MODULE));

        // Import all scripts in the old 'core' folder
        var oldFilesToImport = SpecsIo.getFilesRecursive(engine.getApiManager().getCoreFolder());
        oldFilesToImport.stream().filter(file -> LaraExtension.isValidExtension(file))
                .forEach(this::importScript);

        /* Load user scripts */
        FileList includeFolders = interpreter.getOptions().getProcessedIncludeDirs(engine);

        if (!includeFolders.isEmpty()) {

            interpreter.out().println("Importing scripts/classes in included folders:");

            // Also include java and javascript files which respect the
            // following structure inside the include folders:
            // <include>/java
            // <include>/scripts
            for (final File includeFolder : includeFolders) {

                if (!includeFolder.exists()) {
                    interpreter.out().warnln("Included folder '" + includeFolder + "' does not exist.");
                    continue;
                }

                // Get JAVA files from "java" folder
                final File javaPath = new File(includeFolder, "java");
                // Get JS files from "scripts" folder
                final File scriptsPath = new File(includeFolder, "scripts");

                // Add scripts that are directly included in the 'includeFolders'
                final List<File> allJava = getAllJavaClassPaths(includeFolder); // right now it is just using jars
                final List<File> allScripts = new ArrayList<>();

                var importScripts = interpreter.getOptions().isAutomaticallyIncludeJs();

                if (importScripts) {
                    allScripts.addAll(SpecsIo.getFiles(includeFolder, "js"));// getAllScriptFiles(includeFolder);
                    allScripts.addAll(SpecsIo.getFiles(includeFolder, "mjs"));
                }

                if (javaPath.exists()) {
                    final List<File> javaFolderFiles = getAllJavaClassPaths(javaPath);
                    allJava.addAll(javaFolderFiles);
                }
                if (scriptsPath.exists()) {
                    final List<File> scriptFolderFiles = getAllScriptFiles(scriptsPath);
                    allScripts.addAll(scriptFolderFiles);
                }
                // Add JAVA files
                importClassPaths(allJava);

                // Add JS files
                allScripts.forEach(this::importScript);

            }
        }

    }

    /**
     *
     */
    public void importAndInitialize() {

        for (Class<?> importingClass : CLASSES_TO_IMPORT) {
            importClassWithSimpleName(importingClass);
        }

        final File jarDir = new File(LaraIUtils.getJarFoldername());
        final String laraPath = "var " + LARASystem.LARAPATH + "  = '" + jarDir.getAbsolutePath().replace('\\', '/')
                + "';\n";
        final String attribute = "var " + Interpreter.ATTRIBUTES
                + " = { set: function(newReport){ mergeObjects(this,newReport);}};\n";
        interpreter.evaluate(laraPath, "import_and_initialize");
        interpreter.evaluate(attribute, "import_and_initialize");
        LaraI laraI = interpreter.getLaraI();

        if (laraI.getOptions().useStackTrace()) {
            interpreter.put(CallStackTrace.STACKTRACE_NAME, interpreter.getStackStrace());
        }
        interpreter.put(MasterWeaver.WEAVER_NAME, laraI.getWeaver());
        interpreter.put(LARASystem.LARA_SYSTEM_NAME, new LARASystem(laraI));
        WeaverEngine engine = laraI.getWeaver().getEngine();
        LanguageSpecificationV2 ls = engine.getLanguageSpecificationV2();
        interpreter.put(MasterWeaver.LANGUAGE_SPECIFICATION_NAME,
                WeaverSpecification.newInstance(ls, engine.getScriptEngine()));
    }

    /**
     * Import a class in the interpreter and associate the simple name to the class
     *
     * @param classToImport
     */
    public void importClassWithSimpleName(Class<?> classToImport) {
        // importClass(classToImport);
        try {
            String code = "var " + classToImport.getSimpleName() + " = Java.type('" + classToImport.getCanonicalName()
                    + "');\n";
            interpreter.evaluate(code, "import_class_" + classToImport.getSimpleName());
        } catch (Exception e) {
            throw new JavaImportException(new File(classToImport.getCanonicalName()), e);
        }
    }

    /**
     * This method returns all classpaths to use in LARA This includes the current folder and all the jars in this
     * folder
     *
     * @param folder
     * @return
     */
    private List<File> getAllJavaClassPaths(File folder) {
        final ArrayList<File> files = new ArrayList<>();
        // final File dir = new File(path);
        if (!folder.isDirectory()) {
            interpreter.out().warnln("The path for the includes is invalid: " + folder);
            return files;
        }
        files.add(folder);
        for (final File f : folder.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".jar") || f.getName().endsWith(".class")) {
                files.add(f);
            }
        }
        return files;
    }

    // ================================================================================//
    // Utilities //
    // ================================================================================//
    public static List<File> getAllScriptFiles(File dir) {
        final ArrayList<File> files = new ArrayList<>();

        if (!dir.isDirectory()) {
            return files;
        }
        for (final File f : dir.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".js")) {
                files.add(f);
            }
        }
        return files;
    }

    private void importClassPaths(final List<File> jarFolderFiles) {
        // Check if Java 9 or greater
        if (SpecsSystem.getJavaVersionNumber() > 1.8) {
            SpecsLogs.debug("Importing classes dynamically not supported for Java 9 or greater");
            return;
        }

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        Class<URLClassLoader> sysclass = URLClassLoader.class;

        for (final File classPath : jarFolderFiles) {
            interpreter.out().println(" " + MessageConstants.BRANCH_STR + SpecsIo.getCanonicalPath(classPath));

            try {
                Method method = sysclass.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(sysloader, new Object[] { classPath.toURI().toURL() });
            } catch (Throwable t) {
                throw new JavaImportException(classPath, t);
            }
        }
    } // end method

    private void importScript(ResourceProvider resource) {
        final String internalScripts = SpecsIo.getResource(resource);
        var extension = SpecsIo.getExtension(resource.getFilename());
        evaluateImport(internalScripts, resource.getResource(), true, JsFileType.getType(extension));
    }

    private void importScript(File source) {
        final String internalScripts = SpecsIo.read(source);
        var extension = SpecsIo.getExtension(source);
        evaluateImport(internalScripts, source.getAbsolutePath(), false, JsFileType.getType(extension));
    }

    private void evaluateImport(final String internalScripts, String source, boolean isInternal, JsFileType type) {
        try {

            // Check if source was already loaded
            if (loadedSources.contains(source)) {
                LaraLog.debug("ImportProcessor: ignoring already loaded source '" + source + "'");
                return;
            }

            interpreter.out().println("  " + MessageConstants.BRANCH_STR + source);
            interpreter.evaluate(internalScripts, type, source);

            // Add source to loaded sources
            loadedSources.add(source);

        } catch (Exception e) {
            throw new ScriptImportException(source, isInternal, e);
        }
    }

}
