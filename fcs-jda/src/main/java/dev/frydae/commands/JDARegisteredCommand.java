package dev.frydae.commands;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class JDARegisteredCommand extends RegisteredCommand {
    private final boolean global;
    private final Permission[] permissions;

    public JDARegisteredCommand(@NotNull BaseCommand instance, RegisteredCommand parent, @NotNull Class<?> baseClass, Method method, @NotNull String name, @NotNull String description, List<CommandParameter> parameters, boolean global, Permission[] permissions) {
        super(instance, parent, baseClass, method, name, description, parameters.stream().map(JDACommandParameter::new).collect(Collectors.toList()));

        this.global = global;
        this.permissions = permissions;
    }

    //<region Child overrides>
    @Override
    public @NotNull JDABaseCommand getInstance() {
        return (JDABaseCommand) super.getInstance();
    }
    //<endregion>

    /**
     * Finds a {@link JDACommandParameter} by name.
     *
     * @param name the name of the parameter
     * @return a {@link JDACommandParameter} if found, null otherwise
     */
    public JDACommandParameter getParameter(String name) {
        CommandParameter parameter = super.getParameter(name);

        if (parameter != null) {
            return new JDACommandParameter(parameter);
        }

        return null;
    }

    public boolean hasPermissions() {
        return permissions != null;
    }
}
