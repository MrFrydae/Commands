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
                    String methodPermission = CommandManager.getAnnotations().getAnnotationValue(cmd.getMethod(), CommandPermission.class, null);

                    String permission = methodPermission != null ? methodPermission : CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null);

                    for (String alias : cmd.getAliases()) {
                        FabricRegisteredCommand command = new FabricRegisteredCommand(
                                cmd.getInstance(),
                                cmd.getParent(),
                                cmd.getBaseClass(),
                                cmd.getMethod(),
                                alias,
                                cmd.getDescription(),
                                cmd.getParameters(),
                                permission
                        );

                        FabricCommandManager.getRootCommands().add(command);
                    }
                });
    }

    static void registerSubcommands(FabricBaseCommand baseCommand) {
        Class<? extends FabricBaseCommand> cmdClass = baseCommand.getClass();

        RegisteredCommand parentCommand = collectSubcommands(baseCommand);

        if (parentCommand != null) {
            String cmdPerm = CommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null);

            for (String alias : parentCommand.getAliases()) {
                FabricRegisteredCommand parent = new FabricRegisteredCommand(parentCommand.getInstance(), parentCommand.getParent(), parentCommand.getBaseClass(), parentCommand.getMethod(), alias, parentCommand.getDescription(), parentCommand.getParameters(), cmdPerm);

                for (RegisteredCommand subcommand : parentCommand.getSubcommands()) {
                    String subPerm = CommandManager.getAnnotations().getAnnotationValue(subcommand.getMethod(), CommandPermission.class, null);

                    List<CommandParameter> subParams = collectMethodParameters(subcommand.getMethod());

                    for (String subcommandAlias : subcommand.getAliases()) {
                        parent.addSubcommand(new FabricRegisteredCommand(parent.getInstance(), parent, parent.getBaseClass(), subcommand.getMethod(), subcommandAlias, subcommand.getDescription(), subParams, subPerm));
                    }
                }

                FabricCommandManager.getRootCommands().add(parent);
            }
        }
    }
}
