package dev.frydae.commands;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;

public final class JDARegisteredCommand extends RegisteredCommand {
    @Getter private final boolean global;
    @Getter private final Permission[] permissions;

    public JDARegisteredCommand(@NotNull JDABaseCommand instance, JDARegisteredCommand parent, @NotNull Class<?> baseClass, Method method, @NotNull String name, @NotNull String description, JDACommandParameter[] parameters, boolean global, Permission[] permissions) {
        super(instance, parent, baseClass, method, name, description, parameters);
        this.global = global;
        this.permissions = permissions;
    }

    //<region Child overrides>
    @Override
    public @NotNull JDABaseCommand getInstance() {
        return (JDABaseCommand) super.getInstance();
    }

    @Override
    public JDACommandParameter[] getParameters() {
        return (JDACommandParameter[]) super.getParameters();
    }

    //<endregion>

    /**
     * Finds a {@link JDACommandParameter} by name.
     *
     * @param name the name of the parameter
     * @return a {@link JDACommandParameter} if found, null otherwise
     */
    public JDACommandParameter getParameter(String name) {
        return Arrays.stream(getParameters())
                .filter(p -> p.getName() != null)
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public boolean hasPermissions() {
        return permissions != null;
    }
}