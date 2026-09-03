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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import java.util.Optional;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.CrtpJavaClass;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.java.utils.ConvertUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.JoinPointClass;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;

import pt.up.fe.specs.util.SpecsLogs;

/**
 * Generate the Join Point abstract class for a given join point type
 */
public class AbstractJoinPointClassGenerator extends GeneratorHelper {

    private final JoinPointClass joinPoint;

    protected AbstractJoinPointClassGenerator(JavaAbstractsGenerator javaGenerator, JoinPointClass joinPoint) {
        super(javaGenerator);
        this.joinPoint = joinPoint;
    }

    /**
     * Generate the Join Point abstract class for the given join point type
     *
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator, JoinPointClass joinPoint) {
        final AbstractJoinPointClassGenerator gen = new AbstractJoinPointClassGenerator(javaGenerator, joinPoint);
        return gen.generate();
    }

    /**
     * Generate the Join Point abstract class for the given join point type.
     * 
     * <p>
     * This generator implements CRTP (Curiously Recurring Template Pattern) for
     * polymorphic 'this' type support. ALL classes get the CRTP type parameter for
     * consistency and future extensibility:
     * </p>
     * 
     * <ul>
     * <li>{@code ANode<Self extends ANode<Self>>} with methods returning
     * {@code Self}</li>
     * <li>{@code ALoop<Self extends ALoop<Self>>} extends {@code AStmt<Self>}</li>
     * </ul>
     */
    @Override
    public JavaClass generate() {
        final String baseClassName = GenConstants.abstractPrefix() + Utils.firstCharToUpper(joinPoint.getName());

        // Determine if this is a leaf class (used for generating 'final' modifiers on
        // some methods)
        boolean isLeafClass = !javaGenerator.getLanguageSpecification().isSuper(joinPoint);

        // Create the CRTP-enabled class (all classes get the CRTP type parameter)
        final CrtpJavaClass javaC = new CrtpJavaClass(baseClassName,
                javaGenerator.getJoinPointClassPackage());
        javaC.add(Modifier.ABSTRACT);
        javaC.appendComment("Auto-Generated class for join point " + baseClassName);
        javaC.appendComment(ln() + "This class is overwritten by the Weaver Generator." + ln() + ln());
        joinPoint.getToolTip().ifPresent(javaC::appendComment);
        javaC.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());

        // All classes use "Self" for ThisType resolution (CRTP pattern)
        JavaType thisType = new JavaType(CrtpJavaClass.SELF_TYPE_PARAMETER);

        addFields(javaC, thisType);
        addActions(javaC, thisType);

        String superTypeName = null;

        // If explicitly extends a join point
        if (joinPoint.getExtendExplicit().isPresent()) {
            var superType = joinPoint.getExtendExplicit().get();

            String superClassName = GenConstants.abstractPrefix() + Utils.firstCharToUpper(superType.getName());

            superTypeName = addSuperMethods(javaC, thisType);

            // Set superclass with CRTP type argument (all classes use Self)
            JavaType superJavaType = new JavaType(superClassName, javaGenerator.getJoinPointClassPackage());
            javaC.setSuperClass(superJavaType);

        } else {
            addConstructor(javaC);

            // Extends the user-editable abstract class (which also has CRTP)
            javaC.setAddTypeArgToSuperClass(javaGenerator.addTypeArgToRootUserSuperClass());
            javaC.setSuperClass(javaGenerator.getSuperClass());
        }

        generateGet_Class(javaC, isLeafClass);

        if (superTypeName != null) {
            GeneratorUtils.generateInstanceOf(javaC, "this." + superTypeName, isLeafClass);
        }

        AttributesEnumGenerator.generate(javaC, joinPoint);

        return javaC;
    }

    private void generateGet_Class(JavaClass javaC, boolean isFinal) {
        final Method clazzMethod = new Method(JavaTypeFactory.getStringType(), GenConstants.getClassName());
        clazzMethod.add(Annotation.OVERRIDE);
        if (isFinal) {
            clazzMethod.add(Modifier.FINAL);
        }
        clazzMethod.appendComment("Returns the join point type of this class");
        clazzMethod.addJavaDocTag(JDocTag.RETURN, "The join point type");
        clazzMethod.appendCode("return \"" + joinPoint.getName() + "\";");
        javaC.add(clazzMethod);
    }

    private void addConstructor(JavaClass abstJPClass) {
        JavaClass weaverClass = javaGenerator.getWeaverImplClass();
        String qualifiedName = weaverClass.getQualifiedName();
        String weaverName = weaverClass.getName();
        abstJPClass.addImport(qualifiedName);
        JavaType weaverType = new JavaType(weaverName);

        var constructor = new Constructor(abstJPClass);
        constructor.addArgument(weaverType, "weaver");
        constructor.appendCode("super(weaver);");
        abstJPClass.add(constructor);
    }

    /**
     * Add fields
     *
     */
    private void addFields(JavaClass javaC, JavaType currentJpType) {

        for (var attribute : joinPoint.getAttributesSelf()) {
            Method generateAttribute = GeneratorUtils.generateAttribute(attribute, javaC, javaGenerator, currentJpType);
            boolean overridesAttribute = joinPoint.getExtend().map(parent -> parent.hasAttribute(attribute.getName()))
                    .orElse(false);
            Method generateAttributeImpl = GeneratorUtils.generateAttributeImpl(generateAttribute, attribute,
                    javaC, javaGenerator, overridesAttribute);
            if (generateAttributeImpl != null) {
                javaC.add(generateAttributeImpl);
            }
            if (overridesAttribute) {
                String parentName = joinPoint.getExtend().map(JoinPointClass::getName).orElse("<unknown>");
                SpecsLogs.warn(String.format(
                        "Attribute '%s' in join point '%s' redeclares inherited attribute from parent '%s'.",
                        attribute.getName(), joinPoint.getName(), parentName));
            }
        }

    }

    /**
     * Adds actions for the join point
     *
     */
    private void addActions(JavaClass javaC, JavaType currentJpType) {

        for (var action : joinPoint.getActionsSelf()) {
            Action preparedAction = alignWithSpecHierarchy(action);
            boolean skipWrapper = GeneratorUtils.normalizeJoinPointBaseAction(
                    preparedAction,
                    javaGenerator,
                    "Action '%s' in join point '" + joinPoint.getName()
                            + "' redeclares inherited action with different return type."
                            + " Using return type '%s'.");
            GeneratorUtils.addActionAndWrapper(
                    javaC,
                    preparedAction,
                    javaGenerator,
                    currentJpType,
                    skipWrapper,
                    "Skipping action '%s' in join point '" + joinPoint.getName()
                            + "' due to duplicate method signature '%s'.",
                    "Skipping action wrapper '%s' in join point '" + joinPoint.getName()
                            + "' due to duplicate method signature.");
        }

    }

    private Action alignWithSpecHierarchy(Action action) {
        return joinPoint.getExtend()
                .flatMap(parent -> findMatchingAction(parent, action))
                .map(superAction -> ensureMatchingParameters(action, superAction))
                .orElse(action);
    }

    private Action ensureMatchingParameters(Action action, Action superAction) {
        if (GeneratorUtils.hasSameSignature(action, superAction)) {
            return action;
        }

        if (!GeneratorUtils.hasSameParameterTypes(action, superAction)) {
            return action;
        }

        // Same parameters but different return type - adopt super type to keep override
        // valid
        SpecsLogs.warn(String.format(
                "Action '%s' in join point '%s' redeclares inherited action with different return type."
                        + " Using return type '%s'.",
                action.getName(), joinPoint.getName(), superAction.getReturnType()));

        action.setType(superAction.getType());
        return action;
    }

    private Optional<Action> findMatchingAction(JoinPointClass parent, Action action) {
        return parent.getAction(action.getName()).stream()
                .filter(candidate -> GeneratorUtils.hasSameParameterTypes(candidate, action))
                .findFirst();
    }

    /**
     * Add code that calls to the super methods
     *
     * @param javaC         the target Java class
     * @param currentJpType the JavaType representing the current join point
     *                      abstract being generated
     */
    private String addSuperMethods(JavaClass javaC, JavaType currentJpType) {

        var superType = joinPoint.getExtendExplicit()
                .orElseThrow(() -> new RuntimeException("Expected join point to explicitly extend another join point"));

        final String superClassName = GenConstants.abstractPrefix() + Utils.firstCharToUpper(superType.getName());
        final String fieldName = GenConstants.abstractPrefix().toLowerCase()
                + Utils.firstCharToUpper(superType.getName());
        final JavaType joinPointType = ConvertUtils.withJoinPointTypeArgument(
            new JavaType(superClassName, javaGenerator.getJoinPointClassPackage()), currentJpType);
        javaC.add(new Field(joinPointType, fieldName, Privacy.PROTECTED));

        final Constructor constructor = new Constructor(javaC);
        constructor.addArgument(joinPointType, fieldName);

        JavaClass weaverClass = javaGenerator.getWeaverImplClass();
        String qualifiedName = weaverClass.getQualifiedName();
        String weaverName = weaverClass.getName();
        javaC.addImport(qualifiedName);
        JavaType weaverType = new JavaType(weaverName);
        constructor.addArgument(weaverType, "weaver");

        if (superType.getExtendExplicit().isPresent()) {
            constructor.appendCode("super(" + fieldName + ", weaver);" + ln());
        } else {
            constructor.appendCode("super(weaver);" + ln());
        }

        constructor.appendCode("this." + fieldName + " = " + fieldName + ";");
        GeneratorUtils.addSuperMethods(javaC, fieldName, javaGenerator, joinPoint, currentJpType);

        // Add global methods for global attributes
        // With CRTP, global attributes that use ThisType must resolve to the current
        // Self type to keep override signatures compatible across the hierarchy.
        var globalAttributes = javaGenerator.getLanguageSpecification().getGlobal().getAttributesSelf().stream()
            .filter(attribute -> !GeneratorUtils.isRuntimeBackedAttribute(attribute))
            .toList();
        JavaType globalJpType = currentJpType;

        GeneratorUtils.addSuperGetters(javaC, fieldName, javaGenerator, globalAttributes, globalJpType);
        GeneratorUtils.addSuperActions(javaGenerator, javaC, superType, fieldName, currentJpType);
        addGetSuperMethod(javaC, joinPointType, fieldName);
        return fieldName;
    }

    /**
     * E.g.: Optional&lt;? extends AExpression&gt;
     *
     */
    private void addGetSuperMethod(JavaClass javaC, JavaType joinPointType, String fieldName) {
        String GET_SUPER_NAME = "getSuper";
        javaC.addImport(Optional.class);
        JavaGenericType wildExtendsType = JavaTypeFactory.getWildExtendsType(joinPointType);
        JavaType optionalOfSuperType = JavaTypeFactory.convert(Optional.class);
        optionalOfSuperType.addGeneric(wildExtendsType);
        final Method getSuperMethod = new Method(optionalOfSuperType, GET_SUPER_NAME);
        getSuperMethod.add(Annotation.OVERRIDE);

        getSuperMethod.appendCodeln("return Optional.of(this." + fieldName + ");");
        javaC.add(getSuperMethod);
    }

}
