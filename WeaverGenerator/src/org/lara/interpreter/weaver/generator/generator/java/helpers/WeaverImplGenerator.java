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
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import java.io.File;
import java.util.List;

/**
 * Generate the weaver abstract class, containing the four methods to implement: handlesApplicationFolder, begin, select
 * and close. The getActions method (list of available actions) will be automatically generated. The getRoot method
 * (returns the name of the root join point) will be automatically generated.
 *
 * @author tiago
 */
public class WeaverImplGenerator extends GeneratorHelper {

    protected WeaverImplGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     *
     * @param enums
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        final WeaverImplGenerator gen = new WeaverImplGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     *
     * @return
     */
    @Override
    public JavaClass generate() {
        final JavaClass java = generateWeaverClass();

        addHandlesApplicationFolderMethod(java);
        addBeginMethod(java);
        addCloseMethod(java);
        addGetGearsMethod(java);
        addGetWeaver(java);

        return java;
    }

    private void addGetWeaver(JavaClass java) {
        String weaverName = java.getName();
        JavaType weavingEngineClass = new JavaType(weaverName);

        Method getWE = new Method(weavingEngineClass, "get" + weaverName);
        getWE.add(Annotation.OVERRIDE);
        getWE.appendComment("Returns Weaving Engine as a " + weaverName);
        getWE.appendCode("return (" + weaverName + ")getWeaverStatic();");
        java.add(getWE);
    }

    /**
     * Generate the Weaver abstract class with name : A + the weaver name, in the given package and
     *
     * @param weaverName
     * @param weaverPackage
     * @param aJoinPointPackage
     * @return JavaClass java = new JavaClass(weaverName, outPackage); String text = getWeaverText(weaverName,
     * outPackage); java.appendComment(text); java.add(JDocTag.AUTHOR, "Lara C."); java.setSuperClass(new
     * JavaType(abstractWeaver.getName(), abstractWeaver.getClassPackage()));
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
     * Generates the method that defines if the weaver can deal with a folder as the application, or only one file at
     * the time
     *
     * @param java
     */
    private static void addHandlesApplicationFolderMethod(JavaClass java) {
        final Method handleFiles = new Method(JavaTypeFactory.getBooleanType(), "handlesApplicationFolder");
        handleFiles
                .appendComment(
                        "Warns the lara interpreter if the weaver accepts a folder as the application or only one file at a time."
                                + ln());
        handleFiles.addJavaDocTag(JDocTag.RETURN,
                "true if the weaver is able to work with several files, false if only works with one file");
        handleFiles.appendCode("//Can the weaver handle an application folder?" + ln());
        handleFiles.appendCode("return true/false;");
        java.add(handleFiles);
    }

    /**
     * Generates the method that starts the weaving process
     *
     * @param java
     */
    private static void addBeginMethod(JavaClass java) {
        final Method begin = new Method(JavaTypeFactory.getBooleanType(), "begin");
        final JavaType fileType = new JavaType(File.class);
        final JavaType listType = JavaTypeFactory.convert(List.class);
        final JavaType dataStoreType = JavaTypeFactory.convert(DataStore.class);

        JavaTypeFactory.addGenericType(listType, fileType);

        begin.addArgument(listType, "sources");
        begin.addArgument(fileType, "outputDir");
        final JavaType stringType = JavaTypeFactory.getStringType();
        stringType.setArray(true);
        begin.addArgument(dataStoreType, "args");
        begin.appendComment("Set a file/folder in the weaver if it is valid file/folder type for the weaver." + ln());
        begin.addJavaDocTag(JDocTag.PARAM, "source the file with the source code");
        begin.addJavaDocTag(JDocTag.PARAM, "outputDir output directory for the generated file(s)");
        begin.addJavaDocTag(JDocTag.PARAM, "args arguments to start the weaver");
        begin.addJavaDocTag(JDocTag.RETURN, "true if the file type is valid");
        begin.appendCode("//Initialize weaver with the input file/folder" + ln());
        begin.appendCode("throw new UnsupportedOperationException(\"Method begin for " + java.getName()
                + " is not yet implemented\");");
        java.add(begin);
    }

    /**
     * Generates the method that closes the weaving process
     *
     * @param java
     */
    private static void addCloseMethod(JavaClass java) {
        final Method close = new Method(JavaTypeFactory.getBooleanType(), "close");
        close.appendComment(
                " Closes the weaver to the specified output directory location, if the weaver generates new file(s)"
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
     * @param java
     */
    private static void addGetGearsMethod(JavaClass java) {
        final JavaGenericType genGearType = new JavaGenericType(new JavaType(AGear.class));
        final JavaType listType = JavaTypeFactory.getListJavaType(genGearType);
        // String listGearType = List.class.getSimpleName() + "<";
        // listGearType += AGear.class.getSimpleName() + ">";
        final Method getGears = new Method(listType, "getGears");
        // close.addModifier(Modifier.ABSTRACT);
        getGears.appendComment(" Returns a list of Gears associated to this weaver engine" + ln());
        getGears.addJavaDocTag(JDocTag.RETURN,
                "a list of implementations of {@link AGear} or null if no gears are available");
        getGears.appendCode("return null; //i.e., no gears currently being used");
        java.add(getGears);
    }

}
