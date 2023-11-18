package dev.frydae.commands;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class FabricRegisteredCommand extends RegisteredCommand {
    @Getter private final boolean permsExist;

    public FabricRegisteredCommand(@NotNull BaseCommand instance, RegisteredCommand parent, @NotNull Class<?> baseClass, Method method, @NotNull String name, @NotNull String description, List<CommandParameter> parameters, boolean permsExist) {
        super(instance, parent, baseClass, method, name, description, parameters.stream().map(FabricCommandParameter::new).collect(Collectors.toList()));

        this.permsExist = permsExist;
    }

    public @NotNull FabricBaseCommand getInstance() {
        return (FabricBaseCommand) super.getInstance();
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
        return permsExist;
    }
}
