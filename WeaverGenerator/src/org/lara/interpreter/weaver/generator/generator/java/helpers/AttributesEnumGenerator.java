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

import org.lara.language.specification.dsl.JoinPointClass;
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

    private JoinPointClass joinPoint;
    private JavaClass javaC;

    protected AttributesEnumGenerator(JavaClass javaC, JoinPointClass joinPoint) {
        this.javaC = javaC;
        this.joinPoint = joinPoint;
    }

    public static void generate(JavaClass javaC, JoinPointClass joinPoint) {
        AttributesEnumGenerator generator = new AttributesEnumGenerator(javaC, joinPoint);
        generator.generate();
    }

    public void generate() {
        var attributes = joinPoint.getAttributes();

        if (attributes.isEmpty()) {
            return;
        }

        String enumName = StringUtils.firstCharToUpper(joinPoint.getName()) + "Attributes";
        JavaEnum anEnum = new JavaEnum(enumName, javaC.getClassPackage());
        anEnum.setPrivacy(Privacy.PROTECTED);

        addAttributes(attributes, anEnum);

        addNameField(anEnum);

        addFromStringMethod(enumName, anEnum);

        addGetNamesMethod(enumName, anEnum);
        addContainsMethod(anEnum);
        javaC.add(anEnum);
    }

    private void addAttributes(List<org.lara.language.specification.dsl.Attribute> attributes, JavaEnum anEnum) {
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

    private void addContainsMethod(JavaEnum anEnum) {

        Method method = new Method(JavaTypeFactory.getBooleanType(), "contains", Modifier.STATIC);
        method.addArgument(String.class, "name");
        method.appendComment("True if the enum contains the given attribute name, false otherwise.");
        method.appendCodeln("return fromString(name).isPresent();");
        anEnum.addImport(Collectors.class);
        anEnum.addImport(Arrays.class);
        anEnum.add(method);
    }

}
