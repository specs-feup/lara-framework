package org.lara.langspec2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Runtime registry for provider lookup.
 * <p>
 * Providers are registered at weaver startup and resolved by join point instances
 * to dispatch inherited attribute/action implementations.
 */
public final class ProviderRegistry {

    private final Map<Class<?>, Object> providers = new HashMap<>();

    public <P> void register(Class<P> providerDef, P provider) {
        providers.put(providerDef, provider);
    }

    @SuppressWarnings("unchecked")
    public <P> P resolve(Class<P> providerDef) {
        P provider = (P) providers.get(providerDef);
        if (provider == null) {
            throw new IllegalStateException("No provider registered for " + providerDef.getName());
        }
        return provider;
    }

    public boolean hasProvider(Class<?> providerDef) {
        return providers.containsKey(providerDef);
    }
}
