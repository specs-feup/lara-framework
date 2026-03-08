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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.lara.interpreter.exception.ActionException;
import org.lara.interpreter.exception.AttributeException;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.NamedEnum;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.GenericType;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.classtypes.JavaEnum;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Argument;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.EnumItem;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.JavaDoc;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;

import pt.up.fe.specs.util.SpecsLogs;
import tdrc.utils.Pair;
import tdrc.utils.StringUtils;

public class GeneratorUtils {

    private static String ln() {
        return Utils.ln();
    }

    /**
     * Add methods of the super join point to the java class, resolving ThisType to
     * "Self".
     *
     * <p>
     * With CRTP (Curiously Recurring Template Pattern), all classes in the
     * hierarchy use the same "Self" type parameter. Therefore, inherited methods
     * that use ThisType should also resolve to "Self" - the override is valid
     * because both parent and child use "Self".
     * </p>
     *
     * @param javaC         the target Java class
     * @param fieldName     the name of the field holding the super join point
     * @param generator     the generator context
     * @param current       the current join point class
     * @param currentJpType the JavaType representing "Self" - used for ThisType
     *                      resolution in all methods
     */
    public static void addSuperMethods(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
            JoinPointClass current, JavaType currentJpType) {

        var parent = current.getExtendExplicit().orElse(null);
        if (parent == null) {
            return;
        }

        // With CRTP, all classes use "Self" as their type parameter, so inherited
        // methods should also use "Self" for ThisType resolution (the override is valid
        // because both parent and child use the same type parameter "Self")
        addSuperGetters(javaC, fieldName, generator, parent.getAttributesSelf(), currentJpType);
        addSuperMethods(javaC, fieldName, generator, parent, currentJpType);
    }

    /**
     * Add getter methods for inherited attributes.
     * 
     * <p>
     * With CRTP (Curiously Recurring Template Pattern), all classes use "Self" as
     * their type parameter. Therefore, inherited attributes that use ThisType
     * should also resolve to "Self" - the override is valid because both parent and
     * child use the same type parameter.
     * </p>
     *
     * @param javaC         the target Java class
     * @param fieldName     the name of the field holding the super join point
     * @param generator     the generator context
     * @param attributes    the list of inherited attributes
     * @param currentJpType the JavaType representing "Self" (for ThisType
     *                      resolution)
     */
    public static void addSuperGetters(JavaClass javaC, String fieldName, JavaAbstractsGenerator generator,
            List<Attribute> attributes, JavaType currentJpType) {

        var mutableAttributes = new ArrayList<>(attributes);
        mutableAttributes.sort(Comparator.comparing(Attribute::getName));

        for (var attribute : mutableAttributes) {

            IType attrType = normalizeAttributeTypeForGetter(attribute);
            JavaType type = ConvertUtils.getConvertedType(attrType, generator, currentJpType);
            Function<Parameter, JavaType> parameterTypeResolver = parameter -> ConvertUtils.getConvertedType(
                    parameter.getIType(), generator, currentJpType);
            boolean needsCast = currentJpType != null && TypeTraversalUtils.containsThisType(attrType);

            String sanitizedName = sanitizeAttributeName(attribute.getName());
            String methodBase = attributeMethodBaseName(attribute.getName());

            String effectiveMethodBase = methodBase;
            if (type.isArray()) {
                effectiveMethodBase += GenConstants.getArrayMethodSufix();
            }
            if (generator.hasImplMode() && !type.isArray()) {
                effectiveMethodBase += GenConstants.getImplementationSufix();
            }

            final Method getter = createSuperGetter(sanitizedName, effectiveMethodBase, type, fieldName,
                    attribute.getParameters(), parameterTypeResolver, needsCast);

            if (hasMethodSignature(javaC, getter)) {
                continue;
            }

            getter.add(Annotation.OVERRIDE);
            javaC.add(getter);
        }

    }

    private static IType normalizeAttributeTypeForGetter(Attribute attribute) {
        IType attrType = attribute.getType();
        if (!(attrType instanceof LiteralEnum)) {
            return attrType;
        }

        return new GenericType("String", false);
    }

    /**
     * Create the action methods calling the super class method
     *
     */
    public static void addSuperActions(JavaAbstractsGenerator javaGenerator, JavaClass javaC,
            JoinPointClass joinPointSuperType,
            String fieldName,
            JavaType currentJpType) {

        var jps = new ArrayList<>(joinPointSuperType.getActions());

        // TODO: HACK - Two insert methods are missing from the actions/generation,
        // adding them manually

        var returnType = new ArrayType(new JPType(JoinPointClass.globalJoinPoint()));
        // var globalJpType = new GenericType("JoinpointInterface", false);
        var globalJpType = PrimitiveClasses.JOINPOINT_INTERFACE;
        var paramsJp = List.of(new Parameter(PrimitiveClasses.STRING, "position"), new Parameter(globalJpType, "code"));
        var paramsString = List.of(new Parameter(PrimitiveClasses.STRING, "position"),
                new Parameter(PrimitiveClasses.STRING, "code"));

        var insertActionWithJp = new org.lara.language.specification.dsl.Action(returnType, "insert", paramsJp);
        var insertActionWithString = new org.lara.language.specification.dsl.Action(returnType, "insert", paramsString);

        jps.add(insertActionWithString);
        jps.add(insertActionWithJp);

        // Sort with the insert actions inside
        jps.sort(Comparator.comparing(Action::getName));

        for (var action : jps) {
            final Method m = generateActionMethod(action, javaGenerator, currentJpType);
            m.setName(m.getName() + GenConstants.getImplementationSufix());
            m.clearCode();
            m.add(Annotation.OVERRIDE);

            // Cast is only needed when ThisType is involved.
            // JPType by itself does not require a cast and may trigger unnecessary-cast
            // warnings.
            boolean needsCast = TypeTraversalUtils.containsThisType(action.getType());

            if (!action.getReturnType().equals("void")) {
                m.appendCode("return ");
                if (needsCast) {
                    // Add cast to the method's return type (which has Self resolved)
                    m.appendCode("(" + m.getReturnType().getSimpleType() + ") ");
                }
            }
            appendDelegationInvocation(m, fieldName, m.getName());

            if (!hasMethodSignature(javaC, m)) {
                javaC.add(m);
            }
        }

    }

    public static String extractEnumName(String jpName, String attribute) {
        return Utils.firstCharToUpper(jpName) + Utils.firstCharToUpper(attribute) + "Enum";
    }

    /**
     * Create Methods based on the fields
     *
     */
    public static Pair<Method, Method> createGetterAndSetter(Field field, String originalName,
            boolean abstractGetters) {
        final String attr = field.getName();
        final JavaType attrClassType = field.getType();

        final Method getAttribute = createGetter(attr, originalName, attrClassType, abstractGetters);
        final Method setAttribute = createSetter(attr, originalName, attrClassType);

        return new Pair<>(getAttribute, setAttribute);
    }

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

    private static Method createSuperGetter(String attr, String originalName, JavaType getAttrType, String superField,
            List<Parameter> list,
            Function<Parameter, JavaType> parameterTypeResolver, boolean needsCast) {

        final boolean hasParameters = !list.isEmpty();
        final String methodName = hasParameters ? originalName : "get" + Utils.firstCharToUpper(originalName);
        final Method getAttribute = new Method(getAttrType, methodName);
        getAttribute.appendComment("Get value on attribute " + attr);
        getAttribute.addJavaDocTag(JDocTag.RETURN, "the attribute's value");
        getAttribute.appendCode("return ");
        if (needsCast) {
            getAttribute.appendCode("(" + getAttrType.getSimpleType() + ") ");
        }

        for (var parameter : list) {
            JavaType type = parameterTypeResolver.apply(parameter);
            getAttribute.addArgument(type, parameter.getName());
        }

        appendDelegationInvocation(getAttribute, superField, methodName);

        return getAttribute;
    }

    private static void appendDelegationInvocation(Method method, String targetField, String targetMethodName) {
        method.appendCode("this." + targetField + "." + targetMethodName + "(");
        method.appendCode(StringUtils.join(method.getParams(), Argument::getName, ", "));
        method.appendCode(");");
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
        getter.appendCode("return " + newGetter.getName() + "(");
        final List<Argument> arguments = getter.getParams();
        final String argsList = StringUtils.join(arguments, Argument::getName, ", ");
        getter.appendCode(argsList);
        getter.appendCode(");");
        getter.remove(Modifier.ABSTRACT);
        javaC.add(newGetter);
    }

    public static boolean hasMethodSignature(JavaClass javaClass, Method candidate) {
        return javaClass.getMethods().stream().anyMatch(existing -> sameSignature(existing, candidate));
    }

    private static boolean sameSignature(Method first, Method second) {
        if (!first.getName().equals(second.getName())) {
            return false;
        }
        List<Argument> firstParams = first.getParams();
        List<Argument> secondParams = second.getParams();
        if (firstParams.size() != secondParams.size()) {
            return false;
        }
        for (int i = 0; i < firstParams.size(); i++) {
            JavaType leftType = firstParams.get(i).getClassType();
            JavaType rightType = secondParams.get(i).getClassType();
            if (!Objects.equals(leftType, rightType)) {
                return false;
            }
        }
        return true;
    }

    private static String sanitizeAttributeName(String attributeName) {
        return StringUtils.getSanitizedName(attributeName);
    }

    private static String attributeMethodBaseName(String attributeName) {
        String sanitized = StringUtils.getSanitizedName(attributeName);
        String withoutPrefix = sanitized.replaceFirst("^_+", "");
        return withoutPrefix.isEmpty() ? sanitized : withoutPrefix;
    }

    public static boolean hasSameSignature(Action left, Action right) {
        return hasSameParameterTypes(left, right)
                && normalizeType(left.getReturnType()).equals(normalizeType(right.getReturnType()));
    }

    public static boolean hasSameParameterTypes(Action left, Action right) {
        List<Parameter> leftParams = left.getParameters();
        List<Parameter> rightParams = right.getParameters();
        if (leftParams.size() != rightParams.size()) {
            return false;
        }

        for (int i = 0; i < leftParams.size(); i++) {
            String leftType = normalizeType(leftParams.get(i).getType());
            String rightType = normalizeType(rightParams.get(i).getType());
            if (!leftType.equals(rightType)) {
                return false;
            }
        }

        return true;
    }

    public static Optional<java.lang.reflect.Method> findJoinPointBaseMethod(Action action) {
        return Arrays.stream(org.lara.interpreter.weaver.interf.JoinPoint.class.getMethods())
                .filter(method -> method.getName().equals(action.getName()))
                .filter(method -> parametersMatch(method, action))
                .findFirst();
    }

    public static String toSpecTypeName(Class<?> type) {
        if (type.isArray()) {
            return toSpecTypeName(type.getComponentType()) + "[]";
        }

        if (org.lara.interpreter.weaver.interf.JoinPoint.class.equals(type)) {
            return "joinpoint";
        }

        return type.getSimpleName();
    }

    public static String normalizeType(String type) {
        return type.replace("java.lang.", "").trim();
    }

    public static JoinPointBaseActionInfo analyzeJoinPointBaseAction(Action action) {
        var baseMethod = findJoinPointBaseMethod(action);
        if (baseMethod.isEmpty()) {
            return new JoinPointBaseActionInfo(Optional.empty(), false);
        }

        java.lang.reflect.Method method = baseMethod.get();
        boolean skipWrapper = java.lang.reflect.Modifier.isFinal(method.getModifiers());
        String expectedReturnType = toSpecTypeName(method.getReturnType());
        String actualReturnType = action.getReturnType();
        Optional<String> correctedReturnType = normalizeType(expectedReturnType).equals(normalizeType(actualReturnType))
                ? Optional.empty()
                : Optional.of(expectedReturnType);

        return new JoinPointBaseActionInfo(correctedReturnType, skipWrapper);
    }

    /**
     * Aligns an action with the base JoinPoint contract (if one exists), applying a
     * corrected return type when needed.
     *
     * @param action            action to normalize
     * @param generator         generator used to resolve corrected type names
     * @param warningMessageFmt message format with placeholders: action name,
     *                          corrected type name
     * @return true when wrapper generation should be skipped because the base
     *         method is final
     */
    public static boolean normalizeJoinPointBaseAction(Action action, JavaAbstractsGenerator generator,
            String warningMessageFmt) {
        var baseInfo = analyzeJoinPointBaseAction(action);
        baseInfo.correctedReturnType().ifPresent(specTypeName -> {
            action.setType(generator.getLanguageSpecification().getType(specTypeName));
            SpecsLogs.warn(warningMessageFmt.formatted(action.getName(), specTypeName));
        });

        return baseInfo.skipWrapper();
    }

    public static void addActionAndWrapper(JavaClass targetClass, Action action, JavaAbstractsGenerator generator,
            JavaType currentJpType, boolean skipWrapper, String duplicateActionMessage,
            String duplicateWrapperMessage) {
        final Method method = generateActionMethod(action, generator, currentJpType);
        if (hasMethodSignature(targetClass, method)) {
            SpecsLogs.warn(duplicateActionMessage.formatted(action.getName(), method.getName()));
            return;
        }
        targetClass.add(method);

        Method wrapper = generateActionImplMethod(method, action, targetClass, generator, currentJpType);
        if (skipWrapper) {
            return;
        }

        if (hasMethodSignature(targetClass, wrapper)) {
            SpecsLogs.warn(duplicateWrapperMessage.formatted(wrapper.getName()));
            return;
        }

        targetClass.add(wrapper);
    }

    public record JoinPointBaseActionInfo(Optional<String> correctedReturnType, boolean skipWrapper) {
    }

    private static boolean parametersMatch(java.lang.reflect.Method method, Action action) {
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

    /**
     * Generates the method with the name and parameters of the action, resolving
     * ThisType to the current join point type.
     *
     * @param action        the action used to generate its method
     * @param generator     the generator context
     * @param currentJpType the JavaType representing the current join point
     *                      abstract being generated
     */
    public static Method generateActionMethod(org.lara.language.specification.dsl.Action action,
            JavaAbstractsGenerator generator, JavaType currentJpType) {
        JavaType actionReturn = getJavaType(action.getType(), action.getName(), action, "ActionParam", generator,
                currentJpType);
        final Method m = new Method(actionReturn, action.getName());
        action.getToolTip().ifPresent(m::appendComment);
        for (var param : action.getParameters()) {

            String paramName = param.getName();
            paramName = StringUtils.getSanitizedName(paramName);
            JavaType jType = getJavaType(param.getIType(), paramName, action, "ActionParam", generator,
                    currentJpType);

            paramName = StringUtils.getSanitizedName(paramName);
            m.addArgument(jType, paramName);
            m.addJavaDocTag(JDocTag.PARAM, paramName + " ");
        }
        m.appendCode("throw new UnsupportedOperationException(" + GenConstants.getClassName() + "()+\": Action "
                + action.getName() + " not implemented \");");

        return m;
    }

    private static JavaType getJavaType(IType type, String paramName,
            org.lara.language.specification.dsl.Action action, String sufix,
            JavaAbstractsGenerator generator, JavaType currentJpType) {

        if (type instanceof LiteralEnum literalEnum && literalEnum.getValues().size() > 1) {
            final String firstCharToUpper = StringUtils.firstCharToUpper(action.getName());
            final JavaEnum enumerator = generateEnum(literalEnum.getValues(), paramName, firstCharToUpper + sufix,
                    generator);
            generator.getEnums().add(enumerator);
            return JavaType.enumType(enumerator.getName(), enumerator.getClassPackage());
        }
        return ConvertUtils.getConvertedType(type, generator, currentJpType);
    }

    /**
     * Processes the arguments. Processing includes:
     *
     * <p>
     * - Arrays are converted to arrays of Objects, for compatibility with the
     * JavaScript layer.
     * </p>
     *
     */
    public static List<Argument> convertParamArrayToObjArray(List<Argument> arguments) {
        var newArgs = new ArrayList<Argument>(arguments.size());

        for (var arg : arguments) {
            if (arg.getClassType().isArray()) {
                int arrayDimension = arg.getClassType().getArrayDimension();
                arg = arg.clone();
                JavaType objectArrayType = JavaTypeFactory.getObjectType();
                objectArrayType.setArrayDimension(arrayDimension);
                arg.setClassType(objectArrayType);
            }

            newArgs.add(arg);
        }

        return newArgs;
    }

    /**
     * Convert an action method to actionImpl,which will be the one the user should
     * implement, and generate the action
     * implementation that invokes this new actionImpl
     *
     */
    public static Method generateActionImplMethod(Method original, org.lara.language.specification.dsl.Action action,
            JavaClass targetClass, JavaAbstractsGenerator generator, JavaType currentJpType) {

        String actionName = action.getName();
        String returnType = action.getReturnType();
        boolean hasEvents = generator.hasEvents();

        // Use IType-aware conversion that properly handles ThisType
        JavaType actionReturn = getJavaType(action.getType(), action.getName(), action, "ActionParam", generator,
                currentJpType);

        // TODO: This is the abstract method that will be called from JavaScript,
        // instead of cloned should have another name. Also, this method is called
        // generateActionImplMethod, but is not generating that method.
        Method cloned = original.clone();
        original.setName(original.getName() + GenConstants.getImplementationSufix());
        cloned.clearCode();
        cloned.add(Modifier.FINAL);

        // If return type is not void, set return to Object
        if (!returnType.equals("void")) {
            cloned.setReturnType(JavaTypeFactory.getObjectType());
        }

        String joinedArgs = StringUtils.join(original.getParams(), Argument::getName, ", ");

        // Special case: when single argument is an array, it will be used as the
        // varargs of triggerAction() that
        // expected Object[]. This can raise a warning, it might be ambiguous since we
        // want to pass the array as
        // the only value of the varags, and not each element of the array as an
        // arguments of the args.
        if (original.getParams().size() == 1 && original.getParams().get(0).getClassType().isArray()) {
            joinedArgs = "new Object[] { " + joinedArgs + "}";
        }

        cloned.appendCodeln("try {");
        cloned.appendCode("\t");

        if (hasEvents) {
            targetClass.addImport(Stage.class);
            targetClass.addImport(Optional.class);

            cloned.appendCodeln("if(hasListeners()) {");
            cloned.appendCode("\t\teventTrigger().triggerAction(Stage.BEGIN, \"" + actionName
                    + "\", this, Optional.empty()");

            if (!joinedArgs.isEmpty()) {
                cloned.appendCode(", " + joinedArgs);
            }

            cloned.appendCodeln(");");

            cloned.appendCodeln("\t}");
            cloned.appendCode("\t");
        }

        if (!returnType.equals("void")) {
            cloned.appendCode(original.getReturnType().getSimpleType() + " result = ");
        }

        List<Argument> arguments = cloned.getParams();
        List<String> newArgs = new ArrayList<>(arguments.size());
        JavaType stringType = JavaTypeFactory.getStringType();
        for (Argument arg : arguments) {
            String argStr;
            if (arg.getClassType().isEnum()) {
                targetClass.addImport(NamedEnum.class);
                targetClass.addImport(arg.getClassType());
                argStr = "NamedEnum.fromString(" + arg.getClassType().getName() + ".class, " + arg.getName()
                        + ", \"parameter " + arg.getName() + "\")";
                arg.setClassType(stringType);
            } else if (arg.getClassType().isArray()) {
                argStr = "pt.up.fe.specs.util.SpecsCollections.cast(" + arg.getName() + ", "
                        + arg.getClassType().getName() + ".class)";
            } else {
                argStr = arg.getName();
            }
            newArgs.add(argStr);
        }

        // System.out.println(actionName + ": " + newArgs);
        cloned.appendCodeln("this." + original.getName() + "(" + String.join(", ", newArgs) + ");");

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
        }

        if (!returnType.equals("void")) {
            if (actionReturn.isPrimitive()) {
                cloned.appendCodeln("\treturn result;");
            } else {
                cloned.appendCodeln("\treturn result!=null?result:getUndefinedValue();");
            }
        }

        cloned.appendCodeln("} catch(Exception e) {");
        cloned.appendCode("\tthrow new " + ActionException.class.getSimpleName());
        cloned.appendCodeln("(" + GenConstants.getClassName() + "(), \"" + actionName + "\", e);");
        cloned.appendCodeln("}");
        targetClass.addImport(ActionException.class);

        // Adapts parameters after processing and code generation is done, to improve
        // compatibility with
        // calls from JavaScript
        cloned.setArguments(convertParamArrayToObjArray(cloned.getParams()));

        return cloned;

    }

    public static JavaType generateJoinPointBaseType(String _package, String type) {
        if (type.equals("joinpoint")) {
            type = "joinPoint"; // otherwise it will generate code with an error
        }
        return new JavaType(GenConstants.abstractPrefix() + Utils.firstCharToUpper(type), _package);
    }

    /**
     * Generate a java enum with the given name and collection of items
     *
     * @param itemsCollection the collection of items, i.e., a string with items
     *                        separated by a comma
     * @param attributeName   the name of the attribute
     * @param baseName        the base for the name
     */
    public static JavaEnum generateEnum(List<String> items, String attributeName, String baseName,
            JavaAbstractsGenerator generator) {

        final String javaEnumName = extractEnumName(baseName, attributeName);
        final JavaEnum enumerator = new JavaEnum(javaEnumName, generator.getLiteralEnumsPackage());
        for (String itemName : items) {
            itemName = itemName.trim();
            String enumName = itemName.toUpperCase().replaceAll("[^A-Z0-9_]", "_");
            if (!enumName.isEmpty() && Character.isDigit(enumName.charAt(0))) {
                enumName = "_" + enumName;
            }

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
     * Define the return type for the use of an enum.
     * TODO: DECIDE BETWEEN RETURN OF STRING OR THE ENUM
     *
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
     */
    public static Method generateAttributeImpl(Method original, org.lara.language.specification.dsl.Attribute attribute,
            JavaClass targetClass,
            JavaAbstractsGenerator generator,
            boolean skipWrapper) {

        Method cloned = original.clone();
        original.setName(original.getName() + GenConstants.getImplementationSufix());

        if (skipWrapper) {
            return null;
        }

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
            cloned.appendCodeln("if(hasListeners()) {");
            cloned.appendCode("\t\teventTrigger().triggerAttribute(Stage.BEGIN, this, \"" + attribute.getName()
                    + "\", Optional.empty()");
            if (!joinedArgs.isEmpty()) {
                cloned.appendCode(", " + joinedArgs);
            }
            cloned.appendCodeln(");");
            cloned.appendCodeln("\t}");
            cloned.appendCode("\t");
        }

        cloned.appendCode(original.getReturnType().getSimpleType() + " result = ");
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
        }

        if (original.getReturnType().isPrimitive()) {
            cloned.appendCodeln("\treturn result;");
        } else {
            cloned.appendCodeln("\treturn result!=null?result:getUndefinedValue();");
        }

        cloned.appendCodeln("} catch(Exception e) {");
        cloned.appendCode("\tthrow new " + AttributeException.class.getSimpleName());
        cloned.appendCodeln("(" + GenConstants.getClassName() + "(), \"" + attribute.getName() + "\", e);");
        cloned.appendCodeln("}");
        targetClass.addImport(AttributeException.class);

        return cloned;
    }

    /**
     * Generate code for a given attribute, resolving ThisType to the current join
     * point type.
     *
     * @param attribute     the attribute to generate
     * @param javaC         the target Java class
     * @param generator     the generator context
     * @param currentJpType the JavaType representing the current join point
     *                      abstract being generated
     */
    public static Method generateAttribute(org.lara.language.specification.dsl.Attribute attribute, JavaClass javaC,
            JavaAbstractsGenerator generator, JavaType currentJpType) {
        IType attrType = attribute.getType();
        boolean isEnum = false;
        JavaEnum enumerator = null;
        JavaType javaType;
        final String name = attribute.getName();
        final String fieldName = sanitizeAttributeName(name);
        final String methodBaseName = attributeMethodBaseName(name);

        // A LiteralEnum with only one value is typically a type reference like
        // {TypeName}, not a true enum. We only generate an enum for multi-value
        // LiteralEnums.
        if (attrType instanceof LiteralEnum literalEnum && literalEnum.getValues().size() > 1) {
            isEnum = true;
            enumerator = generateEnum(literalEnum.getValues(), name, javaC.getName(), generator);
            generator.getEnums().add(enumerator);
            javaType = new JavaType(enumerator.getName(), enumerator.getClassPackage());
        } else {
            // Use IType-aware conversion that resolves ThisType while preserving
            // primitive signatures in generated impl methods.
            javaType = ConvertUtils.getConvertedType(attrType, generator, currentJpType);
        }
        final Field attributeField = new Field(javaType, fieldName, Privacy.PROTECTED);
        if (!generator.isAbstractGetters()) {
            javaC.add(attributeField);
        }

        var parameters = attribute.getParameters();
        if (parameters.isEmpty()) {

            final Pair<Method, Method> get_set = createGetterAndSetter(attributeField, methodBaseName,
                    generator.isAbstractGetters());
            final Method getter = get_set.left();
            if (isEnum) {
                defineEnumReturnType(getter, enumerator, attributeField, generator.isAbstractGetters());
            } else if (javaType.isArray()) {
                encapsulateArrayAttribute(javaC, getter);
            }
            attribute.getToolTip().ifPresent(comment -> getter.setJavaDocComment(new JavaDoc(comment)));
            javaC.add(getter);

            return getter;
        }
        final Method methodForAttribute = new Method(javaType, name);

        methodForAttribute.add(Modifier.ABSTRACT);
        for (var param : parameters) {
            // Use IType-aware conversion for parameter types as well
            final Argument arg = newSanitizedArgument(param.getName(), param.getIType(), generator, currentJpType);
            methodForAttribute.addArgument(arg);
            methodForAttribute.addJavaDocTag(JDocTag.PARAM, arg.getName());
        }

        methodForAttribute.addJavaDocTag(JDocTag.RETURN, "");
        if (javaType.isArray()) {
            encapsulateArrayAttribute(javaC, methodForAttribute);
        }

        javaC.add(methodForAttribute);
        return methodForAttribute;
    }

    /**
     * Creates a sanitized argument from an IType, resolving ThisType to the current
     * join point type.
     */
    private static Argument newSanitizedArgument(String name, IType type, JavaAbstractsGenerator generator,
            JavaType currentJpType) {
        final String sanitizedName = StringUtils.getSanitizedName(name);
        final JavaType paramType = ConvertUtils.getConvertedType(type, generator, currentJpType);
        return new Argument(paramType, sanitizedName);
    }

    /**
     * Generate the default code that compares the nodes of the join points
     *
     */
    public static Method generateCompareNodes(JavaType superClass) {
        final Method method = new Method(JavaTypeFactory.getBooleanType(), "compareNodes");
        method.addArgument(
                ConvertUtils.withJoinPointWildcard(superClass), "aJoinPoint");
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
