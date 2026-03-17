package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;

import java.util.*;

/**
 * Generates user-defined entity classes (TypeDef) and enum classes (EnumDef).
 */
public final class EntityGenerator {

    private final WeaverModel model;
    private final GeneratorConfig config;

    public EntityGenerator(WeaverModel model, GeneratorConfig config) {
        this.model = model;
        this.config = config;
    }

    public Map<String, String> generateTypeDefs() {
        var files = new LinkedHashMap<String, String>();
        for (var td : model.getTypeDefs().values()) {
            files.put(td.name(), generateTypeDef(td));
        }
        return files;
    }

    public Map<String, String> generateEnumDefs() {
        var files = new LinkedHashMap<String, String>();
        for (var ed : model.getEnumDefs().values()) {
            files.put(ed.name(), generateEnumDef(ed));
        }
        return files;
    }

    private String generateTypeDef(TypeDef td) {
        var sb = new JavaSourceBuilder();
        sb.line("package " + config.entitiesPackage() + ";");
        sb.line();

        sb.line("/**");
        if (td.tooltip() != null) {
            sb.line(" * " + td.tooltip());
        } else {
            sb.line(" * User-defined type: " + td.name());
        }
        sb.line(" */");
        sb.openBlock("public class " + TypeMapper.capitalize(td.name()));

        // Fields
        for (var field : td.fields()) {
            var javaType = mapType(field.type());
            sb.line("private " + javaType + " " + field.name() + ";");
        }
        sb.line();

        // Getters and setters
        for (var field : td.fields()) {
            var javaType = mapType(field.type());
            var cap = TypeMapper.capitalize(field.name());

            sb.openBlock("public " + javaType + " get" + cap + "()");
            sb.line("return " + field.name() + ";");
            sb.closeBlock();
            sb.line();

            sb.openBlock("public void set" + cap + "(" + javaType + " " + field.name() + ")");
            sb.line("this." + field.name() + " = " + field.name() + ";");
            sb.closeBlock();
            sb.line();
        }

        sb.closeBlock();
        return sb.toString();
    }

    private String generateEnumDef(EnumDef ed) {
        var sb = new JavaSourceBuilder();
        sb.line("package " + config.enumsPackage() + ";");
        sb.line();

        sb.line("/**");
        if (ed.tooltip() != null) {
            sb.line(" * " + ed.tooltip());
        } else {
            sb.line(" * User-defined enum: " + ed.name());
        }
        sb.line(" */");
        sb.openBlock("public enum " + TypeMapper.capitalize(ed.name()));

        for (int i = 0; i < ed.values().size(); i++) {
            var v = ed.values().get(i);
            var suffix = i < ed.values().size() - 1 ? "," : ";";
            var enumConst = v.value().toUpperCase().replace(' ', '_').replace('-', '_');
            if (v.display() != null) {
                sb.line(enumConst + "(\"" + v.display() + "\")" + suffix);
            } else {
                sb.line(enumConst + "(\"" + v.value() + "\")" + suffix);
            }
        }

        sb.line();
        sb.line("private final String display;");
        sb.line();
        sb.openBlock(TypeMapper.capitalize(ed.name()) + "(String display)");
        sb.line("this.display = display;");
        sb.closeBlock();
        sb.line();
        sb.openBlock("public String getDisplay()");
        sb.line("return display;");
        sb.closeBlock();
        sb.line();
        sb.line("@Override");
        sb.openBlock("public String toString()");
        sb.line("return display;");
        sb.closeBlock();

        sb.closeBlock(); // enum
        return sb.toString();
    }

    private String mapType(org.lara.langspec2.types.JpDataType type) {
        return TypeMapper.toJavaType(type, "Object", name -> TypeMapper.capitalize(name));
    }
}
