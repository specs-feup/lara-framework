package org.lara.weavergen2.java;

/**
 * Utility for building Java source code strings with proper indentation and formatting.
 */
public final class JavaSourceBuilder {

    private final StringBuilder sb = new StringBuilder();
    private int indent = 0;
    private static final String INDENT_UNIT = "    ";

    public JavaSourceBuilder line(String text) {
        sb.append(indentStr()).append(text).append("\n");
        return this;
    }

    public JavaSourceBuilder line() {
        sb.append("\n");
        return this;
    }

    public JavaSourceBuilder append(String text) {
        sb.append(text);
        return this;
    }

    public JavaSourceBuilder openBlock(String header) {
        sb.append(indentStr()).append(header).append(" {\n");
        indent++;
        return this;
    }

    public JavaSourceBuilder closeBlock() {
        indent--;
        sb.append(indentStr()).append("}\n");
        return this;
    }

    public JavaSourceBuilder closeBlockNoNewline() {
        indent--;
        sb.append(indentStr()).append("}");
        return this;
    }

    public JavaSourceBuilder indent() {
        indent++;
        return this;
    }

    public JavaSourceBuilder dedent() {
        indent--;
        return this;
    }

    public String indentStr() {
        return INDENT_UNIT.repeat(indent);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
