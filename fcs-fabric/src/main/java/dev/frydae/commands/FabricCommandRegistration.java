package dev.frydae.commands;

import dev.frydae.commands.annotations.CommandPermission;
import dev.frydae.commands.annotations.Default;

import java.lang.reflect.Method;
import java.util.List;

public class FabricCommandRegistration extends CommandRegistration {
    static void registerCommandAliases(FabricBaseCommand baseCommand) {
        Class<? extends FabricBaseCommand> cmdClass = baseCommand.getClass();

        collectCommandAliases(baseCommand)
                .forEach(cmd -> {
                    for (String alias : cmd.getAliases()) {
                        FabricRegisteredCommand command = new FabricRegisteredCommand(
                                cmd.getInstance(),
                                cmd.getParent(),
                                cmd.getBaseClass(),
                                cmd.getMethod(),
                                alias,
                                cmd.getDescription(),
                                cmd.getParameters(),
                                CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null)
                        );

                        FabricCommandManager.getRootCommands().add(command);
                    }
                });
    }

    static void registerSubcommands(FabricBaseCommand baseCommand) {
        Class<? extends FabricBaseCommand> cmdClass = baseCommand.getClass();

        RegisteredCommand parentCommand = collectSubcommands(baseCommand);

        if (parentCommand != null) {
            String[] cmdPerms = CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null);

            for (String alias : parentCommand.getAliases()) {
                FabricRegisteredCommand parent = new FabricRegisteredCommand(parentCommand.getInstance(), parentCommand.getParent(), parentCommand.getBaseClass(), parentCommand.getMethod(), alias, parentCommand.getDescription(), parentCommand.getParameters(), cmdPerms);

                for (RegisteredCommand subcommand : parentCommand.getSubcommands()) {
                    String[] subPerms = CommandManager.getAnnotations().getAnnotationValue(subcommand.getMethod(), CommandPermission.class, null);

                    List<CommandParameter> subParams = collectMethodParameters(subcommand.getMethod());

                    for (String subcommandAlias : subcommand.getAliases()) {
                        parent.addSubcommand(new FabricRegisteredCommand(parent.getInstance(), parent, parent.getBaseClass(), subcommand.getMethod(), subcommandAlias, subcommand.getDescription(), subParams, subPerms));
                    }
                }

                FabricCommandManager.getRootCommands().add(parent);
            }
        }
    }
}
