/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.artifactsmodel.schema.NewObject;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;

import tdrc.utils.StringUtils;

/**
 * Generate the weaver abstract class, containing the four methods to implement: handlesApplicationFolder, begin, select
 * and close. The getActions method (list of available actions) will be automatically generated. The getRoot method
 * (returns the name of the root join point) will be automatically generated.
 * 
 * @author tiago
 *
 */
public class WeaverAbstractGenerator extends GeneratorHelper {

    protected WeaverAbstractGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     * 
     * @param enums
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        final WeaverAbstractGenerator gen = new WeaverAbstractGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     * 
     * @return
     */
    @Override
    public JavaClass generate() {
        final JavaClass java = generateWeaverAbstractClass();

        /*
         * Super abstract methods that are not implemented here
         * addHandlesApplicationFolderMethod(java); addBeginMethod(java);
         * addSelectMethod(java); addCloseMethod(java);
         */
        addGetActionMethod(java);
        addGetRootMethod(java);
        addGetAllImportableClassesMethod(java);
        addImplementsEventsMethod(java);
        return java;
    }

    private void addImplementsEventsMethod(JavaClass java) {
        final Method getActions = new Method(JavaTypeFactory.getBooleanType(), "implementsEvents");
        // getActions.addModifier(Modifier.ABSTRACT);
        getActions.add(Annotation.OVERRIDE);
        getActions.add(Modifier.FINAL);
        getActions.appendComment("Does the generated code implements events?\n");
        getActions.addJavaDocTag(JDocTag.RETURN, "true if implements events, false otherwise");

        getActions.appendCode("return " + javaGenerator.hasEvents() + ";");
        java.add(getActions);
    }

    /**
     * Generate the Weaver abstract class with name : A + the weaver name, in the given package and
     * 
     * @param weaverName
     * @param weaverPackage
     * @param aJoinPointPackage
     * @return
     */
    private JavaClass generateWeaverAbstractClass() {
        final String weaverName = javaGenerator.getWeaverName();
        final JavaClass java = new JavaClass("A" + weaverName, javaGenerator.getAbstractWeaverPackage());
        java.add(Modifier.ABSTRACT);
        final String text = "Abstract "
                + JavaAbstractsGenerator.getWeaverText(weaverName, javaGenerator.getAUserJoinPointType());
        java.appendComment(text);

        java.appendComment("\nThe implementation of the abstract methods is mandatory!");
        java.add(JDocTag.AUTHOR, "Lara C.");
        // java.addInterface(new JavaType(WeaverEngine.class));
        java.setSuperClass(new JavaType(WeaverEngine.class));
        // java.addImport(ArrayList.class);
        java.addImport(Arrays.class);
        return java;
    }

    /**
     * Generates the method that returns a list of the available actions in the weaver
     * 
     * @param actionModel
     * @param java
     */
    private void addGetActionMethod(JavaClass java) {

        final Method getActions = new Method(JavaTypeFactory.getListStringJavaType(), "getActions");
        // getActions.addModifier(Modifier.ABSTRACT);
        getActions.add(Annotation.OVERRIDE);
        getActions.add(Modifier.FINAL);
        getActions.appendComment("Get the list of available actions in the weaver\n");
        getActions.addJavaDocTag(JDocTag.RETURN, "list with all actions");
        final List<Action> actions = javaGenerator.getLanguageSpecification().getActionModel().getActionsList()
                .getAction();

        /*
         * Use ArrayList getActions.appendCode(
         * "List<String> weaverActions = new ArrayList<String>();\n"); String
         * joinedActions = StringUtils.join(actions, action ->
         * "weaverActions.add(\"" + action.getName() + "\");", "\n");
         * getActions.appendCode(joinedActions); getActions.appendCode(
         * "\nreturn weaverActions;");
         */
        /* Use Arrays.asList() */
        final Function<Action, String> mapper = a -> '"' + a.getName() + '"';
        // String joinedActions = StringUtils.join(actions, mapper, ", ");
        final StringBuffer generatedCode = GeneratorUtils.array2ListCode("String", "weaverActions", actions, mapper);
        getActions.appendCode(generatedCode);
        java.add(getActions);
    }

    private void addGetRootMethod(JavaClass java) {
        final Method getRoot = new Method(JavaTypeFactory.getStringType(), "getRoot");
        getRoot.add(Annotation.OVERRIDE);
        getRoot.add(Modifier.FINAL);
        getRoot.appendComment("Returns the name of the root\n");
        getRoot.addJavaDocTag(JDocTag.RETURN, "the root name");
        final String rootAlias = javaGenerator.getLanguageSpecification().getJpModel().getJoinPointList()
                .getRootAlias();
        getRoot.appendCode("return \"" + rootAlias + "\";");
        java.add(getRoot);
    }

    private void addGetAllImportableClassesMethod(JavaClass java) {
        final JavaType classType = JavaTypeFactory.getClassType();
        JavaTypeFactory.addGenericType(classType, (JavaTypeFactory.getWildCardType()));
        final JavaType listClassType = JavaTypeFactory.getListJavaType(classType);
        final Method getImportableClasses = new Method(listClassType, "getAllImportableClasses");
        getImportableClasses.add(Modifier.FINAL);
        getImportableClasses.add(Annotation.OVERRIDE);
        getImportableClasses.appendComment("Returns a list of classes that may be imported and used in LARA.\n");
        getImportableClasses.addJavaDocTag(JDocTag.RETURN, "a list of importable classes");
        final List<NewObject> entities = javaGenerator.getLanguageSpecification().getArtifacts().getObjects();

        final String entPackage = javaGenerator.getEntitiesPackage() + ".";
        final Function<NewObject, String> mapper = ent -> {
            java.addImport(entPackage + ent.getName());

            return ent.getName() + ".class";
        };
        String joinedClasses = StringUtils.join(entities, mapper, ", ");
        getImportableClasses.appendCode("Class<?>[] defaultClasses = {");
        getImportableClasses.appendCode(joinedClasses);
        getImportableClasses.appendCodeln("};");
        getImportableClasses.appendCodeln("List<Class<?>> otherClasses = this.getImportableClasses();");
        java.addImport(ArrayList.class);
        getImportableClasses
                .appendCodeln("List<Class<?>> allClasses = new ArrayList<>(Arrays.asList(defaultClasses));");
        getImportableClasses.appendCodeln("allClasses.addAll(otherClasses);");
        getImportableClasses.appendCodeln("return allClasses;");
        java.add(getImportableClasses);
    }

    /**
     * Generates the method that defines if the weaver can deal with a folder as the application, or only one file at
     * the time
     * 
     * @param java
     */
    @Deprecated
    protected void addHandlesApplicationFolderMethod(JavaClass java) {
        final Method handleFiles = new Method(JavaTypeFactory.getBooleanType(), "handlesApplicationFolder",
                Modifier.ABSTRACT);
        handleFiles.appendComment(
                "Warns the lara interpreter if the weaver accepts a folder as the application or only one file at a time.\n");
        handleFiles.addJavaDocTag(JDocTag.RETURN,
                "true if the weaver is able to work with several files, false if only works with one file");
        java.add(handleFiles);
    }

    /**
     * Generates the method that starts the weaving process
     * 
     * @param java
     */
    @Deprecated
    protected void addBeginMethod(JavaClass java) {
        final Method begin = new Method(JavaTypeFactory.getBooleanType(), "begin", Modifier.ABSTRACT);
        final JavaType simpleName = new JavaType(File.class);
        begin.addArgument(simpleName, "source");
        begin.addArgument(simpleName, "outputDir");
        final JavaType stringType = JavaTypeFactory.getStringType();
        stringType.setArray(true);
        begin.addArgument(stringType, "args");
        begin.appendComment("Set a file/folder in the weaver if it is valid file/folder type for the weaver.\n");
        begin.addJavaDocTag(JDocTag.PARAM, "source the file with the source code");
        begin.addJavaDocTag(JDocTag.PARAM, "outputDir output directory for the generated file(s)");
        begin.addJavaDocTag(JDocTag.PARAM, "args arguments to start the weaver");
        begin.addJavaDocTag(JDocTag.RETURN, "true if the file type is valid");
        java.add(begin);
    }

    /**
     * Generates the method that selects the root join point
     * 
     * @param java
     */
    @Deprecated
    protected void addSelectMethod(JavaClass java) {
        final Method select = new Method(GenConstants.getJoinPointInterfaceType(), "select", Modifier.ABSTRACT);
        select.appendComment(" Returns the program root to be used by the weaver for the selects");
        select.addJavaDocTag(JDocTag.RETURN, "interface implementation for the join point root/program");
        java.add(select);
    }

    /**
     * Generates the method that closes the weaving process
     * 
     * @param java
     */
    @Deprecated
    protected void addCloseMethod(JavaClass java) {
        final Method close = new Method(JavaTypeFactory.getBooleanType(), "close", Modifier.ABSTRACT);
        close.appendComment(
                " Closes the weaver to the specified output directory location, if the weaver generates new file(s)\n");
        close.addJavaDocTag(JDocTag.RETURN, "if close was successful");
        java.add(close);
    }

}
