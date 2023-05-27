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

import java.util.List;
import java.util.stream.Collectors;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.ConvertUtils;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.SelectOp;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.artifactsmodel.schema.DefArgType;
import org.lara.language.specification.artifactsmodel.schema.Global;
import org.lara.language.specification.joinpointmodel.schema.GlobalJoinPoints;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Argument;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;

import pt.up.fe.specs.util.SpecsCollections;
import tdrc.utils.StringUtils;

/**
 * Generates the base Join Point abstract class, containing the global attributes and actions
 */
public class SuperAbstractJoinPointGenerator extends GeneratorHelper {

    protected SuperAbstractJoinPointGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     * 
     * @param javaGenerator
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        final SuperAbstractJoinPointGenerator gen = new SuperAbstractJoinPointGenerator(javaGenerator);
        return gen.generate();
    }

    /**
     * Generate the base Join Point abstract class, containing the global attributes and actions
     * 
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    @Override
    public JavaClass generate() {
        return generateAbstractJoinPointClass();
    }

    /**
     * Generate an abstract class for the join points, containing the global attributes and actions. It also generates
     * the code for listing the available attributes and actions.
     * 
     * @param langSpec
     * @param sanitizedOutPackage
     * @param interfaceName
     * @param enums
     * @return
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

        // Add abstract version of generic select
        abstJPClass.add(GeneratorUtils.generateSelectGeneric(abstJPClass, true));

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

        // Override getWeavingEngine to return the engine with the specific <qualifiedName> class
        Method getWE = new Method(weavingEngineClass, GenConstants.getWeaverEngineMethodName());
        getWE.add(Annotation.OVERRIDE);
        getWE.appendComment("Returns the Weaving Engine this join point pertains to.");
        getWE.appendCode("return " + weaverName + ".get" + weaverName + "();");
        abstJPClass.add(getWE);
        /*
        JavaClass weaverImplClass = javaGenerator.getWeaverImplClass();
        String qualifiedName = weaverImplClass.getQualifiedName();
        String weaverName = weaverImplClass.getName();
        abstJPClass.addImport(qualifiedName);
        abstJPClass.addImport(Preconditions.class);
        // Add a field containing the specific engine type
        JavaType weavingEngineClass = new JavaType(weaverName);
        Field engineField = new Field(weavingEngineClass, "weaverEngine");
        abstJPClass.add(engineField);
        
        // Override getWeavingEngine to return the engine with the specific <qualifiedName> class
        Method getWE = new Method(weavingEngineClass, GenConstants.getWeaverEngineMethodName());
        getWE.add(Annotation.OVERRIDE);
        getWE.appendComment("Returns the Weaving Engine this join point pertains to.");
        getWE.appendCode("return this.weaverEngine;");
        abstJPClass.add(getWE);
        // Override setWeavingEngine to verify if the weaving engine is the expected type (<qualifiedName>) and to store
        // the engine in the new field
        Method setWE = new Method(JavaTypeFactory.getVoidType(), GenConstants.setWeaverEngineMethodName());
        setWE.add(Annotation.OVERRIDE);
        setWE.appendComment("Sets the Weaving Engine this join point pertains to.");
        setWE.addArgument(WeaverEngine.class, "weaverEngine");
        
        //
        // JavaWeaver.class.getCanonicalName());
        setWE.appendCodeln("Preconditions.checkArgument(weaverEngine instanceof " + weaverName + ",");
        setWE.appendCodeln("\t\"The weaver engine class is expected to be " + qualifiedName + "\");");
        setWE.appendCodeln("this.weaverEngine = (" + weaverName + ")weaverEngine;");
        setWE.appendCode("super." + GenConstants.setWeaverEngineMethodName() + "(weaverEngine);");
        abstJPClass.add(setWE);
        */
    }

    /**
     * Generate the default methods com comparing two joinpoints: same, compareNodes and getNode()
     * 
     * @param abstJPClass
     *            target class
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
     * @param abstJPClass
     */
    private void generateGetNodeMethod(JavaClass abstJPClass) {
        final Method getNode = new Method(javaGenerator.getNodeJavaType(), "getNode", Modifier.ABSTRACT);
        getNode.appendComment("Returns the tree node reference of this join point."
                + "<br><b>NOTE</b>This method is essentially used to compare two join points");
        getNode.addJavaDocTag(JDocTag.RETURN, "Tree node reference");
        abstJPClass.add(getNode);
    }

    /**
     * Generate the default "same" method, that verifies if the argument has the same join point class as "this" and
     * calls the compareNodes method to compare the join point nodes
     * 
     * @param abstJPClass
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
     * Generate fields and methods for the attributes and actions global to all join points
     * 
     * @param abstJPClass
     *            the target join point abstraction class
     * 
     * @return
     */
    private void generateGlobalJoinPointData(JavaClass abstJPClass) {
        // abstJPClass.addImport(List.class.getCanonicalName());

        // Add actions to the abstract join point class
        generateGlobalActionsAsMethods(abstJPClass);

        generateGlobalSelects(abstJPClass);

        generateGlobalAttributes(abstJPClass);

    }

    /**
     * Generate the global attributes as fields and/or getter methods
     * 
     * @param abstJPClass
     * @param enums
     */
    private void generateGlobalAttributes(JavaClass abstJPClass) {
        final LanguageSpecification langSpec = javaGenerator.getLanguageSpecification();

        // Add attributes and methods to the abstract join point class
        final Global global = langSpec.getArtifacts().getArtifactsList().getGlobal();
        if (global == null) {
            return;
        }

        List<Attribute> attributes = global.getAttribute();
        if (attributes.isEmpty()) {
            return;
        }
        final Method fillWithAttributes = new Method(JavaTypeFactory.getVoidType(),
                GenConstants.fillWAttrMethodName(),
                Privacy.PROTECTED);
        fillWithAttributes.add(Annotation.OVERRIDE);
        fillWithAttributes.addArgument(JavaTypeFactory.getListStringJavaType(), "attributes");
        fillWithAttributes.appendCode("// Default attributes" + ln());
        fillWithAttributes.appendCode("super.fillWithAttributes(attributes);" + ln() + ln());
        fillWithAttributes.appendCode("//Attributes available for all join points" + ln());
        abstJPClass.add(fillWithAttributes);

        for (final Attribute attr : attributes) {

            final String name = attr.getName();
            fillWithAttributes.appendCode("attributes.add(\"" + name);
            final Method method = GeneratorUtils.generateAttribute(attr, abstJPClass, javaGenerator);
            final List<Argument> arguments = method.getParams();
            if (!arguments.isEmpty()) {
                fillWithAttributes.appendCode("(");

                final String argsList = StringUtils.join(arguments, ", ");
                fillWithAttributes.appendCode(argsList);
                // Argument arg = arguments.get(0);
                // fillWithAttributes.appendCode(arg.getClassType() + " " +
                // arg.getName());
                //
                // for (int i = 1; i < arguments.size(); i++) {
                //
                // arg = arguments.get(i);
                // fillWithAttributes.appendCode("," + arg.getClassType() +
                // " " + arg.getName());
                // }
                fillWithAttributes.appendCode(")");
            }

            fillWithAttributes.appendCode("\");" + ln());

            Method methodImpl = GeneratorUtils.generateAttributeImpl(method, attr,
                    abstJPClass, javaGenerator);

            GeneratorUtils.generateDefMethods(attr, method.getReturnType(), abstJPClass, javaGenerator);
            abstJPClass.add(methodImpl);

        }

        // Then add default attributes -> already in JoinPoint class!
        // addDefaultAttributes(abstJPClass, fillWithAttributes);
    }

    /**
     * Generate the global selects as abstract methods
     * 
     * @param actionModel
     * @param abstJPClass
     */
    private void generateGlobalSelects(JavaClass abstJPClass) {

        final GlobalJoinPoints globalSelects = javaGenerator.getLanguageSpecification().getJpModel()
                .getJoinPointList()
                .getGlobal();
        if (globalSelects == null) {
            return;
        }
        List<Select> selects = globalSelects.getSelect();
        if (selects.isEmpty()) {
            return;
        }

        final Method fillWithSelects = new Method(JavaTypeFactory.getVoidType(), GenConstants.fillWSelMethodName(),
                Privacy.PROTECTED);
        abstJPClass.add(fillWithSelects);
        fillWithSelects.add(Annotation.OVERRIDE);
        fillWithSelects.addArgument(JavaTypeFactory.getListStringJavaType(), "selects");
        fillWithSelects.appendCode("//Selects available for all join points" + ln());

        // abstJPClass.addImport(List.class.getCanonicalName());
        for (final Select select : selects) {

            // final Method selectMethod = GeneratorUtils.generateSelectMethod(select,
            // javaGenerator.getJoinPointClassPackage(),
            // true);
            final Method selectMethod = GeneratorUtils.generateSelectMethodGeneric(select,
                    javaGenerator.getJoinPointClassPackage());
            abstJPClass.add(selectMethod);
            abstJPClass.addImport(SelectOp.class);

            fillWithSelects.appendCode("selects.add(\"" + select + "\");" + ln());
        }

    }

    /**
     * List all the actions as methods
     * 
     * @param actionModel
     * @param abstJPClass
     */
    private void generateGlobalActionsAsMethods(JavaClass abstJPClass) {

        if (javaGenerator.hasDefs()) {
            List<Attribute> attributes = javaGenerator.getLanguageSpecification().getArtifacts().getArtifactsList()
                    .getGlobal().getAttribute().stream().filter(a -> !a.getDef().isEmpty())
                    .collect(Collectors.toList());
            GeneratorUtils.createDefImpl(abstJPClass, false, attributes, javaGenerator);
            // generateDefImpl(abstJPClass);
        }
        List<Action> actionsForAll = javaGenerator.getLanguageSpecification().getActionModel().getActionsForAll();

        if (actionsForAll.isEmpty()) {
            return;
        }
        final Method fillWithActions = new Method(JavaTypeFactory.getVoidType(), GenConstants.fillWActMethodName(),
                Privacy.PROTECTED);
        fillWithActions.add(Annotation.OVERRIDE);
        abstJPClass.add(fillWithActions);
        abstJPClass.addImport(List.class);
        final JavaType listStringType = JavaTypeFactory.getListStringJavaType();
        fillWithActions.addArgument(listStringType, "actions");
        for (final Action action : actionsForAll) {
            final Method m = GeneratorUtils.generateActionMethod(action, javaGenerator);
            abstJPClass.add(m);

            fillWithActions.appendCode("actions.add(\"" + action.getName() + "(");
            // Function<Argument, String> mapper = arg -> arg.getClassType() + "
            // " + arg.getName();
            String joinedArgs = StringUtils.join(m.getParams(), ", ");
            fillWithActions.appendCode(joinedArgs);
            fillWithActions.appendCode(")\");" + ln());

            Method cloned = GeneratorUtils.generateActionImplMethod(m, action.getName(), action.getReturn(),
                    abstJPClass, javaGenerator.hasEvents());
            abstJPClass.add(cloned);
        }
        // addDefaultActions(abstJPClass, fillWithActions);
    }

    @Deprecated
    void generateDefImpl(JavaClass abstJPClass) {
        List<Attribute> attributes = javaGenerator.getLanguageSpecification().getArtifacts().getArtifactsList()
                .getGlobal().getAttribute();
        Method defMethod = new Method(JavaTypeFactory.getVoidType(), GenConstants.withImpl("def"));
        defMethod.add(Annotation.OVERRIDE);
        defMethod.addArgument(String.class, "attribute");
        defMethod.addArgument(Object.class, "value");
        defMethod.appendCodeln("switch(attribute){");

        for (Attribute attribute : attributes) {
            List<DefArgType> def = attribute.getDef();
            if (def.isEmpty()) {
                continue;
            }
            JavaType returnType = ConvertUtils.getAttributeConvertedType(attribute.getType(), javaGenerator);
            defMethod.appendCodeln("case \"" + attribute.getName() + "\": {");
            List<String> processedTypes = SpecsCollections.newArrayList();
            for (DefArgType defType : def) {
                String type = defType.getType();
                if (processedTypes.contains(type)) {
                    continue;
                }
                JavaType defJavaType;
                if (type == null) {
                    defJavaType = returnType.clone();
                } else {
                    defJavaType = ConvertUtils.getAttributeConvertedType(type, javaGenerator);
                }
                abstJPClass.addImport(defJavaType);
                defMethod.appendCodeln("\tif(value instanceof " + defJavaType.getSimpleType() + "){");
                defMethod.appendCode("\t\tthis.");
                defMethod.appendCode(GenConstants.getDefAttributeImplName(attribute.getName()));
                defMethod.appendCode("((");
                defMethod.appendCode(defJavaType.getSimpleType());
                defMethod.appendCodeln(")value);");
                defMethod.appendCodeln("\t\treturn;");
                defMethod.appendCodeln("\t}");
                processedTypes.add(type);
            }
            defMethod.appendCodeln("\t" +
                    GeneratorUtils.UnsupDefTypeExceptionCode(attribute.getName(), "value.getClass()"));
            defMethod.appendCodeln("}");
        }

        defMethod.appendCode("default: ");
        defMethod.appendCodeln(
                GeneratorUtils.UnsupDefExceptionCode("attribute"));
        defMethod.appendCodeln("}");
        abstJPClass.add(defMethod);
    }

    /**
     * Add the default attributes to the abstract join point representation <br>
     * Already in JoinPoint class!
     * 
     * @param abstJPClass
     */
    // private static void addDefaultAttributes(JavaClass abstJPClass, Method fillWithAttributes) {
    // fillWithAttributes.appendCode("attributes.add(\"selects\");\n");
    // fillWithAttributes.appendCode("attributes.add(\"attributes\");\n");
    // fillWithAttributes.appendCode("attributes.add(\"actions\");\n");
    //
    // }

    /**
     * Add the default actions to the abstract join point representation <br>
     * already in JoinPoint class!
     * 
     * @param abstJPClass
     */
    // private static void addDefaultActions(JavaClass abstJPClass, Method fillWithActions) {
    // final JavaType voidType = JavaTypeFactory.getVoidType();
    // final Method insertMethod = new Method(voidType, "insert");
    // final JavaType stringType = JavaTypeFactory.getStringType();
    // insertMethod.addArgument(stringType, "position");
    // insertMethod.addArgument(stringType, "code");
    // insertMethod.appendCode(GeneratorUtils.UnsupActionExceptionCode("insert"));
    // abstJPClass.add(insertMethod);
    //
    // Method cloned = GeneratorUtils.generateActionImplMethod(insertMethod, "insert", "void", abstJPClass);
    // abstJPClass.add(cloned);
    //
    // final Method defMethod = new Method(voidType, "def");
    // defMethod.addArgument(stringType, "attribute");
    // defMethod.addArgument(JavaTypeFactory.getObjectType(), "value");
    // defMethod.appendCode(GeneratorUtils.UnsupActionExceptionCode("def"));
    // abstJPClass.add(defMethod);
    //
    // cloned = GeneratorUtils.generateActionImplMethod(defMethod, "def", "void", abstJPClass);
    // abstJPClass.add(cloned);
    //
    // fillWithActions.appendCode("actions.add(\"insert(String position, String code)\");\r\n");
    // fillWithActions.appendCode("actions.add(\"def(String attribute, Object value)\");\r\n");
    // }

}
