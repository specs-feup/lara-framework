/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.options.WeaverOption;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import pt.up.fe.specs.util.SpecsIo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate the weaver abstract class, containing the four methods to implement:
 * handlesApplicationFolder, begin, select
 * and close. The getActions method (list of available actions) will be
 * automatically generated. The getRoot method
 * (returns the name of the root join point) will be automatically generated.
 *
 * @author tiago
 */
public class WeaverImplGenerator extends GeneratorHelper {

    protected WeaverImplGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes
     * and actions
     *
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        final WeaverImplGenerator gen = new WeaverImplGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes
     * and actions
     *
     */
    @Override
    public JavaClass generate() {
        final JavaClass java = generateWeaverClass();

        addBeginMethod(java);
        addCloseMethod(java);
        addGetGearsMethod(java);
        addGetOptionsMethod(java);
        addGetWeaver(java);
        addBuildLanguageSpecification(java);

        return java;
    }

    private void addGetWeaver(JavaClass java) {
        String weaverName = java.getName();
        JavaType weavingEngineClass = new JavaType(weaverName);

        Method getWE = new Method(weavingEngineClass, "get" + weaverName);
        getWE.add(Modifier.STATIC);
        getWE.appendComment("Returns thread-local instance of weaver engine.");
        getWE.appendCode("return (" + weaverName + ") WeaverEngine.getThreadLocalWeaver();");
        java.add(getWE);

        java.addImport("org.lara.interpreter.weaver.interf.WeaverEngine");
    }

    /**
     * Generate the Weaver abstract class with name : A + the weaver name, in the
     * given package and
     *
     * @return JavaClass java = new JavaClass(weaverName, outPackage); String text =
     *         getWeaverText(weaverName,
     *         outPackage); java.appendComment(text); java.add(JDocTag.AUTHOR, "Lara
     *         C."); java.setSuperClass(new
     *         JavaType(abstractWeaver.getName(),
     *         abstractWeaver.getClassPackage()));
     */
    private JavaClass generateWeaverClass() {
        final String weaverName = javaGenerator.getWeaverName();
        final JavaClass java = new JavaClass(weaverName, javaGenerator.getWeaverPackage());
        java.setSuperClass(new JavaType("A" + weaverName, javaGenerator.getAbstractWeaverPackage()));

        final String text = JavaAbstractsGenerator.getWeaverText(weaverName, javaGenerator.getAUserJoinPointType());
        java.appendComment(text);
        java.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());
        return java;
    }

    /**
     * Generates the method that starts the weaving process
     *
     */
    private static void addBeginMethod(JavaClass java) {
        final Method begin = new Method(JavaTypeFactory.getBooleanType(), "begin");
        final JavaType fileType = new JavaType(File.class);
        final JavaType listType = JavaTypeFactory.convert(List.class);
        final JavaType dataStoreType = JavaTypeFactory.convert(DataStore.class);

        JavaTypeFactory.addGenericType(listType, fileType);

        begin.add(Annotation.OVERRIDE);
        begin.addArgument(listType, "sources");
        begin.addArgument(fileType, "outputDir");
        final JavaType stringType = JavaTypeFactory.getStringType();
        stringType.setArray(true);
        begin.addArgument(dataStoreType, "args");
        begin.appendComment("Setups the weaver with inputs sources, an output folder and the provided options." + ln());
        begin.addJavaDocTag(JDocTag.PARAM, "sources the sources with the code (files/folders)");
        begin.addJavaDocTag(JDocTag.PARAM, "outputDir output folder for the generated file(s)");
        begin.addJavaDocTag(JDocTag.PARAM, "args options for the weaver");
        begin.addJavaDocTag(JDocTag.RETURN, "true if initialization occurred without problems, false otherwise");
        begin.appendCode("//Initialize weaver with the input file/folder" + ln());
        begin.appendCode("throw new UnsupportedOperationException(\"Method begin for " + java.getName()
                + " is not yet implemented\");");
        java.add(begin);
    }

    /**
     * Generates the method that closes the weaving process
     *
     */
    private static void addCloseMethod(JavaClass java) {
        final Method close = new Method(JavaTypeFactory.getBooleanType(), "close");

        close.add(Annotation.OVERRIDE);
        close.appendComment(
                " Performs operations needed when closing the weaver (e.g., generates new version of source file(s) to the specified output folder)."
                        + ln());
        close.addJavaDocTag(JDocTag.RETURN, "if close was successful");
        close.appendCode("//Terminate weaver execution with final steps required and writing output files" + ln());
        close.appendCode("throw new UnsupportedOperationException(\"Method close for " + java.getName()
                + " is not yet implemented\");");
        java.add(close);
    }

    /**
     * Generates the default code for method getGears
     *
     */
    private static void addGetGearsMethod(JavaClass java) {
        final JavaGenericType genGearType = new JavaGenericType(new JavaType(AGear.class));
        final JavaType listType = JavaTypeFactory.getListJavaType(genGearType);
        final Method getGears = new Method(listType, "getGears");
        getGears.appendComment(" Returns a list of Gears associated to this weaver engine" + ln());
        getGears.addJavaDocTag(JDocTag.RETURN,
                "a list of implementations of {@link AGear} or null if no gears are available");
        getGears.appendCode("return List.of(); //i.e., no gears currently being used");
        java.add(getGears);
    }

    /**
     * Generates the default code for method getOptions
     *
     */
    private static void addGetOptionsMethod(JavaClass java) {
        final JavaGenericType genWeaverOptionType = new JavaGenericType(new JavaType(WeaverOption.class));
        final JavaType listType = JavaTypeFactory.getListJavaType(genWeaverOptionType);

        final Method getOptions = new Method(listType, "getOptions");

        getOptions.add(Annotation.OVERRIDE);
        getOptions.appendComment(" Returns a list of options specific to this weaver engine." + ln());
        getOptions.addJavaDocTag(JDocTag.RETURN,
                "a list of {@link WeaverOption} representing additional options provided by this weaver");
        getOptions.appendCode("return List.of(); //i.e., no additional options");
        java.add(getOptions);
    }

    private void addBuildLanguageSpecification(JavaClass java) {
        // Build static method
        final Method buildLangSpecStatic = new Method(new JavaType(LanguageSpecification.class),
                "buildLanguageSpecification");

        buildLangSpecStatic.add(Modifier.STATIC);
        buildLangSpecStatic.appendComment(" Builds the language specification, based on the input XML files." + ln());
        buildLangSpecStatic.addJavaDocTag(JDocTag.RETURN,
                "a new {@link LanguageSpecification} instance for this weaver");

        var code = """
                return LanguageSpecification.newInstance(() -> "<BASE_RESOURCE_FOLDER>" + LanguageSpecification.getJoinPointsFilename(),
                 () -> "<BASE_RESOURCE_FOLDER>" + LanguageSpecification.getAttributesFilename(),
                 () -> "<BASE_RESOURCE_FOLDER>" + LanguageSpecification.getActionsFilename());
                """;

        var langSpecFolder = javaGenerator.getLanguageSpecificationDir();
        if (langSpecFolder == null) {
            throw new RuntimeException("Could not determine language specification folder");
        }

        // Assume first level of the folder should be removed
        // TODO: parameterize this

        var resourcesPath = new ArrayList<String>();
        boolean first = true;
        for (var parentName : SpecsIo.getParentNames(langSpecFolder)) {

            // Ignore .
            if (parentName.equals(".")) {
                continue;
            }

            // Ignore first
            if (first) {
                first = false;
                continue;
            }

            // Add others
            resourcesPath.add(parentName);
        }

        var path = resourcesPath.stream().collect(Collectors.joining("/", "", "/"));

        buildLangSpecStatic.appendCode(code.replace("<BASE_RESOURCE_FOLDER>", path));

        java.addImport(LanguageSpecification.class);
        java.add(buildLangSpecStatic);

        final Method buildLangSpec = new Method(new JavaType(LanguageSpecification.class), "buildLangSpecs");

        buildLangSpec.add(Annotation.OVERRIDE);
        buildLangSpec.setPrivacy(Privacy.PROTECTED);
        buildLangSpec.appendComment(" Builds the language specification, based on the input XML files." + ln());
        buildLangSpec.addJavaDocTag(JDocTag.RETURN,
                "a new {@link LanguageSpecification} instance for this weaver");
        buildLangSpec.appendCode("return buildLanguageSpecification();");

        java.add(buildLangSpec);

    }

}
