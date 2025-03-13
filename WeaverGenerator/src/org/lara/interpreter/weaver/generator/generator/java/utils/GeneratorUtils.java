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
import org.lara.interpreter.weaver.interf.NamedEnum;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.classtypes.JavaEnum;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.*;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;
import tdrc.utils.Pair;
import tdrc.utils.StringUtils;

import java.util.*;
import java.util.function.Function;

public class GeneratorUtils {

    private static String ln() {
        return Utils.ln();
    }

    public static void createListOfAvailableActions(JavaClass javaC, JoinPointClass joinPoint, String superName, boolean isFinal) {

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

        for (var act : joinPoint.getActionsSelf()) {
            listActions.appendCode("actions.add(\"" + act.getReturnType() + " " + act.getName() + "(");
            final String joinedParams = StringUtils.join(act.getParameters(), org.lara.language.specification.dsl.Parameter::getType, ", ");
            listActions.appendCode(joinedParams);
            listActions.appendCode(")\");" + ln());
        }


        javaC.add(listActions);
    }

    /**
     * Add methods of the super join point to the java class
     *
     * @param javaC
     * @param fieldName
     * @param langSpec
     * @param current
     */
    public static void addSuperMethods(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                       JoinPointClass current) {

        var parent = current.getExtendExplicit().orElse(null);
        if (parent == null) {
            return;
        }

        addSuperGetters(javaC, fieldName, generator, parent);
        addSuperMethods(javaC, fieldName, generator, parent);
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
                                       JoinPointClass parent) {

        addSuperGetters(javaC, fieldName, generator, parent.getAttributesSelf());
    }

    public static void addSuperGetters(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
                                       List<org.lara.language.specification.dsl.Attribute> attributes) {

        // TODO: Remove sort
        Collections.sort(attributes, (attribute, t1) -> attribute.getName().compareTo(t1.getName()));
        // System.out.println("JP: " + javaC.getName());
        //for (final Attribute attribute : attributes) {
        for (var attribute : attributes) {

            // System.out.println("ATTR:" + attribute.getName());
            String attrClassStr = attribute.getReturnType().trim();

            if (attrClassStr.startsWith("{")) { // then it is an enumerator
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

            final Method getter = createSuperGetter(sanitizedName, name, type, fieldName, attribute.getParameters(),
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
    public static void addSuperActions(JavaAbstractsGenerator javaGenerator, JavaClass javaC, JoinPointClass joinPointSuperType,
                                       String fieldName) {

        var jps = new ArrayList<>(joinPointSuperType.getActions());

        // TODO: HACK - Two insert methods are missing from the actions/generation, adding them manually

        var returnType = new ArrayType(new JPType(JoinPointClass.globalJoinPoint()));
//        var globalJpType = new GenericType("JoinpointInterface", false);
        var globalJpType = PrimitiveClasses.JOINPOINT_INTERFACE;
        var paramsJp = List.of(new Parameter(PrimitiveClasses.STRING, "position"), new Parameter(globalJpType, "code"));
        var paramsString = List.of(new Parameter(PrimitiveClasses.STRING, "position"), new Parameter(PrimitiveClasses.STRING, "code"));

        var insertActionWithJp = new org.lara.language.specification.dsl.Action(returnType, "insert", paramsJp);
        var insertActionWithString = new org.lara.language.specification.dsl.Action(returnType, "insert", paramsString);

        jps.add(insertActionWithString);
        jps.add(insertActionWithJp);

        // Sort with the insert actions inside
        Collections.sort(jps, (attribute, t1) -> attribute.getName().compareTo(t1.getName()));

// getJoinPointOwnActions(joinPointSuperType); // These two lines makes
        // the same thing as the code above
        // joinPointOwnActions.addAll(langSpec.getActionModel().getActionsForAll());
        for (var action : jps) {
            //for (var action : joinPointSuperType.getActionsSelf().stream().sorted((attribute, t1) -> attribute.getName().compareTo(t1.getName())).toList()) {

            if (javaGenerator.hasDefs() && action.getName().equals("def")) {
                continue;
            }

            final Method m = generateActionMethod(action, javaGenerator);
            m.setName(m.getName() + GenConstants.getImplementationSufix());
            m.clearCode();
            m.add(Annotation.OVERRIDE);
            if (!action.getReturnType().equals("void")) {
                m.appendCode("return ");
            }
            m.appendCode("this." + fieldName + "." + m.getName() + "(");
            final String joinedParameters = StringUtils.join(m.getParams(), Argument::getName, ", ");
            m.appendCode(joinedParameters);

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
                                            List<org.lara.language.specification.dsl.Parameter> list,
                                            JavaAbstractsGenerator generator) {

        if (!list.isEmpty()) {
            final Method getAttribute = new Method(getAttrType, originalName);
            // getAttribute.addModifier(Modifier.ABSTRACT);
            getAttribute.appendComment("Get value on attribute " + attr);
            getAttribute.addJavaDocTag(JDocTag.RETURN, "the attribute's value");
            getAttribute.appendCode("return this." + superField + "." + originalName + "(");

            for (var parameter : list) {

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
    public static Method generateActionMethod(org.lara.language.specification.dsl.Action action, JavaAbstractsGenerator generator) {

        JavaType actionReturn = getJavaType(action.getReturnType(), action.getName(), action, "ActionParam", generator);
        final Method m = new Method(actionReturn, action.getName());
        String comment = action.getToolTip().orElse(null);
        if (comment != null) {
            m.appendComment(comment);
        }
        for (var param : action.getParameters()) {

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

    private static JavaType getJavaType(String type, String paramName, org.lara.language.specification.dsl.Action action, String sufix,
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
                arg.getClassType().setPackage("java.lang");
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
    public static Method generateActionImplMethod(Method original, org.lara.language.specification.dsl.Action action,
                                                  JavaClass targetClass, JavaAbstractsGenerator generator) {

        String actionName = action.getName();
        String returnType = action.getReturnType();

        JavaType actionReturn = getJavaType(action.getReturnType(), action.getName(), action, "ActionParam", generator);

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

        // TODO: Not sure when this is called, and if it should also have code to convert null to undefined
        if (!returnType.equals("void")) {
            cloned.appendCode("return ");
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
    public static Method generateAttributeImpl(Method original, org.lara.language.specification.dsl.Attribute attribute, JavaClass targetClass,
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

        cloned.appendCode("return ");
        cloned.appendCodeln("this." + original.getName() + "(" + joinedArgs + ");");
        cloned.appendCodeln("} catch(Exception e) {");
        cloned.appendCode("\tthrow new " + AttributeException.class.getSimpleName());
        cloned.appendCodeln("(" + GenConstants.getClassName() + "(), \"" + attribute.getName() + "\", e);");
        cloned.appendCodeln("}");
        targetClass.addImport(AttributeException.class);

        return cloned;
    }


    public static Method generateAttribute(org.lara.language.specification.dsl.Attribute attribute, JavaClass javaC, JavaAbstractsGenerator generator) {
        String attrClassStr = attribute.getReturnType().trim();
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

        var parameters = attribute
                .getParameters();
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
            String comment = attribute.getToolTip().orElse(null);
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
        for (var param : parameters) {

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
    }
}
