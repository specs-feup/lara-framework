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

import org.lara.interpreter.weaver.LaraWeaverEngine;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.language.specification.dsl.types.EnumDef;
import org.lara.language.specification.dsl.types.TypeDef;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

/**
 * Generate the weaver abstract class. The getActions method (list of available
 * actions) will be automatically generated. The getRoot method
 * (returns the name of the root join point) will be automatically generated.
 *
 * @author tiago
 */
public class WeaverAbstractGenerator extends GeneratorHelper {

    protected WeaverAbstractGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes
     * and actions
     *
     * @param enums
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        final WeaverAbstractGenerator gen = new WeaverAbstractGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes
     * and actions
     *
     * @return
     */
    @Override
    public JavaClass generate() {
        final JavaClass java = generateWeaverAbstractClass();

        addGetActionMethod(java);
        addGetRootMethod(java);
        addGetAllImportableClassesMethod(java);

        return java;
    }

    /**
     * Generate the Weaver abstract class with name : A + the weaver name, in the
     * given package and
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

        java.appendComment(ln() + "The implementation of the abstract methods is mandatory!");
        java.add(JDocTag.AUTHOR, "Lara C.");
        // java.addInterface(new JavaType(WeaverEngine.class));
        java.setSuperClass(new JavaType(LaraWeaverEngine.class));
        // java.addImport(ArrayList.class);
        java.addImport(Arrays.class);
        return java;
    }

    /**
     * Generates the method that returns a list of the available actions in the
     * weaver
     *
     * @param actionModel
     * @param java
     */
    private void addGetActionMethod(JavaClass java) {

        final Method getActions = new Method(JavaTypeFactory.getListStringJavaType(), "getActions");
        // getActions.addModifier(Modifier.ABSTRACT);
        getActions.add(Annotation.OVERRIDE);
        getActions.add(Modifier.FINAL);
        getActions.appendComment("Get the list of available actions in the weaver" + ln());
        getActions.addJavaDocTag(JDocTag.RETURN, "list with all actions");

        // Using linked hashset to have deterministic order
        var uniqueActions = new LinkedHashSet<>(javaGenerator.getLanguageSpecification().getAllActions());
        var generatedCode = GeneratorUtils.array2ListCode("String", "weaverActions", uniqueActions,
                a -> '"' + a.getName() + '"');

        getActions.appendCode(generatedCode);
        java.add(getActions);
    }

    private void addGetRootMethod(JavaClass java) {
        final Method getRoot = new Method(JavaTypeFactory.getStringType(), "getRoot");
        getRoot.add(Annotation.OVERRIDE);
        getRoot.add(Modifier.FINAL);
        getRoot.appendComment("Returns the name of the root" + ln());
        getRoot.addJavaDocTag(JDocTag.RETURN, "the root name");
        final String rootAlias = javaGenerator.getLanguageSpecification().getRootAlias();
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
        getImportableClasses.appendComment("Returns a list of classes that may be imported and used in LARA." + ln());
        getImportableClasses.addJavaDocTag(JDocTag.RETURN, "a list of importable classes");

        var entities = javaGenerator.getLanguageSpecification().getTypeDefs().values();
        var enums = javaGenerator.getLanguageSpecification().getEnumDefs().values();

        final String entPackage = javaGenerator.getEntitiesPackage() + ".";
        final String enumPackage = javaGenerator.getEnumsPackage() + ".";
        final Function<TypeDef, String> mapper = ent -> {
            java.addImport(entPackage + ent.getName());
            return ent.getName() + ".class";
        };
        final Function<EnumDef, String> enumMapper = _enum -> {
            java.addImport(enumPackage + _enum.getName());
            return _enum.getName() + ".class";
        };
        List<String> joined = new ArrayList<>();
        entities.stream().map(mapper).forEach(joined::add);
        enums.stream().map(enumMapper).forEach(joined::add);
        String joinedClasses = String.join(", ", joined);
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

}
