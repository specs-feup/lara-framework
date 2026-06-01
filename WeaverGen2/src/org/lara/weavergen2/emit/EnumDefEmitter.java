package org.lara.weavergen2.emit;

import org.lara.langspec2.model.EnumDef;
import org.lara.langspec2.model.EnumValue;
import org.lara.weavergen2.model.GenerationProfile;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;

public final class EnumDefEmitter {

    private final GenerationProfile config;

    public EnumDefEmitter(GenerationProfile config) {
        this.config = config;
    }

    public String emit(EnumDef ed) {
        var sb = new JavaSourceBuilder();
        sb.line("package " + config.enumsPackage() + ";");
        sb.line();
        sb.line("import java.util.Objects;");
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
        sb.line();

        addFromDisplayMethod(sb, ed);

        sb.closeBlock();
        return sb.toString();
    }

    private void addFromDisplayMethod(JavaSourceBuilder sb, EnumDef ed) {
        var enumName = TypeMapper.capitalize(ed.name());
        var validValues = ed.values().stream()
                .map(this::effectiveEnumDisplay)
                .map(EnumDefEmitter::escapeJavaString)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");

        sb.openBlock("public static " + enumName + " fromDisplay(String display)");
        sb.openBlock("for (" + enumName + " value : values())");
        sb.openBlock("if (Objects.equals(value.getDisplay(), display) || (display != null && value.name().equalsIgnoreCase(display)))");
        sb.line("return value;");
        sb.closeBlock();
        sb.closeBlock();
        sb.line("throw new IllegalArgumentException(\"Unknown value for enum " + enumName + ": \" + display"
                + " + \". Expected one of: " + validValues + "\");");
        sb.closeBlock();
    }

    private String effectiveEnumDisplay(EnumValue value) {
        return value.display() != null ? value.display() : value.value();
    }

    private static String escapeJavaString(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
