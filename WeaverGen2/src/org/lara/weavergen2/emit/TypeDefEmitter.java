package org.lara.weavergen2.emit;

import org.lara.langspec2.model.TypeDef;
import org.lara.langspec2.model.WeaverModel;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;

public final class TypeDefEmitter {

    private final WeaverModel model;
    private final GenerationProfile config;
    private final EntityTypeSupport types;

    public TypeDefEmitter(WeaverModel model, GenerationProfile config) {
        this.model = model;
        this.config = config;
        this.types = new EntityTypeSupport(config);
    }

    public String emit(TypeDef td) {
        var sb = new JavaSourceBuilder();
        sb.line("package " + config.entitiesPackage() + ";");
        sb.line();
        sb.line("import java.util.*;");
        sb.line();

        var usesJoinpoint = td.fields().stream().anyMatch(field -> types.containsJoinpointRef(field.type()));
        var usesEnum = td.fields().stream().anyMatch(field -> types.containsEnumRef(field.type()));

        if (usesJoinpoint) {
            sb.line("import " + config.joinPointPackage() + ".*;");
        }
        if (usesEnum && !model.getEnumDefs().isEmpty()) {
            sb.line("import " + config.enumsPackage() + ".*;");
        }
        if (usesJoinpoint || usesEnum) {
            sb.line();
        }

        sb.line("/**");
        if (td.tooltip() != null) {
            sb.line(" * " + td.tooltip());
        } else {
            sb.line(" * User-defined type: " + td.name());
        }
        sb.line(" */");
        sb.openBlock("public class " + TypeMapper.capitalize(td.name()));

        for (var field : td.fields()) {
            var javaType = types.mapType(field.type());
            sb.line("private " + javaType + " " + field.name() + ";");
        }
        sb.line();

        for (var field : td.fields()) {
            var javaType = types.mapType(field.type());
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
}
