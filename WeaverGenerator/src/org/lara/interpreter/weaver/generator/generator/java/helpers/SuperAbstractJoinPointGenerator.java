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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Parameter;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import pt.up.fe.specs.util.SpecsLogs;

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
     */
    private JavaClass generateAbstractJoinPointClass() {

        // JavaClass abstJPClass = new JavaClass(abstractPrefix + globalJPInfo
        final JavaClass abstJPClass = new JavaClass(javaGenerator.getaJoinPointType());
        abstJPClass.add(Modifier.ABSTRACT);
        abstJPClass.setSuperClass(GenConstants.getJoinPointInterfaceType());
        abstJPClass.appendComment("Abstract class containing the global attributes and default action exception.");
        abstJPClass.appendComment(ln() + "This class is overwritten when the weaver generator is executed.");
        abstJPClass.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());
        // abstJPClass.setSuperClass(GenConstants.getJoinPointInterfaceType());
        // abstJPClass.addImport(JoinPoint.class.getCanonicalName());

        generateCompareMethods(abstJPClass);
        generateGlobalJoinPointData(abstJPClass);
        GeneratorUtils.generateInstanceOf(abstJPClass, "super", false);
        addWeaverEngineField(abstJPClass);

        return abstJPClass;
    }

    // @Override
    // public JavaWeaver getWeaverEngine() {
    // return JavaWeaver.getJavaWeaver();
    // }
    private void addWeaverEngineField(JavaClass abstJPClass) {
        JavaClass weaverImplClass = javaGenerator.getWeaverImplClass();
        String qualifiedName = weaverImplClass.getQualifiedName();
        String weaverName = weaverImplClass.getName();
        abstJPClass.addImport(qualifiedName);
        JavaType weavingEngineClass = new JavaType(weaverName);

        // Override getWeavingEngine to return the engine with the specific
        // <qualifiedName> class
        Method getWE = new Method(weavingEngineClass, GenConstants.getWeaverEngineMethodName());
        getWE.add(Annotation.OVERRIDE);
        getWE.appendComment("Returns the Weaving Engine this join point pertains to.");
        getWE.appendCode("return " + weaverName + ".get" + weaverName + "();");
        abstJPClass.add(getWE);
    }

    /**
     * Generate the default methods com comparing two joinpoints: same, compareNodes
     * and getNode()
     *
     * @param abstJPClass target class
     */
    private void generateCompareMethods(JavaClass abstJPClass) {
        generateSameMethod(abstJPClass);
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
     * same join point class as "this" and
     * calls the compareNodes method to compare the join point nodes
     *
     */
    private static void generateSameMethod(JavaClass abstJPClass) {
        final Method same = new Method(JavaTypeFactory.getBooleanType(), "same");
        same.add(Annotation.OVERRIDE);
        same.addArgument(GenConstants.getJoinPointInterfaceType(), "iJoinPoint");
        same.appendCode(
                "if (this.get_class().equals(iJoinPoint.get_class())) {" + ln() + ln()
                        + "        return this.compareNodes(("
                        + abstJPClass.getName() + ") iJoinPoint);" + ln() + "    }" + ln() + "    return false;");
        abstJPClass.add(same);
    }

    /**
     * Generate fields and methods for the attributes and actions global to all join
     * points
     *
     * @param abstJPClass the target join point abstraction class
     */
    private void generateGlobalJoinPointData(JavaClass abstJPClass) {
        // abstJPClass.addImport(List.class.getCanonicalName());

        // Add actions to the abstract join point class
        generateGlobalActionsAsMethods(abstJPClass);

        generateGlobalAttributes(abstJPClass);

    }

    /**
     * Generate the global attributes as fields and/or getter methods
     *
     */
    private void generateGlobalAttributes(JavaClass abstJPClass) {

        var globalAttrs = javaGenerator.getLanguageSpecification().getGlobal().getAttributesSelf();

        if (globalAttrs.isEmpty()) {
            return;
        }

        for (var attr : globalAttrs) {
            final Method method = GeneratorUtils.generateAttribute(attr, abstJPClass, javaGenerator);

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
    private void generateGlobalActionsAsMethods(JavaClass abstJPClass) {

        var actions = javaGenerator.getLanguageSpecification().getGlobal().getActionsSelf();

        if (actions.isEmpty()) {
            return;
        }

        for (var action : actions) {
            ActionPlan plan = prepareGlobalAction(action);

            final Method m = GeneratorUtils.generateActionMethod(plan.action(), javaGenerator);
            abstJPClass.add(m);

            Method cloned = GeneratorUtils.generateActionImplMethod(m, plan.action(),
                    abstJPClass, javaGenerator);
            if (!plan.skipWrapper()) {
                abstJPClass.add(cloned);
            }
        }
    }

    private ActionPlan prepareGlobalAction(Action action) {
        var baseMethod = findJoinPointBaseMethod(action);
        boolean skipWrapper = false;

        if (baseMethod.isPresent()) {
            java.lang.reflect.Method method = baseMethod.get();

            if (!hasSameReturnType(action, method)) {
                String specTypeName = toSpecTypeName(method.getReturnType());
                action.setType(javaGenerator.getLanguageSpecification().getType(specTypeName));
                SpecsLogs.warn(String.format(
                        "Global action '%s' redeclares inherited action with different return type. Using return type '%s'.",
                        action.getName(), specTypeName));
            }

            if (java.lang.reflect.Modifier.isFinal(method.getModifiers())) {
                skipWrapper = true;
            }
        }

        return new ActionPlan(action, skipWrapper);
    }

    private Optional<java.lang.reflect.Method> findJoinPointBaseMethod(Action action) {
        return Arrays.stream(org.lara.interpreter.weaver.interf.JoinPoint.class.getMethods())
                .filter(method -> method.getName().equals(action.getName()))
                .filter(method -> parametersMatch(method, action))
                .findFirst();
    }

    private boolean parametersMatch(java.lang.reflect.Method method, Action action) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<Parameter> params = action.getParameters();
        if (parameterTypes.length != params.size()) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            String expected = normalizeType(toSpecTypeName(parameterTypes[i]));
            String actual = normalizeType(params.get(i).getType());
            if (!expected.equals(actual)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasSameReturnType(Action action, java.lang.reflect.Method method) {
        String expected = normalizeType(toSpecTypeName(method.getReturnType()));
        String actual = normalizeType(action.getReturnType());
        return expected.equals(actual);
    }

    private String toSpecTypeName(Class<?> type) {
        if (type.isArray()) {
            return toSpecTypeName(type.getComponentType()) + "[]";
        }

        if (org.lara.interpreter.weaver.interf.JoinPoint.class.equals(type)) {
            return "joinpoint";
        }

        return type.getSimpleName();
    }

    private String normalizeType(String type) {
        return type.replace("java.lang.", "").trim();
    }

    private record ActionPlan(Action action, boolean skipWrapper) {}

}
