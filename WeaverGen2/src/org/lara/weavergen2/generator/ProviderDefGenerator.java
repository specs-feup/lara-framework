package org.lara.weavergen2.generator;

import java.util.function.Function;

import org.lara.langspec2.model.*;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;

/**
 * Generates provider definition interfaces for each join point class that declares own attributes or actions.
 */
public final class ProviderDefGenerator {

    private final JpClass jpClass;
    private final WeaverModel model;
    private final GeneratorConfig config;

    public ProviderDefGenerator(JpClass jpClass, WeaverModel model, GeneratorConfig config) {
        this.jpClass = jpClass;
        this.model = model;
        this.config = config;
    }

    /**
     * Returns true if this JP class has own attributes or actions and thus needs a provider interface.
     */
    public boolean hasContent() {
        return !jpClass.getOwnAttributes().isEmpty() || !jpClass.getOwnActions().isEmpty();
    }

    public String generate() {
        var sb = new JavaSourceBuilder();

        sb.line("package " + config.providerPackage() + ";");
        sb.line();

        // Imports
        sb.line("import " + config.joinPointPackage() + ".*;");
        if (!model.getTypeDefs().isEmpty()) {
            sb.line("import " + config.entitiesPackage() + ".*;");
        }
        if (!model.getEnumDefs().isEmpty()) {
            sb.line("import " + config.enumsPackage() + ".*;");
        }
        sb.line();

        var interfaceName = TypeMapper.providerDefName(jpClass.getName());
        var jpClassName = TypeMapper.abstractClassName(jpClass.getName());

        sb.line("/**");
        sb.line(" * Provider definition interface for the {@code " + jpClass.getName() + "} join point level.");
        sb.line(" * <p>");
        sb.line(" * Implement this interface to provide behavior for inherited " + jpClass.getName() + "-level");
        sb.line(" * attributes and actions when accessed from child join point types.");
        sb.line(" */");
        sb.openBlock("public interface " + interfaceName + "<JP extends " + jpClassName + "<?>>");

        // Own attributes
        for (var attr : jpClass.getOwnAttributes()) {
            var javaRetType = mapType(attr.type());
            var methodName = "get" + TypeMapper.capitalize(attr.name());

            if (attr.parameters().isEmpty()) {
                sb.openBlock("default " + javaRetType + " " + methodName + "Impl(JP _jp)");
            } else {
                var params = formatParams(attr.parameters());
                sb.openBlock("default " + javaRetType + " " + methodName + "Impl(JP _jp, " + params + ")");
            }
            sb.line("throw new UnsupportedOperationException(\"" + jpClass.getName() + ": " + attr.name() + " not implemented\");");
            sb.closeBlock();
            sb.line();
        }

        // Own actions
        for (var action : jpClass.getOwnActions()) {
            var javaRetType = mapType(action.returnType());
            var methodName = action.name();
            var params = formatParams(action.parameters());
            var fullParams = params.isEmpty() ? "JP _jp" : "JP _jp, " + params;

            sb.openBlock("default " + javaRetType + " " + methodName + "Impl(" + fullParams + ")");
            sb.line("throw new UnsupportedOperationException(\"" + jpClass.getName() + ": " + action.name() + " not implemented\");");
            sb.closeBlock();
            sb.line();
        }

        sb.closeBlock();

        return sb.toString();
    }

    private String mapType(org.lara.langspec2.types.JpDataType type) {
        Function<String, String> jpMapper = name -> {
            if (name.equals("joinpoint") || name.equals(model.getGlobal().getName())) {
                return TypeMapper.abstractClassName(model.getGlobal().getName()) + "<?>";
            }
            return TypeMapper.abstractClassName(name) + "<?>";
        };

        return TypeMapper.toJavaType(
                type,
                "JP",
                jpMapper,
                TypeMapper::capitalize,
                TypeMapper::capitalize
        );
    }

    private String formatParams(java.util.List<Parameter> params) {
        return params.stream()
                .map(p -> mapType(p.type()) + " " + p.name())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}
