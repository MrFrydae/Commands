package dev.frydae.commands;

import com.google.common.collect.Maps;

import java.util.Map;

public class CommandContexts<R extends CommandExecutionContext> {
    private final Map<Class<?>, ContextResolver<?, R>> contextMap;

    public CommandContexts() {
        contextMap = Maps.newHashMap();
    }

    /**
     * Registers a new context for a specific type.
     *
     * @param clazz the class to register
     * @param handler the handler to register
     * @param <T> the type to register
     */
    public <T> void registerContext(Class<T> clazz, ContextResolver<T, R> handler) {
        contextMap.put(clazz, handler);
    }

    /**
     * Gets the context resolver for a specific class.
     *
     * @param clazz the class to get the resolver for
     * @return the context resolver for the specified class
     */
    public ContextResolver<?, R> getResolver(Class<?> clazz) {
        return contextMap.get(clazz);
    }
}
