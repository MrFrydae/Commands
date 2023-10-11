package dev.frydae.commands;

/**
 * Converts information from incoming data to a custom structure.
 *
 * @param <T> the type to resolve
 * @param <R> anonymous function to resolve the input
 */
@FunctionalInterface
public interface ContextResolver<T, R extends CommandExecutionContext> {
    T resolve(R context) throws IllegalCommandException;
}
