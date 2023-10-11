package dev.frydae.commands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.Nullable;

public abstract class CommandConditions<CEC extends CommandExecutionContext> {
    private final Table<Class<?>, String, Condition<?, CEC>> conditions;

    /**
     * Creates a new command conditions instance.
     */
    public CommandConditions() {
        conditions = HashBasedTable.create();

        addConditions();
    }

    public abstract void addConditions();

    /**
     * Adds a condition to the manager.
     *
     * @param clazz the class of the condition
     * @param id the id of the condition
     * @param condition the condition
     * @param <P> the type of the condition
     */
    public <P> void addCondition(Class<P> clazz, String id, Condition<P, CEC> condition) {
        conditions.put(clazz, id.toLowerCase(), condition);
    }

    /**
     * Gets a condition from the manager.
     *
     * @param clazz the class of the condition
     * @param id the id of the condition
     * @return the condition
     */
    @Nullable
    public Condition<?, CEC> getCondition(Class<?> clazz, String id) {
        return conditions.get(clazz, id.toLowerCase());
    }

    /**
     * Validates that the input type matches certain criteria.
     *
     * @param <T> the type to validate
     */
    @FunctionalInterface
    public interface Condition<T, CEC extends CommandExecutionContext> {
        void validate(CommandOptionContext context, CEC executionContext, T value) throws IllegalCommandException;
    }
}
