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
import java.util.List;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.api.LaraIo;
import org.lara.interpreter.api.WeaverApis;
import org.lara.interpreter.exception.AspectDefException;
import org.lara.interpreter.exception.JavaImportException;
import org.lara.interpreter.exception.ScriptImportException;
import org.lara.interpreter.exception.UserException;
import org.lara.interpreter.joptions.keys.FileList;
import org.lara.interpreter.utils.ExceptionUtils;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.MessageConstants;
import org.lara.interpreter.utils.WeaverSpecification;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.utils.FilterExpression;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.dsl.JoinPointFactory;
import org.lara.language.specification.dsl.LanguageSpecificationV2;

import Utils.LARASystem;
import larai.JsLaraCompatibilityResource;
import larai.LaraI;
import pt.up.fe.specs.lara.JsApiResource;
import pt.up.fe.specs.lara.LaraApis;
import pt.up.fe.specs.tools.lara.trace.CallStackTrace;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;

public class ImportProcessor {

    private static final Class<?>[] CLASSES_TO_IMPORT = { LARASystem.class, Stage.class, AspectDefException.class,
            ExceptionUtils.class, UserException.class, FilterExpression.class, JoinPoint.class, LaraIo.class };

    private final Interpreter interpreter;

    public static ImportProcessor newInstance(Interpreter interpreter) {
        return new ImportProcessor(interpreter);
    }

    private ImportProcessor(Interpreter interpreter) {
        this.interpreter = interpreter;
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

        // JS scripts that are needed for older LARA code
        for (final JsLaraCompatibilityResource resource : JsLaraCompatibilityResource.values()) {
            importScript(resource);
        }

        // JS scripts from LaraApi
        for (final JsApiResource resource : JsApiResource.values()) {
            importScript(resource);
        }

        /* Load weaver API scripts */
        LaraI laraI = interpreter.getLaraI();
        MasterWeaver weaver = laraI.getWeaver();
        WeaverEngine engine = weaver.getEngine();
        List<ResourceProvider> engineScripts = engine.getImportableScripts();
        engineScripts.forEach(this::importScript);

        /* Load user scripts */
        FileList includeFolders = interpreter.getOptions().getProcessedIncludeDirs(engine);
        /*
        // LaraBundle laraBundle = new LaraBundle(engine.getLanguages(), engine.getWeaverNames());
        LaraBundle laraBundle = new LaraBundle(engine.getWeaverNames(), interpreter.getOptions().getBundleTags());
        includeFolders = laraBundle.process(includeFolders);
        
        // Process LARA Resources
        LaraResource laraResource = new LaraResource(engine);
        includeFolders = laraResource.process(includeFolders);
        */
        if (!includeFolders.isEmpty()) {

            interpreter.out().println("Importing scripts/classes in included folders:");
            // getIncludedLocalScripts(laraInterp, includeFolders);

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
                final List<File> allScripts = SpecsIo.getFiles(includeFolder, "js");// getAllScriptFiles(includeFolder);

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

        for (Class<?> importingClass : LaraApis.getImportableClasses()) {
            importClassWithSimpleName(importingClass);
        }

        for (Class<?> importingClass : WeaverApis.getImportableClasses()) {
            importClassWithSimpleName(importingClass);
        }

        for (Class<?> importingClass : CLASSES_TO_IMPORT) {
            importClassWithSimpleName(importingClass);
        }

        final File jarDir = new File(LaraIUtils.getJarFoldername());
        final String laraPath = LARASystem.LARAPATH + "  = '" + jarDir.getAbsolutePath().replace('\\', '/') + "';\n";
        final String attribute = "var " + Interpreter.ATTRIBUTES
                + " = { set: function(newReport){ mergeObjects(this,newReport);}};\n";
        interpreter.evaluate(laraPath);
        interpreter.evaluate(attribute);
        LaraI laraI = interpreter.getLaraI();

        if (laraI.getOptions().useStackTrace()) {
            interpreter.put(CallStackTrace.STACKTRACE_NAME, interpreter.getStackStrace());
        }
        interpreter.put(MasterWeaver.WEAVER_NAME, laraI.getWeaver());
        interpreter.put(LARASystem.LARA_SYSTEM_NAME, new LARASystem(laraI));
        WeaverEngine engine = laraI.getWeaver().getEngine();
        LanguageSpecification languageSpecification = engine.getLanguageSpecification();
        LanguageSpecificationV2 ls = JoinPointFactory.fromOld(languageSpecification);
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
            String code = classToImport.getSimpleName() + " = Java.type('" + classToImport.getCanonicalName() + "');\n";
            interpreter.evaluate(code);
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
        // final File dir = new File(path);
        if (!dir.isDirectory()) {
            // WarningMsg.say("The path for the javascripts directory is
            // invalid: " + path);
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
        // URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        // Class.forName("nameofclass", true, new URLClassLoader(urlarrayofextrajarsordirs));

        Class<URLClassLoader> sysclass = URLClassLoader.class;

        for (final File classPath : jarFolderFiles) {
            interpreter.out().println(" " + MessageConstants.BRANCH_STR + SpecsIo.getCanonicalPath(classPath));

            try {
                Method method = sysclass.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(sysloader, new Object[] { classPath.toURI().toURL() });
            } catch (Throwable t) {
                throw new JavaImportException(classPath, t);
            } // end try catch
              // try {
              // if (classPath.getName().endsWith("test.jar")) {
              // // Class<?> forName = Class.forName("org.Test", true, sysloader);
              // Class<?> forName = Class.forName("org.Test");
              // System.out.println("--->" + forName);
              // }
              // } catch (Exception e) {
              // System.out.println("--->COULD NOT FIND Test: " + e.getMessage());
              // }
        }
    } // end method

    // }
    // LaraIUtils.includeClassPath(jarFolderFiles, interpreter.out());
    // LaraIUtils.includeClassPath(classPath);
    // if (classPath.getName().endsWith("hashexporter.jar")) {
    // try {
    // Class<?> forName = Class.forName("exporter.BinaryHashLARA");
    // System.out.println("--->" + forName);
    // } catch (Exception e) {
    // System.out.println("--->COULD NOT FIND BINARY: " + e.getMessage());
    // }
    // }

    // }
    // }

    private void importScript(ResourceProvider resource) {
        final String internalScripts = SpecsIo.getResource(resource);
        evaluateImport(internalScripts, resource.getResource(), true);
    }

    private void importScript(File source) {
        final String internalScripts = SpecsIo.read(source);
        evaluateImport(internalScripts, source.getAbsolutePath(), false);
    }

    private void evaluateImport(final String internalScripts, String source, boolean isInternal) {
        try {

            interpreter.out().println("  " + MessageConstants.BRANCH_STR + source);
            interpreter.evaluate(internalScripts);
        } catch (Exception e) {
            throw new ScriptImportException(source, isInternal, e);
        }
    }

}
