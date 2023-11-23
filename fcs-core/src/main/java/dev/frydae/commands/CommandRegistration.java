package dev.frydae.commands;

import com.google.common.collect.Lists;
import dev.frydae.commands.annotations.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

public class CommandRegistration {
    protected static List<RegisteredCommand> collectCommandAliases(BaseCommand baseCommand) {
        Class<? extends BaseCommand> cmdClass = baseCommand.getClass();

        return Arrays.stream(cmdClass.getMethods())
                .filter(method -> method.isAnnotationPresent(CommandAlias.class))
                .filter(method -> !method.isAnnotationPresent(Disabled.class))
                .map(method -> {
                    String alias = CommandManager.getAnnotations().getAnnotationValue(method, CommandAlias.class);
                    String description = CommandManager.getAnnotations().getAnnotationValue(method, Description.class);

                    List<CommandParameter> commandParameters = collectMethodParameters(method);

                    return new RegisteredCommand(baseCommand, null, cmdClass, method, alias, description, commandParameters);
                })
                .toList();
    }

    @Nullable
    protected static RegisteredCommand collectSubcommands(BaseCommand baseCommand) {
        Class<? extends BaseCommand> cmdClass = baseCommand.getClass();

        if (cmdClass.isAnnotationPresent(CommandAlias.class) && !cmdClass.isAnnotationPresent(Disabled.class)) {
            String alias = CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandAlias.class);
            String description = CommandManager.getAnnotations().getAnnotationValue(cmdClass, Description.class);

            Method parentMethod = null;

            java.util.Optional<Method> first = Arrays.stream(cmdClass.getMethods()).filter(method -> method.isAnnotationPresent(Default.class)).findFirst();
            if (first.isPresent()) {
                parentMethod = first.get();
            }

            RegisteredCommand parent = new RegisteredCommand(baseCommand, null, cmdClass, parentMethod, alias.split("\\|")[0], description, Lists.newArrayList());

            Arrays.stream(cmdClass.getMethods())
                    .filter(method -> method.isAnnotationPresent(Subcommand.class))
                    .filter(method -> !method.isAnnotationPresent(Disabled.class))
                    .map(method -> {
                        String subName = CommandManager.getAnnotations().getAnnotationValue(method, Subcommand.class);
                        String subDesc = CommandManager.getAnnotations().getAnnotationValue(method, Description.class);

                        List<CommandParameter> subParams = collectMethodParameters(method);

                        return new RegisteredCommand(parent.getInstance(), parent, parent.getBaseClass(), method, subName, subDesc, subParams);
                    })
                    .forEach(parent::addSubcommand);

            return parent;
        }

        return null;
    }

    /**
     * Gets the parameters for a given method.
     *
     * @param method The method to get the parameters for.
     * @return A list of command parameters.
     */
    protected static List<CommandParameter> collectMethodParameters(Method method) {
        List<CommandParameter> commandParameters = Lists.newArrayList();

        for (Parameter parameter : method.getParameters()) {
            boolean optional = parameter.isAnnotationPresent(Optional.class) || parameter.isAnnotationPresent(Default.class);

            CommandParameter commandParameter = new CommandParameter(
                    parameter,
                    CommandManager.getAnnotations().getAnnotationValue(parameter, Name.class, parameter.getName()),
                    CommandManager.getAnnotations().getAnnotationValue(parameter, Description.class),
                    optional,
                    CommandManager.getAnnotations().getAnnotationValue(parameter, Default.class, null),
                    CommandManager.getAnnotations().getAnnotationValue(parameter, Completion.class, null),
                    CommandManager.getAnnotations().getAnnotationValue(parameter, Condition.class, null),
                    CommandManager.getAnnotations().getAnnotationValue(parameter, Values.class, null)
            );

            commandParameters.add(commandParameter);
        }

        return commandParameters;
    }
}
