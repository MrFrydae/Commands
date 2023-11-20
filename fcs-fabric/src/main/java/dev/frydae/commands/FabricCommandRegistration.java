package dev.frydae.commands;

import dev.frydae.commands.annotations.CommandPermission;
import dev.frydae.commands.annotations.Default;

import java.lang.reflect.Method;
import java.util.List;

public class FabricCommandRegistration extends CommandRegistration {
    static void registerCommandAliases(FabricBaseCommand baseCommand) {
        Class<? extends FabricBaseCommand> cmdClass = baseCommand.getClass();

        collectCommandAliases(baseCommand)
                .stream()
                .map(cmd -> {
                    Method method = cmd.getMethod();

                    return new FabricRegisteredCommand(
                            cmd.getInstance(),
                            cmd.getParent(),
                            cmd.getBaseClass(),
                            cmd.getMethod(),
                            cmd.getName(),
                            cmd.getDescription(),
                            cmd.getParameters(),
                            CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null)
                    );
                })
                .forEach(command -> FabricCommandManager.getRootCommands().add(command));
    }

    static void registerSubcommands(FabricBaseCommand baseCommand) {
        Class<? extends FabricBaseCommand> cmdClass = baseCommand.getClass();

        RegisteredCommand parentCommand = collectSubcommands(baseCommand);

        if (parentCommand != null) {
            String[] cmdPerms = CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null);

            FabricRegisteredCommand parent = new FabricRegisteredCommand(parentCommand.getInstance(), parentCommand.getParent(), parentCommand.getBaseClass(), parentCommand.getMethod(), parentCommand.getName(), parentCommand.getDescription(), parentCommand.getParameters(), cmdPerms);

            for (RegisteredCommand subcommand : parentCommand.getSubcommands()) {
                String[] subPerms = CommandManager.getAnnotations().getAnnotationValue(subcommand.getMethod(), CommandPermission.class, null);

                List<CommandParameter> subParams = collectMethodParameters(subcommand.getMethod());

                for (CommandParameter subParam : subParams) {
                    if (subParam.hasCompletion()) {
                        if (FabricCommandManager.getSingleton().getCommandCompletions().getCompletions(subParam.getCompletion()) == null) {
                            System.err.printf("Parameter [%s] registered in command [%s] requested missing completion [%s]\n", subParam.getName(), subcommand.getFullName(), subParam.getCompletion());

                            System.exit(1);
                        }
                    }
                }

                parent.addSubcommand(new FabricRegisteredCommand(parent.getInstance(), parent, parent.getBaseClass(), subcommand.getMethod(), subcommand.getName(), subcommand.getDescription(), subParams, subPerms));
            }

            FabricCommandManager.getRootCommands().add(parent);
        }
    }
}
