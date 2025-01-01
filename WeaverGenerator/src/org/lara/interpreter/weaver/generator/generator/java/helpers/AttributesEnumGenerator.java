/**
 * Copyright 2017 SPeCS.
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

import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.classtypes.JavaEnum;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.EnumItem;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import tdrc.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttributesEnumGenerator {

    private JoinPointClass joinPointV2;
    private JavaClass javaC;

    protected AttributesEnumGenerator(JavaClass javaC, JoinPointClass joinPointV2) {
        this.javaC = javaC;
        this.joinPointV2 = joinPointV2;
    }

    public static void generate(JavaClass javaC, JoinPointClass joinPointV2) {
        AttributesEnumGenerator generator = new AttributesEnumGenerator(javaC, joinPointV2);
        generator.generate();
    }

    public void generate() {
        String clazz = joinPointV2.getName();
/*
        List<Attribute> attributes = langSpec.getArtifacts()
                .getAttributesRecursively(clazz).stream().sorted((attribute, t1) -> attribute.getName().compareTo(t1.getName())).toList();

        System.out.println(clazz);
        System.out.println("OLD: " + langSpec.getArtifacts()
                .getAttributesRecursively(clazz).stream().map(Attribute::getName).sorted().toList());
        System.out.println("NEW: " + joinPointV2.getAttributes().stream().map(org.lara.language.specification.dsl.Attribute::getName).sorted().toList());
*/
        // TODO: Sorting so that refactor output can be compared with previous output, can be removed after
        var attributes = joinPointV2.getAttributes().stream().sorted((attribute, t1) -> attribute.getName().compareTo(t1.getName())).toList();

        if (attributes.isEmpty()) {
            return;
        }

        String enumName = StringUtils.firstCharToUpper(clazz) + "Attributes";
        JavaEnum anEnum = new JavaEnum(enumName, javaC.getClassPackage());
        anEnum.setPrivacy(Privacy.PROTECTED);

        //addAttributes(attributes, anEnum);
        addAttributesV2(attributes, anEnum);

        addNameField(anEnum);

        addFromStringMethod(enumName, anEnum);

        addGetNamesMethod(enumName, anEnum);
        addContainsMethod(anEnum);
        javaC.add(anEnum);
    }

    private void addAttributes(List<Attribute> attributes, JavaEnum anEnum) {
        for (Attribute attribute : attributes) {
            String name = attribute.getName();
            EnumItem item = new EnumItem(name.toUpperCase());
            item.addParameter("\"" + name + "\"");
            anEnum.add(item);
        }
    }

    private void addAttributesV2(List<org.lara.language.specification.dsl.Attribute> attributes, JavaEnum anEnum) {
        for (var attribute : attributes) {
            String name = attribute.getName();
            EnumItem item = new EnumItem(name.toUpperCase());
            item.addParameter("\"" + name + "\"");
            anEnum.add(item);
        }
    }

    private void addNameField(JavaEnum anEnum) {
        Field field = new Field(JavaTypeFactory.getStringType(), "name");
        Constructor constr = new Constructor(anEnum);
        constr.addArgument(field);
        constr.appendCode("this.name = name;");
        anEnum.add(field);
    }

    private void addFromStringMethod(String enumName, JavaEnum anEnum) {
        JavaType optionalAttribute = JavaTypeFactory.convert(Optional.class);
        JavaTypeFactory.addGenericType(optionalAttribute, new JavaType(enumName));
        Method method = new Method(optionalAttribute, "fromString", Modifier.STATIC);
        method.appendComment("Return an attribute enumeration item from a given attribute name");
        method.addArgument(String.class, "name");
        method.appendCodeln(
                "return Arrays.asList(values()).stream().filter(attr -> attr.name.equals(name)).findAny();");
        // method.appendCodeln("for (" + enumName + " attribute : values()) {");
        // method.appendCodeln("\tif (name.equals(attribute.name)) {");
        // method.appendCodeln("\t\treturn Optional.of(attribute);");
        // method.appendCodeln("\t}");
        // method.appendCodeln("}");
        // method.appendCodeln("return Optional.empty();");
        anEnum.add(method);
    }

    private void addGetNamesMethod(String enumName, JavaEnum anEnum) {

        Method method = new Method(JavaTypeFactory.getListStringJavaType(), "getNames", Modifier.STATIC);
        method.appendComment("Return a list of attributes in String format");
        method.appendCodeln(
                "return Arrays.asList(values()).stream().map(" + enumName + "::name).collect(Collectors.toList());");
        anEnum.addImport(Collectors.class);
        anEnum.addImport(Arrays.class);
        anEnum.add(method);
    }

    // static public boolean contains(Object name) {
    //
    // return getNames.contains(name);
    // }

    private void addContainsMethod(JavaEnum anEnum) {

        Method method = new Method(JavaTypeFactory.getBooleanType(), "contains", Modifier.STATIC);
        method.addArgument(String.class, "name");
        method.appendComment("True if the enum contains the given attribute name, false otherwise.");
        // method.appendCodeln("return getNames().contains(name);");
        method.appendCodeln("return fromString(name).isPresent();");
        anEnum.addImport(Collectors.class);
        anEnum.addImport(Arrays.class);
        anEnum.add(method);
    }

}
