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

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.CrtpJavaClass;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;

/**
 * Generates the base Join Point abstract class, containing the global
 * attributes and actions
 */
public class SuperAbstractJoinPointGenerator extends GeneratorHelper {

    protected SuperAbstractJoinPointGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes
     * and actions
     *
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {

        final SuperAbstractJoinPointGenerator gen = new SuperAbstractJoinPointGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes
     * and actions
     *
     */
    @Override
    public JavaClass generate() {
        return generateAbstractJoinPointClass();
    }

    /**
     * Generate an abstract class for the join points, containing the global
     * attributes and actions. It also generates
     * the code for listing the available attributes and actions.
     * 
     * <p>
     * This class uses CRTP (Curiously Recurring Template Pattern) to enable
     * polymorphic "this" type: {@code AJoinPoint<Self extends AJoinPoint<Self>>}
     * </p>
     *
     */
    private JavaClass generateAbstractJoinPointClass() {
        // Get the base class type (e.g., "AJoinPoint")
        JavaType baseType = javaGenerator.getaJoinPointType();
        String baseClassName = baseType.getName();

        // All classes use CRTP type parameters
        final CrtpJavaClass abstJPClass = new CrtpJavaClass(baseClassName, baseType.getPackage());
        abstJPClass.add(Modifier.ABSTRACT);
        // The base JoinPoint class doesn't have type parameters, so don't add type args
        // to it
        abstJPClass.setAddTypeArgToSuperClass(false);
        abstJPClass.setSuperClass(GenConstants.getJoinPointInterfaceType());
        abstJPClass.appendComment("Abstract class containing the global attributes and default action exception.");
        abstJPClass.appendComment(ln() + "This class is overwritten when the weaver generator is executed.");
        abstJPClass.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());

        addConstructor(abstJPClass);
        generateCompareMethods(abstJPClass, baseClassName);

        // For CRTP, use "Self" as the type for ThisType resolution
        JavaType thisType = new JavaType(CrtpJavaClass.SELF_TYPE_PARAMETER);
        generateGlobalJoinPointData(abstJPClass, thisType);
        GeneratorUtils.generateInstanceOf(abstJPClass, "super", false);

        return abstJPClass;
    }

    private void addConstructor(JavaClass abstJPClass) {
        JavaType weaverType = new JavaType(WeaverEngine.class);
        abstJPClass.addImport(WeaverEngine.class.getCanonicalName());

        var constructor = new Constructor(abstJPClass);
        constructor.addArgument(weaverType, "weaver");
        constructor.appendCode("super(weaver);");
        abstJPClass.add(constructor);
    }

    /**
     * Generate the default methods com comparing two joinpoints: same, compareNodes
     * and getNode()
     *
     * @param abstJPClass   target class
     * @param baseClassName the base class name without type parameters (for
     *                      casting)
     */
    private void generateCompareMethods(JavaClass abstJPClass, String baseClassName) {
        generateSameMethod(abstJPClass, baseClassName);
        final Method compareNodes = GeneratorUtils.generateCompareNodes(javaGenerator.getaJoinPointType());
        abstJPClass.add(compareNodes);
        generateGetNodeMethod(abstJPClass);
    }

    /**
     * Generate default implementation of the getNode() method
     *
     */
    private void generateGetNodeMethod(JavaClass abstJPClass) {
        final Method getNode = new Method(javaGenerator.getNodeJavaType(), "getNode", Modifier.ABSTRACT);
        getNode.appendComment("Returns the tree node reference of this join point."
                + "<br><b>NOTE</b>This method is essentially used to compare two join points");
        getNode.addJavaDocTag(JDocTag.RETURN, "Tree node reference");
        abstJPClass.add(getNode);
    }

    /**
     * Generate the default "same" method, that verifies if the argument has the
     * same join point class as "this" and calls the compareNodes method to compare
     * the join point nodes
     *
     * @param baseClassName the base class name without type parameters (for
     *                      casting)
     */
    private static void generateSameMethod(JavaClass abstJPClass, String baseClassName) {
        final Method same = new Method(JavaTypeFactory.getBooleanType(), "same");
        same.add(Annotation.OVERRIDE);
        same.addArgument(GenConstants.getJoinPointInterfaceType(), "iJoinPoint");
        // Cast to the base class name (e.g., AJoinPoint)
        same.appendCode(
                "if (this.get_class().equals(iJoinPoint.get_class())) {" + ln() + ln()
                        + "        return this.compareNodes(("
                        + baseClassName + ") iJoinPoint);" + ln() + "    }" + ln() + "    return false;");
        abstJPClass.add(same);
    }

    /**
     * Generate fields and methods for the attributes and actions global to all join
     * points
     *
     * @param abstJPClass the target join point abstraction class
     * @param thisType    the type to use for ThisType resolution (Self for CRTP)
     */
    private void generateGlobalJoinPointData(JavaClass abstJPClass, JavaType thisType) {

        // Add actions to the abstract join point class
        generateGlobalActionsAsMethods(abstJPClass, thisType);

        generateGlobalAttributes(abstJPClass, thisType);

    }

    /**
     * Generate the global attributes as fields and/or getter methods
     *
     */
    private void generateGlobalAttributes(JavaClass abstJPClass, JavaType currentJpType) {

        var globalAttrs = javaGenerator.getLanguageSpecification().getGlobal().getAttributesSelf();

        if (globalAttrs.isEmpty()) {
            return;
        }

        for (var attr : globalAttrs) {
            final Method method = GeneratorUtils.generateAttribute(attr, abstJPClass, javaGenerator, currentJpType);

            Method methodImpl = GeneratorUtils.generateAttributeImpl(method, attr,
                    abstJPClass, javaGenerator, false);

            if (methodImpl != null) {
                abstJPClass.add(methodImpl);
            }
        }
    }

    /**
     * List all the actions as methods
     *
     */
    private void generateGlobalActionsAsMethods(JavaClass abstJPClass, JavaType currentJpType) {

        var actions = javaGenerator.getLanguageSpecification().getGlobal().getActionsSelf();

        if (actions.isEmpty()) {
            return;
        }

        for (var action : actions) {
            boolean skipWrapper = GeneratorUtils.normalizeJoinPointBaseAction(
                    action,
                    javaGenerator,
                    "Global action '%s' redeclares inherited action with different return type. Using return type '%s'.");
            GeneratorUtils.addActionAndWrapper(
                    abstJPClass,
                    action,
                    javaGenerator,
                    currentJpType,
                    skipWrapper,
                    "Skipping global action '%s' due to duplicate method signature '%s'.",
                    "Skipping global action wrapper '%s' due to duplicate method signature.");
        }
    }

}
