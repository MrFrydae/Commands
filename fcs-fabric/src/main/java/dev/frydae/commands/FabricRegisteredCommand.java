package dev.frydae.commands;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FabricRegisteredCommand extends RegisteredCommand {
    private final String[] permissions;

    public FabricRegisteredCommand(@NotNull BaseCommand instance, RegisteredCommand parent, @NotNull Class<?> baseClass, Method method, @NotNull String name, @NotNull String description, List<CommandParameter> parameters, String[] permissions) {
        super(instance, parent, baseClass, method, name, description, parameters.stream().map(FabricCommandParameter::new).collect(Collectors.toList()));

        this.permissions = permissions;

    }

    public @NotNull FabricBaseCommand getInstance() {
        return (FabricBaseCommand) super.getInstance();
    }

    public FabricRegisteredCommand getParent() {
        return (FabricRegisteredCommand) super.getParent();
    }

    /**
     * Finds a {@link FabricCommandParameter} by name.
     *
     * @param name the name of the parameter
     * @return a {@link FabricCommandParameter} if found, null otherwise
     */
    public FabricCommandParameter getParameter(String name) {
        CommandParameter parameter = super.getParameter(name);

        if (parameter != null) {
            return new FabricCommandParameter(parameter);
        }

        return null;
    }

    public boolean hasPermissions() {
        return permissions != null && permissions.length > 0;
    }

    public String[] getPermissions() {
        if (getParent() != null) {
            return Arrays.stream(ArrayUtils.addAll(getParent().getPermissions(), permissions)).distinct().toArray(String[]::new);
        }

        return permissions;
    }
}
