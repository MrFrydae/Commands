package dev.frydae.commands;


import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

@Data
public class RegisteredCommand {
    @NotNull private final BaseCommand instance;
    private final RegisteredCommand parent;
    @NotNull private final Class<?> baseClass;
    private final Method method;
    @NotNull private final String name;
    @NotNull private final String description;
    private final List<CommandParameter> parameters;
    private final List<RegisteredCommand> subcommands = Lists.newCopyOnWriteArrayList();

    /**
     * Prepends the parent's name if one exists.
     *
     * @return the full name
     */
    @NotNull
    public String getFullName() {
        if (parent != null) {
            return parent.getName() + " " + getName();
        }

        return getName();
    }

    public void addSubcommand(RegisteredCommand subcommand) {
        subcommands.add(subcommand);
    }

    public boolean hasSubcommands() {
        return !subcommands.isEmpty();
    }

    /**
     * Finds a {@link CommandParameter} by name.
     *
     * @param name the name of the parameter
     * @return a {@link CommandParameter} if found, null otherwise
     */
    public CommandParameter getParameter(String name) {
        return getParameters().stream()
                .filter(p -> p.getName() != null)
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
