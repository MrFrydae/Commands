package dev.frydae.commands;

import com.google.common.collect.Lists;
import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.CommandPermission;
import dev.frydae.commands.annotations.Completion;
import dev.frydae.commands.annotations.Condition;
import dev.frydae.commands.annotations.Default;
import dev.frydae.commands.annotations.Description;
import dev.frydae.commands.annotations.Disabled;
import dev.frydae.commands.annotations.GlobalCommand;
import dev.frydae.commands.annotations.Name;
import dev.frydae.commands.annotations.Optional;
import dev.frydae.commands.annotations.Subcommand;
import net.dv8tion.jda.api.Permission;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JDACommandRegistration extends CommandRegistration {
    /**
     * Registers command aliases for a given base command.
     *
     * @param baseCommand The base command to register.
     */
    static void registerCommandAliases(JDABaseCommand baseCommand) {
        Class<? extends JDABaseCommand> cmdClass = baseCommand.getClass();

        Permission[] classPerms = JDACommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, new Permission[]{});

        collectCommandAliases(baseCommand)
                .stream()
                .map(cmd -> {
                    Method method = cmd.getMethod();

                    Permission[] methodPerms = JDACommandManager.getAnnotations().getAnnotationValue(method, CommandPermission.class, new Permission[]{});
                    Permission[] mergedPerms = Stream.concat(Arrays.stream(classPerms), Arrays.stream(methodPerms)).distinct().toArray(Permission[]::new);

                    boolean global = method.isAnnotationPresent(GlobalCommand.class);

                    return new JDARegisteredCommand(
                            cmd.getInstance(),
                            cmd.getParent(),
                            cmd.getBaseClass(),
                            cmd.getMethod(),
                            cmd.getName(),
                            cmd.getDescription(),
                            cmd.getParameters(),
                            global,
                            mergedPerms
                    );
                })
                .forEach(command -> JDACommandManager.getRootCommands().add(command));
    }

    /**
     * Registers subcommands for a given base command.
     *
     * @param baseCommand The base command to register.
     */
    static void registerSubCommands(JDABaseCommand baseCommand) {
        Class<? extends JDABaseCommand> cmdClass = baseCommand.getClass();

        RegisteredCommand parentCommand = collectSubcommands(baseCommand);

        if (parentCommand != null) {
            boolean global = cmdClass.isAnnotationPresent(GlobalCommand.class);
            Permission[] permissions = JDACommandManager.getAnnotations().getAnnotationValue(cmdClass, CommandPermission.class, null);

            JDARegisteredCommand parent = new JDARegisteredCommand(parentCommand.getInstance(), parentCommand.getParent(), parentCommand.getBaseClass(), parentCommand.getMethod(), parentCommand.getName(), parentCommand.getDescription(), parentCommand.getParameters(), global, permissions);

            for (RegisteredCommand subcommand : parentCommand.getSubcommands()) {
                Permission[] subPerms = JDACommandManager.getAnnotations().getAnnotationValue(subcommand.getMethod(), CommandPermission.class, null);

                List<CommandParameter> subParams = collectMethodParameters(subcommand.getMethod());

                parent.addSubcommand(new JDARegisteredCommand(parent.getInstance(), parent, parent.getBaseClass(), subcommand.getMethod(), subcommand.getName(), subcommand.getDescription(), subParams, global, subPerms));
            }

            JDACommandManager.getRootCommands().add(parent);
        }
    }
}
