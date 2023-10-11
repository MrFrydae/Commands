package dev.frydae.commands;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommandCompletions<T> {
    private final Map<String, Function<CommandOptionContext, List<T>>> resolverMap;

    CommandCompletions() {
        resolverMap = Maps.newHashMap();
    }

    /**
     * Registers a new completion resolver.
     *
     * @param input the input to search for
     * @param resolver the resolver to register
     */
    public void registerCompletion(String input, Function<CommandOptionContext, List<T>> resolver) {
        resolverMap.put(input, resolver);
    }

    /**
     * Gets the resolver for a given input.
     *
     * @param input the input to search for
     * @return the resolver for this input
     */
    private Function<CommandOptionContext, List<T>> getResolver(String input) {
        return resolverMap.get(input);
    }

    /**
     * Finds completion choices for a key.
     *
     * @param key the key to search for
     * @return a list of {@link T types} for this key
     */
    @Nullable
    public List<T> getCompletions(String key) {
        String[] inputSplit = key.split("\\|");

        String input = inputSplit[0];

        Function<CommandOptionContext, List<T>> resolver = getResolver(input);

        if (resolver == null) {
            return null;
        }

        CommandOptionContext commandOptionContext = new CommandOptionContext(key);

        return resolver.apply(commandOptionContext);
    }
}
