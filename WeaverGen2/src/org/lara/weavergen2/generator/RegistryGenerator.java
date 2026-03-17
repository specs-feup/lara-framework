package org.lara.weavergen2.generator;

import org.lara.langspec2.model.*;
import org.lara.weavergen2.java.JavaSourceBuilder;

/**
 * Generates the {@code ProviderRegistry} class for runtime provider lookup.
 */
public final class RegistryGenerator {

    private final GeneratorConfig config;

    public RegistryGenerator(GeneratorConfig config) {
        this.config = config;
    }

    public String generate() {
        var sb = new JavaSourceBuilder();

        sb.line("package " + config.registryPackage() + ";");
        sb.line();
        sb.line("import java.util.HashMap;");
        sb.line("import java.util.Map;");
        sb.line();
        sb.line("/**");
        sb.line(" * Runtime registry for provider lookup.");
        sb.line(" * Providers are registered at weaver startup and resolved by join point instances.");
        sb.line(" */");
        sb.openBlock("public final class ProviderRegistry");
        sb.line();
        sb.line("private final Map<Class<?>, Object> providers = new HashMap<>();");
        sb.line();

        // register
        sb.openBlock("public <P> void register(Class<P> providerDef, P provider)");
        sb.line("providers.put(providerDef, provider);");
        sb.closeBlock();
        sb.line();

        // resolve
        sb.line("@SuppressWarnings(\"unchecked\")");
        sb.openBlock("public <P> P resolve(Class<P> providerDef)");
        sb.line("P provider = (P) providers.get(providerDef);");
        sb.openBlock("if (provider == null)");
        sb.line("throw new IllegalStateException(\"No provider registered for \" + providerDef.getName());");
        sb.closeBlock();
        sb.line("return provider;");
        sb.closeBlock();
        sb.line();

        // hasProvider
        sb.openBlock("public boolean hasProvider(Class<?> providerDef)");
        sb.line("return providers.containsKey(providerDef);");
        sb.closeBlock();

        sb.closeBlock(); // class

        return sb.toString();
    }
}
