/**
 * Copyright 2013 SPeCS Research Group.
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

package org.lara.interpreter.weaver.generator.generator.java.utils;

import org.lara.interpreter.exception.ActionException;
import org.lara.interpreter.exception.AttributeException;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.NamedEnum;
import org.lara.interpreter.weaver.interf.SelectOp;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.actionsmodel.schema.Parameter;
import org.lara.language.specification.artifactsmodel.schema.Artifact;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.artifactsmodel.schema.DefArgType;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.joinpointmodel.constructor.JoinPointModelConstructor;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.classtypes.JavaEnum;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.*;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;
import pt.up.fe.specs.util.SpecsCollections;
import pt.up.fe.specs.util.SpecsIo;
import tdrc.utils.Pair;
import tdrc.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class GeneratorUtils {

    private static String ln() {
        return SpecsIo.getNewline();
    }

    /**
     * @param javaC
     * @param langSpec
     * @param joinPoint
     * @param superName
     * @param isFinal
     * @deprecated
     */
    public static void createListOfAvailableAttributes(JavaClass javaC, LanguageSpecification langSpec,
                                                       JoinPointType joinPoint, String superName, boolean isFinal) {
        // javaC.addImport(List.class.getCanonicalName());
        final String fillAttributesName = GenConstants.fillWAttrMethodName();
        final Method listSelects = new Method(JavaTypeFactory.getVoidType(), fillAttributesName, Privacy.PROTECTED);
        listSelects.add(Annotation.OVERRIDE);
        if (isFinal) {
            listSelects.add(Modifier.FINAL);
        }
        final JavaType listStringType = JavaTypeFactory.getListStringJavaType();
        listSelects.addArgument(listStringType, "attributes");

        if (superName != null) {

            listSelects.appendCode("this." + superName + "." + fillAttributesName + "(attributes);" + ln());
        } else {
            listSelects.appendCode("super." + fillAttributesName + "(attributes);" + ln());
        }

        final Artifact artifact = langSpec.getArtifacts().getArtifact(joinPoint.getClazz());
        if (artifact != null) {
            for (final Attribute attribute : artifact.getAttribute()) {

                listSelects.appendCode("attributes.add(\"" + attribute.getName() + "\");" + ln());
            }
        }

        javaC.add(listSelects);
    }

    public static void createListOfAvailableAttributes(JavaClass javaC, LanguageSpecification langSpec,
                                                       JoinPointType joinPoint, JoinPointClass joinPointV2, String superName, boolean isFinal) {

        final String fillAttributesName = GenConstants.fillWAttrMethodName();
        final Method listSelects = new Method(JavaTypeFactory.getVoidType(), fillAttributesName, Privacy.PROTECTED);
        listSelects.add(Annotation.OVERRIDE);
        if (isFinal) {
            listSelects.add(Modifier.FINAL);
        }
        final JavaType listStringType = JavaTypeFactory.getListStringJavaType();
        listSelects.addArgument(listStringType, "attributes");

        if (superName != null) {

            listSelects.appendCode("this." + superName + "." + fillAttributesName + "(attributes);" + ln());
        } else {
            listSelects.appendCode("super." + fillAttributesName + "(attributes);" + ln());
        }

        for (var attribute : joinPointV2.getAttributesSelf()) {
            listSelects.appendCode("attributes.add(\"" + attribute.getName() + "\");" + ln());
        }

        javaC.add(listSelects);
    }

    public static void createListOfAvailableSelects(JavaClass javaC, JoinPointType joinPoint, String superName,
                                                    boolean isFinal) {
        // javaC.addImport(List.class.getCanonicalName());
        final String fillSelectsName = GenConstants.fillWSelMethodName();
        final Method listSelects = new Method(JavaTypeFactory.getVoidType(), fillSelectsName, Privacy.PROTECTED);
        listSelects.add(Annotation.OVERRIDE);
        if (isFinal) {
            listSelects.add(Modifier.FINAL);
        }
        final JavaType listStringType = JavaTypeFactory.getListStringJavaType();
        listSelects.addArgument(listStringType, "selects");

        if (superName != null) {

            listSelects.appendCode("this." + superName + "." + fillSelectsName + "(selects);" + ln());
        } else {

            listSelects.appendCode("super." + fillSelectsName + "(selects);" + ln());
        }

        for (final Select sel : joinPoint.getSelect()) {

            listSelects.appendCode("selects.add(\"" + sel.getAlias() + "\");" + ln());
        }

        javaC.add(listSelects);
    }


    public static void createSelectByName(JavaClass javaC, JoinPointClass joinPointV2, String superName,
                                          boolean isFinal) {

        final String selectMethodName = GenConstants.getSelectByNameMethodName();

        JavaType joinPointType = JavaTypeFactory.convert(JoinPoint.class);
        JavaType joinPointListType = JavaTypeFactory.convert(List.class);
        JavaGenericType joinPointWildCardType = JavaTypeFactory.getWildExtendsType(joinPointType);
        joinPointListType.addGeneric(joinPointWildCardType);

        final Method selectByName = new Method(joinPointListType, selectMethodName, Privacy.PUBLIC);
        selectByName.add(Annotation.OVERRIDE);
        if (isFinal) {
            selectByName.add(Modifier.FINAL);
        }
        selectByName.addArgument(JavaTypeFactory.getStringType(), "selectName");

        selectByName.appendCodeln(joinPointListType.getSimpleType() + " joinPointList;");
        selectByName.appendCodeln("switch(selectName) {");

        for (var sel : joinPointV2.getSelects()) {
            var alias = sel.getSelectName();
            selectByName.appendCodeln("\tcase \"" + alias + "\": ");
            selectByName.appendCodeln("\t\tjoinPointList = select" + StringUtils.firstCharToUpper(alias) + "();");
            selectByName.appendCodeln("\t\tbreak;");
        }

        selectByName.appendCode("\tdefault:" + ln() + "\t\tjoinPointList = ");

        String superCall = superName != null ? ("this." + superName) : "super"; // if super exists use the "super" field

        selectByName
                .appendCodeln(superCall + "." + selectMethodName + "(selectName);" + ln() + "\t\tbreak;" + ln() + "}");
        // selectByName.appendCodeln("if(joinPointList != null && !joinPointList.isEmpty()){");
        // selectByName.appendCodeln("\tjoinPointList.forEach(jp -> jp.setWeaverEngine(this));");
        // selectByName.appendCodeln("}");
        selectByName.appendCodeln("return joinPointList;");
        javaC.add(selectByName);

    }

    public static void createListOfAvailableActions(JavaClass javaC, JoinPointType joinPoint, String superName,
                                                    LanguageSpecification langSpec, boolean isFinal) {
        // javaC.addImport(List.class.getCanonicalName());
        final String fillActionsName = GenConstants.fillWActMethodName();

        final Method listActions = new Method(JavaTypeFactory.getVoidType(), fillActionsName, Privacy.PROTECTED);
        listActions.add(Annotation.OVERRIDE);
        if (isFinal) {
            listActions.add(Modifier.FINAL);
        }
        final JavaType listStringType = JavaTypeFactory.getListStringJavaType();
        listActions.addArgument(listStringType, "actions");

        if (superName != null) {

            listActions.appendCode("this." + superName);
        } else {

            listActions.appendCode("super");
        }
        listActions.appendCode("." + fillActionsName + "(actions);" + ln());

        for (final Action act : langSpec.getActionModel().getJoinPointOwnActions(joinPoint.getClazz())) {

            listActions.appendCode("actions.add(\"" + act.getReturn() + " " + act.getName() + "(");
            final String joinedParams = StringUtils.join(act.getParameter(), Parameter::getType, ", ");
            listActions.appendCode(joinedParams);
            listActions.appendCode(")\");" + ln());
        }

        javaC.add(listActions);
    }

    // public static void addSuperMethods(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
    // JoinPointType current) {
    // addSuperMethods(javaC, fieldName, generator, current, false);
    // }

    /**
     * Add methods of the super join point to the java class
     *
     * @param javaC
     * @param fieldName
     * @param langSpec
     * @param current
     */
    public static void addSuperMethods(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                       JoinPointType current) {
        // public static void addSuperMethods(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
        // JoinPointType current, boolean calledOnBaseJp) {
        final String parentType = JoinPointModelConstructor.getJoinPointClass(current.getExtends());
        if (parentType == null || parentType.equals(current.getClazz())) {
            return;
        }
        // if (parentType == null || calledOnBaseJp) {
        // return;
        // }
        //
        // var newCalledOnBaseJp = parentType.equals(current.getClazz());

        final JoinPointType parent = generator.getLanguageSpecification().getJpModel().getJoinPoint(parentType);
        addSuperGetters(javaC, fieldName, generator, parent);
        addSuperSelect(javaC, fieldName, generator, parent);
        // addSuperMethods(javaC, fieldName, generator, parent, newCalledOnBaseJp);
        addSuperMethods(javaC, fieldName, generator, parent);
        addSuperDefs(javaC, fieldName, generator, parent);
    }

    /**
     * @param javaC
     * @param fieldName
     * @param parent
     */
    public static void addSuperSelect(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                      JoinPointType parent) {
        for (final Select sel : parent.getSelect()) {

            final Method selectMethod = generateSelectMethod(sel, generator.getJoinPointClassPackage(), false);
            selectMethod.add(Annotation.OVERRIDE);
            selectMethod.appendCode("return this." + fieldName + "." + selectMethod.getName() + "();");
            javaC.add(selectMethod);
        }
    }

    /**
     * @param javaC
     * @param fieldName
     * @param parent
     */
    public static void addSuperToString(JavaClass javaC, String fieldName) {

        final Method toStringMethod = new Method(JavaTypeFactory.getStringType(), "toString");
        toStringMethod.add(Annotation.OVERRIDE);
        toStringMethod.appendCode("return this." + fieldName + "." + toStringMethod.getName() + "();");
        javaC.add(toStringMethod);
    }

    public static void addSuperGetters(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                       JoinPointType parent) {

        final Artifact artifact = generator.getLanguageSpecification().getArtifacts().getArtifact(parent.getClazz());

        if (artifact == null) {
            return;
        }

        addSuperGetters(javaC, fieldName, generator, artifact.getAttribute());
    }

    public static void addSuperGetters(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                       Collection<Attribute> attributes) {

        // System.out.println("JP: " + javaC.getName());
        for (final Attribute attribute : attributes) {
            // System.out.println("ATTR:" + attribute.getName());
            String attrClassStr = attribute.getType().trim();

            if (attrClassStr.startsWith("{")) { // then it is an enumerator
                // attrClassStr = extractEnumName(attribute.getName());
                attrClassStr = String.class.getSimpleName();
            }

            // if (ObjectOfPrimitives.contains(attrClassStr))
            // attrClassStr = ObjectOfPrimitives.getPrimitive(attrClassStr);

            String name = attribute.getName();
            // JavaType type = ConvertUtils.getConvertedType(attrClassStr, generator);

            JavaType type = ConvertUtils.getAttributeConvertedType(attrClassStr, generator);
            // type = JavaTypeFactory.primitiveUnwrap(type);
            if (type.isArray()) {
                name += GenConstants.getArrayMethodSufix();
            }
            String sanitizedName = StringUtils.getSanitizedName(name);
            if (generator.hasImplMode() && !type.isArray()) {
                name += GenConstants.getImplementationSufix();
            }
            final Method getter = createSuperGetter(sanitizedName, name, type, fieldName, attribute.getParameter(),
                    generator);
            getter.add(Annotation.OVERRIDE);
            javaC.add(getter);
        }

    }

    /**
     * Create the action methods calling the super class method
     *
     * @param javaGenerator
     * @param javaC
     * @param fieldName
     */
    public static void addSuperActions(JavaAbstractsGenerator javaGenerator, JavaClass javaC, String joinPointSuperType,
                                       String fieldName) {
        final List<Action> joinPointOwnActions = javaGenerator.getLanguageSpecification().getActionModel()
                .getJoinPointActions(joinPointSuperType);

        // getJoinPointOwnActions(joinPointSuperType); // These two lines makes
        // the same thing as the code above
        // joinPointOwnActions.addAll(langSpec.getActionModel().getActionsForAll());
        for (final Action action : joinPointOwnActions) {

            if (javaGenerator.hasDefs() && action.getName().equals("def")) {
                continue;
            }

            final Method m = generateActionMethod(action, javaGenerator);
            m.setName(m.getName() + GenConstants.getImplementationSufix());
            m.clearCode();
            m.add(Annotation.OVERRIDE);
            if (!action.getReturn().equals("void")) {
                m.appendCode("return ");
            }
            m.appendCode("this." + fieldName + "." + m.getName() + "(");
            final String joinedParameters = StringUtils.join(m.getParams(), Argument::getName, ", ");
            m.appendCode(joinedParameters);
            // if (!parameters.isEmpty()) {
            //
            // for (int i = 0; i < parameters.size() - 1; i++) {
            //
            // Parameter param = parameters.get(i);
            // m.appendCode(param.getName() + ", ");
            // }
            //
            // Parameter param = parameters.get(parameters.size() - 1);
            // m.appendCode(param.getName());
            // }
            m.appendCode(");");

            javaC.add(m);
        }

    }

    /**
     * @param attribute
     * @return
     */
    public static String extractEnumName(String jpName, String attribute) {
        return Utils.firstCharToUpper(jpName) + Utils.firstCharToUpper(attribute) + "Enum";
    }

    /**
     * Create Methods based on the fields
     *
     * @param originalName
     * @param abstractGetters
     * @param attributes
     * @return
     */
    public static Pair<Method, Method> createGetterAndSetter(Field field, String originalName,
                                                             boolean abstractGetters) {
        final String attr = field.getName();
        final JavaType attrClassType = field.getType();
        // attrClassType = JavaTypeFactory.primitiveUnwrap(attrClassType);
        // if (ObjectOfPrimitives.contains(getAttrType))
        // getAttrType = ObjectOfPrimitives.getPrimitive(getAttrType);

        final Method getAttribute = createGetter(attr, originalName, attrClassType, abstractGetters);
        final Method setAttribute = createSetter(attr, originalName, attrClassType);

        return new Pair<>(getAttribute, setAttribute);
    }

    /**
     * @param attr
     * @param attrClassStr
     * @param attrClassStr2
     * @return
     */
    private static Method createSetter(String attr, String originalName, JavaType attrClassStr) {
        // Generate a get for the attribute
        final String setName = "set" + Utils.firstCharToUpper(originalName);
        final Method setAttribute = new Method(JavaTypeFactory.getVoidType(), setName);
        // setAttribute.addModifier(Modifier.ABSTRACT);
        setAttribute.appendComment("Set value on attribute " + attr);
        setAttribute.addJavaDocTag(JDocTag.PARAM, attr);
        setAttribute.addArgument(attrClassStr, attr);
        setAttribute.appendCode("this." + attr + " = " + attr + ";");
        return setAttribute;
    }

    /**
     * @param attr
     * @param getAttrType
     * @param getAttrType
     * @param abstractGetters
     * @return
     */
    private static Method createGetter(String attr, String originalName, JavaType getAttrType,
                                       boolean abstractGetters) {
        final String getName = "get" + Utils.firstCharToUpper(originalName);
        // final JavaType unwrappedType = JavaTypeFactory.primitiveUnwrap(getAttrType);

        // final Method getAttribute = new Method(unwrappedType, getName);
        final Method getAttribute = new Method(getAttrType, getName);

        getAttribute.appendComment("Get value on attribute " + attr);
        getAttribute.addJavaDocTag(JDocTag.RETURN, "the attribute's value");

        if (abstractGetters) {
            getAttribute.add(Modifier.ABSTRACT);
        } else {
            getAttribute.appendCode("return this." + attr + ";");
        }

        return getAttribute;
    }

    /**
     * @param attr
     * @param getAttrType
     * @param list
     * @return
     */
    private static Method createSuperGetter(String attr, String originalName, JavaType getAttrType, String superField,
                                            List<org.lara.language.specification.artifactsmodel.schema.Parameter> list,
                                            JavaAbstractsGenerator generator) {

        if (list != null && !list.isEmpty()) {
            final Method getAttribute = new Method(getAttrType, originalName);
            // getAttribute.addModifier(Modifier.ABSTRACT);
            getAttribute.appendComment("Get value on attribute " + attr);
            getAttribute.addJavaDocTag(JDocTag.RETURN, "the attribute's value");
            getAttribute.appendCode("return this." + superField + "." + originalName + "(");
            for (org.lara.language.specification.artifactsmodel.schema.Parameter parameter : list) {

                JavaType type = ConvertUtils.getConvertedType(parameter.getType(), generator);
                getAttribute.addArgument(type, parameter.getName());
                getAttribute.appendCode(parameter.getName());
            }
            getAttribute.appendCode(");");

            return getAttribute;
        }
        final String getName = "get" + Utils.firstCharToUpper(originalName);
        final Method getAttribute = new Method(getAttrType, getName);
        // getAttribute.addModifier(Modifier.ABSTRACT);
        getAttribute.appendComment("Get value on attribute " + attr);
        getAttribute.addJavaDocTag(JDocTag.RETURN, "the attribute's value");
        getAttribute.appendCode("return this." + superField + "." + getName + "();");

        return getAttribute;
    }

    public static void encapsulateArrayAttribute(JavaClass javaC, Method getter) {
        final Method newGetter = getter.clone();
        newGetter.setName(newGetter.getName() + GenConstants.getArrayMethodSufix());
        final JavaType returnType = newGetter.getReturnType();
        final String baseType = returnType.getName();
        // getter.setReturnType(new JavaType(Bindings.class));
        getter.setReturnType(new JavaType(Object.class));
        // javaC.addImport(Converter.class); // No longer needed?
        getter.clearCode();
        getter.appendCode(returnType.getSimpleType());
        final String valueName = StringUtils.firstCharToLower(baseType) + GenConstants.getArrayMethodSufix();
        getter.appendCode(" " + valueName + "0 = ");
        getter.appendCode(newGetter.getName() + "(");
        final List<Argument> arguments = getter.getParams();
        final String argsList = StringUtils.join(arguments, Argument::getName, ", ");
        getter.appendCode(argsList);
        getter.appendCode(");" + ln());
        getter.appendCode(encapsulateBasedOnDimension(baseType, valueName, returnType.getArrayDimension(), 0));
        getter.appendCode("return " + GenConstants.getNativeArrayVarName() + "0;");
        getter.remove(Modifier.ABSTRACT);
        javaC.add(newGetter);
    }

    public static String encapsulateBasedOnDimension(String baseType, String valueName, int dimension, int position) {
        final String spaceStr = StringUtils.repeat("\t", position);
        final String nativeArrayVarName = GenConstants.getNativeArrayVarName();
        if (dimension == 1) {
            // return spaceStr + "Bindings " + nativeArrayVarName + position + " = Converter.toNativeArray(" + valueName
            // + position + ");\n";
            // return spaceStr + "Bindings " + nativeArrayVarName + position
            return spaceStr + "Object " + nativeArrayVarName + position
                    + " = getWeaverEngine().getScriptEngine().toNativeArray(" + valueName
                    + position + ");" + ln();
        }
        String converted = "";
        final int currentNa = position;
        final int nextNa = position + 1;
        // int previousNa = dimension + 1;
        String currentBinding = nativeArrayVarName + currentNa;
        // converted += spaceStr + "Bindings " + currentBinding + " = Converter.newNativeArray();\n";
        converted += spaceStr + "Object " + currentBinding + " = Converter.newNativeArray();" + ln();
        String iNa = "i" + currentNa;
        converted += spaceStr + "for (int " + iNa + " = 0; i < " + valueName + currentNa + ".length; i++) {" + ln();
        converted += spaceStr + "\t" + baseType + StringUtils.repeat("[]", dimension - 1);
        converted += " " + valueName + nextNa + " = " + valueName + currentNa + "[ " + iNa + "];" + ln();
        converted += encapsulateBasedOnDimension(baseType, valueName, dimension - 1, position + 1);
        converted += spaceStr + "\t" + currentBinding + ".put(\"\"+" + iNa + ", " + nativeArrayVarName + nextNa
                + ");" + ln();
        converted += spaceStr + "}" + ln();
        return converted;
    }

    /**
     * Generates code for the actions that throws an {@link UnsupportedOperationException}. This code already captures
     * the correct join point name.
     *
     * @param action
     * @return
     */
    public static String UnsupActionExceptionCode(String action) {
        return "throw new UnsupportedOperationException(\"Join point \"+" + GenConstants.getClassName()
                + "()+\": Action " + action + " not implemented \");";
    }

    public static String UnsupDefExceptionCode(String attribute) {
        return "throw new UnsupportedOperationException(\"Join point \"+" + GenConstants.getClassName()
                + "()+\": attribute '\"+" + attribute + "+\"' cannot be defined\");";
    }

    public static String UnsupDefTypeExceptionCode(String attribute, String valueType) {
        return "throw new UnsupportedOperationException(\"Join point \"+" + GenConstants.getClassName()
                + "()+\": attribute '" + attribute + "' cannot be defined with the input type \"+" + valueType
                + ");";
    }

    /**
     * Generates the method with the name and parameters of the action
     *
     * @param action the action used to generate its method
     * @return
     */
    public static Method generateActionMethod(Action action, JavaAbstractsGenerator generator) {

        JavaType actionReturn = getJavaType(action.getReturn(), action.getName(), action, "ActionParam", generator);
        final Method m = new Method(actionReturn, action.getName());
        String comment = action.getTooltip();
        if (comment != null) {
            m.appendComment(comment);
        }
        for (final Parameter param : action.getParameter()) {

            String paramName = param.getName();
            paramName = StringUtils.getSanitizedName(paramName);
            JavaType jType = getJavaType(param.getType(), paramName, action, "ActionParam", generator);

            paramName = StringUtils.getSanitizedName(paramName);
            m.addArgument(jType, paramName);
            m.addJavaDocTag(JDocTag.PARAM, paramName + " ");
        }
        m.appendCode("throw new UnsupportedOperationException(" + GenConstants.getClassName() + "()+\": Action "
                + action.getName() + " not implemented \");");

        return m;
    }

    private static JavaType getJavaType(String type, String paramName, Action action, String sufix,
                                        JavaAbstractsGenerator generator) {

        JavaType jType;
        if (type.startsWith("{")) { // then it is an enumerator
            final String firstCharToUpper = StringUtils.firstCharToUpper(action.getName());
            final JavaEnum enumerator = generateEnum(type, paramName, firstCharToUpper + sufix, generator);
            generator.getEnums().add(enumerator);
            // if (!generator.isAbstractGetters())
            // javaC.addImport(enumerator.getClassPackage() + "." +
            // enumerator.getName());
            type = enumerator.getName();
            jType = JavaType.enumType(enumerator.getName(), enumerator.getClassPackage());
        } else {
            jType = ConvertUtils.getConvertedType(type, generator);
        }
        return jType;
    }

    /**
     * Processes the arguments. Processing includes:
     *
     * <p>
     * - Arrays are converted to arrays of Objects, for compatibility with the JavaScript layer.
     * </p>
     *
     * @param arguments
     * @return
     */
    public static List<Argument> convertParamArrayToObjArray(List<Argument> arguments) {
        var newArgs = new ArrayList<Argument>(arguments.size());

        for (var arg : arguments) {
            if (arg.getClassType().isArray()) {
                arg = arg.clone();
                arg.getClassType().setName("Object");
            }

            newArgs.add(arg);
        }

        return newArgs;
    }

    /**
     * Convert an action method to actionImpl,which will be the one the user should implement, and generate the action
     * implementation that invokes this new actionImpl
     *
     * @param original
     * @return
     */
    public static Method generateActionImplMethod(Method original, Action action,
                                                  JavaClass targetClass, JavaAbstractsGenerator generator) {

        String actionName = action.getName();
        String returnType = action.getReturn();
        boolean hasEvents = generator.hasEvents();

        JavaType actionReturn = getJavaType(action.getReturn(), action.getName(), action, "ActionParam", generator);

        // TODO: This is the abstract method that will be called from JavaScript, instead of cloned should have another name. Also, this method is called generateActionImplMethod, but is not generating that method.
        Method cloned = original.clone();
        original.setName(original.getName() + GenConstants.getImplementationSufix());
        cloned.clearCode();
        cloned.add(Modifier.FINAL);

        // If return type is not void, set return to Object
        if (!returnType.equals("void")) {
            cloned.setReturnType(JavaTypeFactory.getObjectType());
        }

        String joinedArgs = StringUtils.join(original.getParams(), Argument::getName, ", ");

        // Special case: when single argument is an array, it will be used as the varargs of triggerAction() that
        // expected Object[]. This can raise a warning, it might be ambiguous since we want to pass the array as
        // the only value of the varags, and not each element of the array as an arguments of the args.
        if (original.getParams().size() == 1 && original.getParams().get(0).getClassType().isArray()) {
            joinedArgs = "new Object[] { " + joinedArgs + "}";
        }

        cloned.appendCodeln("try {");
        cloned.appendCode("\t");
        // System.out.println("JOINED ARGS '" + joinedArgs + "': " + original.getParams());
        if (hasEvents) {
            targetClass.addImport(Stage.class);
            targetClass.addImport(Optional.class);
            // if (hasListeners()) {
            // eventTrigger().triggerAction(Stage.BEGIN, "insert", this, position, code);
            // }
            cloned.appendCodeln("if(hasListeners()) {");
            cloned.appendCode("\t\teventTrigger().triggerAction(Stage.BEGIN, \"" + actionName
                    + "\", this, Optional.empty()");

            if (!joinedArgs.isEmpty()) {
                cloned.appendCode(", " + joinedArgs);
            }

            cloned.appendCodeln(");");

            cloned.appendCodeln("\t}");
            cloned.appendCode("\t");
            if (!returnType.equals("void")) {
                cloned.appendCode(original.getReturnType().getSimpleType() + " result = ");
            }
        } else {
            // TODO: Not sure when this is called, and if it should also have code to convert null to undefined
            if (!returnType.equals("void")) {
                cloned.appendCode("return ");
            }
        }

        List<Argument> arguments = cloned.getParams();
        List<String> newArgs = new ArrayList<>(arguments.size());
        JavaType stringType = JavaTypeFactory.getStringType();
        for (Argument arg : arguments) {
            String argStr = "";
            if (arg.getClassType().isEnum()) {
                targetClass.addImport(NamedEnum.class);
                targetClass.addImport(arg.getClassType());
                argStr = "NamedEnum.fromString(" + arg.getClassType().getName() + ".class, " + arg.getName()
                        + ", \"parameter " + arg.getName() + "\")";
                arg.setClassType(stringType);
            } else if (arg.getClassType().isArray()) {
                argStr = "pt.up.fe.specs.util.SpecsCollections.cast(" + arg.getName() + ", " + arg.getClassType().getName() + ".class)";
            } else {
                argStr = arg.getName();
            }
            newArgs.add(argStr);
        }

        // System.out.println(actionName + ": " + newArgs);
        cloned.appendCodeln("this." + original.getName() + "(" + StringUtils.join(newArgs, ", ") + ");");
        if (hasEvents) {

            cloned.appendCodeln("\tif(hasListeners()) {");
            cloned.appendCode("\t\teventTrigger().triggerAction(Stage.END, \"" + actionName + "\", this, ");
            if (returnType.equals("void")) {
                cloned.appendCode("Optional.empty()");
            } else {
                cloned.appendCode("Optional.ofNullable(result)");
            }

            if (!joinedArgs.isEmpty()) {
                cloned.appendCode(", " + joinedArgs);
            }

            cloned.appendCodeln(");");
            cloned.appendCodeln("\t}");

            if (!returnType.equals("void")) {
                if (actionReturn.isPrimitive()) {
                    cloned.appendCodeln("\treturn result;");
                } else {
                    cloned.appendCodeln("\treturn result!=null?result:getUndefinedValue();");
                }
            }
        }

        cloned.appendCodeln("} catch(Exception e) {");
        cloned.appendCode("\tthrow new " + ActionException.class.getSimpleName());
        cloned.appendCodeln("(" + GenConstants.getClassName() + "(), \"" + actionName + "\", e);");
        cloned.appendCodeln("}");
        targetClass.addImport(ActionException.class);

        // Adapts parameters after processing and code generation is done, to improve compatibility with
        // calls from JavaScript
        cloned.setArguments(convertParamArrayToObjArray(cloned.getParams()));


        return cloned;

    }

    /**
     * @param selectName
     * @param type
     * @return
     */
    public static Method generateSelectMethod(Select sel, String _package, boolean isAbstract) {
        final String selectName = sel.getAlias();
        final String type = JoinPointModelConstructor.getJoinPointClass(sel);
        final String firstCharToUpper = Utils.firstCharToUpper(selectName);
        final String methodName = "select" + firstCharToUpper;
        final JavaType baseType = generateJoinPointBaseType(_package, type);
        final JavaGenericType genType = JavaTypeFactory.getWildExtendsType(baseType);
        final JavaType listType = JavaTypeFactory.getListJavaType(genType);
        // Method selectMethod = new Method("List<? extends A" +
        // typeFirstCharToUpper + ">", methodName);
        final Method selectMethod = new Method(listType, methodName);
        selectMethod.addJavaDocTag(JDocTag.RETURN);
        if (isAbstract) {
            selectMethod.add(Modifier.ABSTRACT);
        }
        String comment = Optional.ofNullable(sel.getTooltip())
                .orElse("Method used by the lara interpreter to select " + selectName + "s");
        selectMethod.appendComment(comment);
        return selectMethod;
    }

    /**
     * Generic implementation of the select method which uses the select() function in the global join point class.
     *
     * @param selectName
     * @param type
     * @return
     */
    public static Method generateSelectMethodGeneric(Select sel, String _package) {
        final String selectName = sel.getAlias();
        final String type = JoinPointModelConstructor.getJoinPointClass(sel);
        final String firstCharToUpper = Utils.firstCharToUpper(selectName);
        final String methodName = "select" + firstCharToUpper;
        final JavaType baseType = generateJoinPointBaseType(_package, type);
        final JavaGenericType genType = JavaTypeFactory.getWildExtendsType(baseType);
        final JavaType listType = JavaTypeFactory.getListJavaType(genType);
        // Method selectMethod = new Method("List<? extends A" +
        // typeFirstCharToUpper + ">", methodName);
        final Method selectMethod = new Method(listType, methodName);
        selectMethod.addJavaDocTag(JDocTag.RETURN);
        String comment = Optional.ofNullable(sel.getTooltip())
                .orElse("Default implementation of the method used by the lara interpreter to select " + selectName
                        + "s");
        selectMethod.appendComment(comment);

        selectMethod.appendCode("return select(" + baseType + ".class, SelectOp.DESCENDANTS);");

        return selectMethod;
    }

    /**
     * @param _package
     * @param typeFirstCharToUpper
     * @return
     */
    public static JavaType generateJoinPointBaseType(String _package, String type) {
        if (type.equals("joinpoint")) {
            type = "joinPoint"; // otherwise it will generate code with an error
        }
        return new JavaType(GenConstants.abstractPrefix() + Utils.firstCharToUpper(type), _package);
    }

    /**
     * Generate a java enum with the given name and collection of items
     *
     * @param String          the base for the name
     * @param attribute       the name of the attribute
     * @param itemsCollection the collection of items, i.e., a string with items separated by a comma
     * @return
     */
    public static JavaEnum generateEnum(String itemsCollection, String attributeName, String baseName,
                                        JavaAbstractsGenerator generator) {

        final String[] items = itemsCollection.substring(1, itemsCollection.length() - 1).split(",");
        // System.out.println(itemsCollection);
        final String javaEnumName = extractEnumName(baseName, attributeName);
        final JavaEnum enumerator = new JavaEnum(javaEnumName, generator.getLiteralEnumsPackage());
        for (String itemName : items) {
            itemName = itemName.trim();
            String enumName = itemName.toUpperCase();
            enumName = enumName.replace("-", "_");

            final EnumItem item = new EnumItem(enumName);
            item.addParameter('"' + itemName + '"');
            enumerator.add(item);
        }
        final Constructor enumConstr = new Constructor(enumerator);
        final JavaType stringType = JavaTypeFactory.getStringType();
        enumConstr.addArgument(stringType, "name");
        enumConstr.appendCode("this.name = name;" + ln());

        final Field nameField = new Field(stringType, "name");
        enumerator.add(nameField);
        final Method getName = new Method(stringType, "getName");
        getName.appendCode("return name;" + ln());
        enumerator.add(getName);

        enumerator.addInterface(JavaTypeFactory.convert(NamedEnum.class));

        return enumerator;
    }

    /**
     * Define the return type for the use of an enum. TODO: DECIDE BETWEEN RETURN OF STRING OR THE ENUM
     *
     * @param getter
     * @param enumerator
     * @param attributeField
     * @param abstractGetters
     */
    public static void defineEnumReturnType(Method getter, JavaEnum enumerator, Field attributeField,
                                            boolean abstractGetters) {
        getter.setReturnType(JavaTypeFactory.getStringType());
        // getter.setReturnType(enumerator.getName());
        if (!abstractGetters) {
            getter.clearCode();
            getter.appendCode("return this." + attributeField.getName() + ".toString();");
        }
        // + ";");
    }

    /**
     * Generate code for a given attribute
     *
     * @param javaC
     * @param enums
     * @param abstractGetters
     * @param langSpec
     * @param attribute
     */
    public static Method generateAttributeImpl(Method original, Attribute attribute, JavaClass targetClass,
                                               JavaAbstractsGenerator generator) {

        Method cloned = original.clone();
        original.setName(original.getName() + GenConstants.getImplementationSufix());
        cloned.clearCode();
        cloned.add(Modifier.FINAL);
        cloned.remove(Modifier.ABSTRACT);
        cloned.setReturnType(JavaTypeFactory.getObjectType());

        String joinedArgs = StringUtils.join(original.getParams(), Argument::getName, ", ");
        cloned.appendCodeln("try {");
        cloned.appendCode("\t");

        if (generator.hasEvents()) {
            targetClass.addImport(Stage.class);
            targetClass.addImport(Optional.class);
            // if (hasListeners()) {
            // eventTrigger().triggerAction(Stage.BEGIN, "insert", this, position, code);
            // }
            cloned.appendCodeln("if(hasListeners()) {");
            cloned.appendCode("\t\teventTrigger().triggerAttribute(Stage.BEGIN, this, \"" + attribute.getName()
                    + "\", Optional.empty()");
            if (!joinedArgs.isEmpty()) {
                cloned.appendCode(", " + joinedArgs);
            }
            cloned.appendCodeln(");");
            cloned.appendCodeln("\t}");
            cloned.appendCode("\t");
            cloned.appendCode(original.getReturnType().getSimpleType() + " result = ");
        } else {

            cloned.appendCode("return ");
        }
        cloned.appendCodeln("this." + original.getName() + "(" + joinedArgs + ");");
        if (generator.hasEvents()) {

            cloned.appendCodeln("\tif(hasListeners()) {");
            cloned.appendCode(
                    "\t\teventTrigger().triggerAttribute(Stage.END, this, \"" + attribute.getName()
                            + "\", Optional.ofNullable(result)");

            if (!joinedArgs.isEmpty()) {
                cloned.appendCode(", " + joinedArgs);
            }
            cloned.appendCodeln(");");
            cloned.appendCodeln("\t}");
            // cloned.appendCodeln("\treturn result;");
            cloned.appendCodeln("\treturn result!=null?result:getUndefinedValue();"); // return Undefined if result ==
            // null

        }
        cloned.appendCodeln("} catch(Exception e) {");
        cloned.appendCode("\tthrow new " + AttributeException.class.getSimpleName());
        cloned.appendCodeln("(" + GenConstants.getClassName() + "(), \"" + attribute.getName() + "\", e);");
        cloned.appendCodeln("}");
        targetClass.addImport(AttributeException.class);

        return cloned;
    }

    public static Method generateAttribute(Attribute attribute, JavaClass javaC, JavaAbstractsGenerator generator) {
        String attrClassStr = attribute.getType().trim();
        // String originalType = attrClassStr;
        boolean isEnum = false;
        JavaEnum enumerator = null;
        JavaType javaType;
        final String name = attribute.getName();
        if (attrClassStr.startsWith("{")) { // then it is an enumerator
            isEnum = true;
            enumerator = generateEnum(attrClassStr, name, javaC.getName(), generator);
            generator.getEnums().add(enumerator);
            // if (!generator.isAbstractGetters())
            // javaC.addImport(enumerator.getClassPackage() + "." +
            // enumerator.getName());
            attrClassStr = enumerator.getName();
            javaType = new JavaType(enumerator.getName(), enumerator.getClassPackage());
        } else {
            // Any primitive type for attributes are now converted into their wrapper
            javaType = ConvertUtils.getAttributeConvertedType(attrClassStr, generator);
        }
        final String sanName = StringUtils.getSanitizedName(name);
        final Field attributeField = new Field(javaType, sanName, Privacy.PROTECTED);
        // attributeField.setPrivacy(Privacy.PUBLIC);
        if (!generator.isAbstractGetters()) {
            javaC.add(attributeField);
        }

        final List<org.lara.language.specification.artifactsmodel.schema.Parameter> parameters = attribute
                .getParameter();
        if (parameters.isEmpty()) {

            final Pair<Method, Method> get_set = createGetterAndSetter(attributeField, name,
                    generator.isAbstractGetters());
            final Method getter = get_set.getLeft();
            if (isEnum) {
                defineEnumReturnType(getter, enumerator, attributeField, generator.isAbstractGetters());
            } else if (javaType.isArray()) {
                // TODO - see if this is really necessary, and if so correct the
                // implementations
                encapsulateArrayAttribute(javaC, getter);
            }
            // Old code
            // } else if (originalType.equals("Array")) { // if the attribute is
            // an array then it should return a
            // // NativeArray!
            // encapsulateArrayAttribute(javaC, getter);
            // }
            String comment = attribute.getTooltip();
            if (comment != null) {
                getter.setJavaDocComment(new JavaDoc(comment));
            }
            javaC.add(getter);

            return getter;
            // javaC.add(get_set.getRight());
        }
        if (StringUtils.isJavaKeyword(name)) {
            throw new RuntimeException("Could not create functional attribute with reserved keyword '" + name
                    + "'. Please define a different name for the attribute");
        }

        final Method methodForAttribute = new Method(javaType, name);

        methodForAttribute.add(Modifier.ABSTRACT);
        for (final org.lara.language.specification.artifactsmodel.schema.Parameter param : parameters) {

            final Argument arg = newSanitizedArgument(param.getName(), param.getType(), generator);
            methodForAttribute.addArgument(arg);
            methodForAttribute.addJavaDocTag(JDocTag.PARAM, arg.getName());
        }

        methodForAttribute.addJavaDocTag(JDocTag.RETURN, "");
        if (javaType.isArray()) {
            encapsulateArrayAttribute(javaC, methodForAttribute);
        }
        // if (originalType.equals("Array")) { // if the attribute is an array
        // then it should return a
        // NativeArray!
        // encapsulateArrayAttribute(javaC, methodForAttribute);
        // }

        javaC.add(methodForAttribute);
        return methodForAttribute;

    }

    private static Argument newSanitizedArgument(String name, String type, JavaAbstractsGenerator generator) {
        final String sanitizedName = StringUtils.getSanitizedName(name);
        final JavaType paramType = ConvertUtils.getConvertedType(type, generator);
        return new Argument(paramType, sanitizedName);
    }

    /**
     * Generate the default code that compares the nodes of the join points
     *
     * @param superClass
     * @return
     */
    public static Method generateCompareNodes(JavaType superClass) {
        final Method method = new Method(JavaTypeFactory.getBooleanType(), "compareNodes");
        method.addArgument(superClass, "aJoinPoint");
        // abstJPClass.addImport(javaGenerator.getJoinPointClassPackage()); //
        // JoinPoint.class.getCanonicalName()
        method.appendCode("return this.getNode().equals(aJoinPoint.getNode());");
        method.appendComment(
                "Compares the two join points based on their node reference of the used compiler/parsing tool.<br>"
                        + ln()
                        + "This is the default implementation for comparing two join points. <br>" + ln()
                        + "<b>Note for developers:</b> A weaver may override this implementation in the editable abstract join point, so"
                        + ln()
                        + "the changes are made for all join points, or override this method in specific join points.");
        return method;
    }

    /**
     * Generate the default implementation of the generic selection method.
     *
     * @param superClass
     * @return
     */
    public static Method generateSelectGeneric(JavaClass globalJpClass) {
        return generateSelectGeneric(globalJpClass, false);
    }

    public static Method generateSelectGeneric(JavaClass globalJpClass, boolean isAbstract) {

        // Select returns a List
        globalJpClass.addImport(List.class);

        JavaType returnType = new JavaType("<T extends " + globalJpClass.getName() + "> List<? extends T>");

        final Method method = new Method(returnType, "select");
        method.appendComment("Generic select function, used by the default select implementations.");

        method.addArgument(new JavaType("Class<T>"), "joinPointClass");
        method.addArgument(SelectOp.class, "op");

        if (isAbstract) {
            method.add(Modifier.ABSTRACT);
        } else {
            method.add(Annotation.OVERRIDE);
            method.appendCode(
                    "throw new RuntimeException(\"Generic select function not implemented yet. Implement it in order to use the default implementations of select\");");
        }

        return method;
    }

    /**
     * Defines if this joinpoint is an instanceof joinpointclass
     *
     * @param javaC
     * @param isFinal
     */
    public static void generateInstanceOf(JavaClass javaC, String superNameStr, boolean isFinal) {

        String argumentName = "joinpointClass";

        final Method clazzMethod = new Method(JavaTypeFactory.getBooleanType(), GenConstants.getInstanceOfName());
        clazzMethod.add(Annotation.OVERRIDE);
        if (isFinal) {
            clazzMethod.add(Modifier.FINAL);
        }
        clazzMethod.addArgument(String.class, argumentName);
        clazzMethod.appendComment("Defines if this joinpoint is an instanceof a given joinpoint class");
        clazzMethod.addJavaDocTag(JDocTag.RETURN, "True if this join point is an instanceof the given class");
        clazzMethod.appendCodeln(
                "boolean isInstance = " + GenConstants.getClassName() + "().equals(" + argumentName + ");");
        clazzMethod.appendCodeln(
                "if(isInstance) {" + ln() + "\treturn true;" + ln() + "}");
        clazzMethod.appendCodeln(
                "return " + superNameStr + "." + GenConstants.getInstanceOfName() + "(" + argumentName + ");");
        javaC.add(clazzMethod);
    }

    public static StringBuffer array2ListCode(String baseType, String listName, String joinedElements) {
        final StringBuffer arrayCode = new StringBuffer();
        arrayCode.append(baseType + "[] " + listName + "= {");
        if (!joinedElements.isEmpty()) {
            arrayCode.append(joinedElements);
        }
        arrayCode.append("};" + ln() + "return Arrays.asList(" + listName + ");");
        return arrayCode;
    }

    public static <T> StringBuffer array2ListCode(String baseType, String listName, Collection<T> elements,
                                                  Function<T, String> mapper) {
        final String joinedElements = StringUtils.join(elements, mapper, ", ");
        return array2ListCode(baseType, listName, joinedElements);
        // StringBuffer arrayCode = new StringBuffer();
        // arrayCode.append(baseType + "[] " + listName + "= {");
        // if (!elements.isEmpty()) {
        // String joinedElements = StringUtils.join(elements, mapper, ", ");
        // arrayCode.append(joinedElements);
        // }
        // arrayCode.append("};\nreturn Arrays.asList(" + listName + ");");
        // return arrayCode;
    }

    public static void generateDefMethods(Attribute attribute, JavaType returnType, JavaClass javaC,
                                          JavaAbstractsGenerator javaGenerator) {

        Function<String, String> codeProvider = t -> UnsupActionExceptionCode(
                "def " + attribute.getName() + " with type " + t);
        generateDefForType(attribute, returnType, javaC, javaGenerator, codeProvider);

    }

    public static void generateDefForType(Attribute attribute, JavaType returnType, JavaClass javaC,
                                          JavaAbstractsGenerator javaGenerator, Function<String, String> codeProvider) {
        List<DefArgType> defs = attribute.getDef();
        if (defs.isEmpty()) {
            return;
        }
        List<String> processedTypes = SpecsCollections.newArrayList();
        for (DefArgType defType : defs) {
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
            Method defAttrImpl = new Method(JavaTypeFactory.getVoidType(),
                    GenConstants.getDefAttributeImplName(attribute));
            defAttrImpl.setPrivacy(Privacy.PUBLIC);
            defAttrImpl.addArgument(defJavaType, "value");
            defAttrImpl.appendCode(codeProvider.apply(defJavaType.getName()));

            processedTypes.add(type);
            javaC.add(defAttrImpl);
        }
    }

    public static void addSuperDefs(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                    JoinPointType parent) {

        final Artifact artifact = generator.getLanguageSpecification().getArtifacts().getArtifact(parent.getClazz());
        if (artifact != null) {
            for (final Attribute attribute : artifact.getAttribute()) {
                List<DefArgType> defs = attribute.getDef();
                if (defs.isEmpty()) {
                    continue;
                }
                String attrClassStr = attribute.getType().trim();

                if (attrClassStr.startsWith("{")) { // then it is an enumerator
                    // attrClassStr = extractEnumName(attribute.getName());
                    attrClassStr = String.class.getSimpleName();
                }
                String defMethodName = GenConstants.getDefAttributeImplName(attribute);
                JavaType type = ConvertUtils.getAttributeConvertedType(attrClassStr, generator);

                Function<String, String> codeProvider = t -> "this." + fieldName + "." + defMethodName + "(value);";
                generateDefForType(attribute, type, javaC, generator, codeProvider);
            }
        }
    }

    public static void createDefImpl(JavaClass javaC, boolean isFinal,
                                     List<Attribute> attributes, JavaAbstractsGenerator javaGenerator) {
        Method defMethod = new Method(JavaTypeFactory.getVoidType(), GenConstants.withImpl("def"));
        defMethod.add(Annotation.OVERRIDE);
        if (isFinal) {
            defMethod.add(Modifier.FINAL);
        }
        defMethod.addArgument(String.class, "attribute");
        defMethod.addArgument(Object.class, "value");
        defMethod.appendCodeln("switch(attribute){");

        for (Attribute attribute : attributes) {
            // System.out.println("CREATING DEF FOR " + attribute.getN);
            List<DefArgType> def = attribute.getDef();
            JavaType returnType = ConvertUtils.getAttributeConvertedType(attribute.getType(), javaGenerator);
            defMethod.appendCodeln("case \"" + attribute.getName() + "\": {");
            List<String> processedTypes = SpecsCollections.newArrayList();
            for (DefArgType defType : def) {
                String type = defType.getType();
                // System.out.println("\tFOR DEF: " + type);
                if (processedTypes.contains(type)) {
                    continue;
                }
                JavaType defJavaType;
                if (type == null) {
                    defJavaType = returnType.clone();
                } else {
                    defJavaType = ConvertUtils.getAttributeConvertedType(type, javaGenerator);
                }
                javaC.addImport(defJavaType);
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

            defMethod.appendCodeln("\tthis.unsupportedTypeForDef(attribute, value);");
            // \tString valueType = value.getClass().getSimpleName();");
            // defMethod.appendCodeln("\tif(value instanceof JoinPoint){");
            // defMethod.appendCodeln("\t\tvalueType = ((JoinPoint)value).getJoinPointType();");
            // defMethod.appendCodeln("\t}");
            // defMethod.appendCodeln("\t" +
            // GeneratorUtils.UnsupDefTypeExceptionCode(attribute.getName(), "valueType"));
            defMethod.appendCodeln("}");
        }

        defMethod.appendCode("default: ");
        defMethod.appendCodeln(
                GeneratorUtils.UnsupDefExceptionCode("attribute"));
        defMethod.appendCodeln("}");
        javaC.add(defMethod);
        javaC.addImport(JoinPoint.class);
    }

    public static void createDefImplV2(JavaClass javaC, boolean isFinal,
                                       List<org.lara.language.specification.dsl.Attribute> attributes, JavaAbstractsGenerator javaGenerator) {
        Method defMethod = new Method(JavaTypeFactory.getVoidType(), GenConstants.withImpl("def"));
        defMethod.add(Annotation.OVERRIDE);
        if (isFinal) {
            defMethod.add(Modifier.FINAL);
        }
        defMethod.addArgument(String.class, "attribute");
        defMethod.addArgument(Object.class, "value");
        defMethod.appendCodeln("switch(attribute){");

        for (var attribute : attributes) {
            // TODO: Support DEFs in LangSpecV2, add here
            // List<DefArgType> def = attribute.getDef();
            // JavaType returnType = ConvertUtils.getAttributeConvertedType(attribute.getType(), javaGenerator);
            // defMethod.appendCodeln("case \"" + attribute.getName() + "\": {");
            // List<String> processedTypes = SpecsCollections.newArrayList();
            // for (DefArgType defType : def) {
            // String type = defType.getType();
            // // System.out.println("\tFOR DEF: " + type);
            // if (processedTypes.contains(type)) {
            // continue;
            // }
            // JavaType defJavaType;
            // if (type == null) {
            // defJavaType = returnType.clone();
            // } else {
            // defJavaType = ConvertUtils.getAttributeConvertedType(type, javaGenerator);
            // }
            // javaC.addImport(defJavaType);
            // defMethod.appendCodeln("\tif(value instanceof " + defJavaType.getSimpleType() + "){");
            // defMethod.appendCode("\t\tthis.");
            // defMethod.appendCode(GenConstants.getDefAttributeImplName(attribute.getName()));
            // defMethod.appendCode("((");
            // defMethod.appendCode(defJavaType.getSimpleType());
            // defMethod.appendCodeln(")value);");
            // defMethod.appendCodeln("\t\treturn;");
            // defMethod.appendCodeln("\t}");
            // processedTypes.add(type);
            // }

            defMethod.appendCodeln("\tthis.unsupportedTypeForDef(attribute, value);");
            // \tString valueType = value.getClass().getSimpleName();");
            // defMethod.appendCodeln("\tif(value instanceof JoinPoint){");
            // defMethod.appendCodeln("\t\tvalueType = ((JoinPoint)value).getJoinPointType();");
            // defMethod.appendCodeln("\t}");
            // defMethod.appendCodeln("\t" +
            // GeneratorUtils.UnsupDefTypeExceptionCode(attribute.getName(), "valueType"));
            defMethod.appendCodeln("}");
        }

        defMethod.appendCode("default: ");
        defMethod.appendCodeln(
                GeneratorUtils.UnsupDefExceptionCode("attribute"));
        defMethod.appendCodeln("}");
        javaC.add(defMethod);
        javaC.addImport(JoinPoint.class);
    }
}
