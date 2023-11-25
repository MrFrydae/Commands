package dev.frydae.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.frydae.accessmanager.PermissionStore;
import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class FabricCommandManager extends CommandManager {
    private static FabricCommandManager singleton;
    @Getter private final FabricCommandContexts commandContexts;
    @Getter private final FabricCommandCompletions commandCompletions;
    @Getter private final FabricCommandConditions commandConditions;

    private FabricCommandManager() {
        this.commandContexts = new FabricCommandContexts();
        this.commandCompletions = new FabricCommandCompletions();
        this.commandConditions = new FabricCommandConditions();
    }

    public static FabricCommandManager getSingleton() {
        if (singleton == null) {
            singleton = new FabricCommandManager();
        }

        return singleton;
    }

    public static void registerCommand(FabricBaseCommand baseCommand) {
        FabricCommandRegistration.registerCommandAliases(baseCommand);
        FabricCommandRegistration.registerSubcommands(baseCommand);
    }

    private static boolean checkPermissions(ServerCommandSource source, String permission) {
        boolean permsModFound = FabricLoader.getInstance().getAllMods().stream().anyMatch(m -> m.getMetadata().getId().equalsIgnoreCase("access-manager"));

        if (permsModFound) {
            return PermissionStore.hasPermission(Objects.requireNonNull(source.getPlayer()), permission);
        } else {
            return source.hasPermissionLevel(1);
        }
    }

    public static void upsertCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                getRootCommands().stream()
                        .map(cmd -> (FabricRegisteredCommand) cmd)
                        .forEach(command -> upsertCommand(dispatcher, command))
        );
    }

    private static void upsertCommand(CommandDispatcher<ServerCommandSource> dispatcher, FabricRegisteredCommand command) {
        for (String alias : command.getAliases()) {
            LiteralArgumentBuilder<ServerCommandSource> builder = literal(alias);

            if (command.hasSubcommands()) {
                if (command.getMethod() != null) {
                    builder.executes(c -> executeCommand(command, c));
                }

                builder = setupSubcommands(command, builder);
            } else {
                builder = collectCommandArguments(alias, command, c -> executeCommand(command, c));

                if (command.hasPermission()) {
                    builder = builder.requires(p -> checkPermissions(p, command.getPermission()));
                }
            }

            dispatcher.register(builder);
        }
    }

    private static LiteralArgumentBuilder<ServerCommandSource> setupSubcommands(FabricRegisteredCommand command, LiteralArgumentBuilder<ServerCommandSource> builder) {
        for (RegisteredCommand scmd : command.getSubcommands()) {
            FabricRegisteredCommand subcommand = (FabricRegisteredCommand) scmd;

            for (String alias : subcommand.getAliases()) {
                LiteralArgumentBuilder<ServerCommandSource> subBuilder = collectCommandArguments(alias, subcommand, c -> executeCommand(subcommand, c));

                if (subcommand.hasPermission()) {
                    subBuilder = subBuilder.requires(p -> checkPermissions(p, subcommand.getPermission()));
                }

                builder = builder.then(subBuilder);
            }
        }

        builder = builder.requires(p -> command.getSubcommands().stream()
                .map(s -> (FabricRegisteredCommand) s)
                .anyMatch(scmd -> checkPermissions(p, scmd.getPermission())));

        return builder;
    }

    private static LiteralArgumentBuilder<ServerCommandSource> collectCommandArguments(String alias, FabricRegisteredCommand command, Command<ServerCommandSource> executor) {
        LiteralArgumentBuilder<ServerCommandSource> subBuilder = literal(alias);

        RequiredArgumentBuilder<ServerCommandSource, String> argumentBuilder = null;

        for (CommandParameter param : CommandUtils.reverseList(command.getParameters())) {
            FabricCommandParameter commandParameter = (FabricCommandParameter) param;

            RequiredArgumentBuilder<ServerCommandSource, String> argument = argument(commandParameter.getName(), StringArgumentType.word());

            argument = setupParameterCompletions(commandParameter, argument);

            if (argumentBuilder == null) {
                argumentBuilder = argument.executes(executor);
            } else {
                argumentBuilder = argument.then(argumentBuilder);
            }
        }

        if (argumentBuilder == null) {
            return subBuilder.executes(executor);
        } else {
            return subBuilder.then(argumentBuilder);
        }
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> setupParameterCompletions(FabricCommandParameter commandParameter, RequiredArgumentBuilder<ServerCommandSource, String> argument) {
        FabricCommandCompletions commandCompletions = FabricCommandManager.getSingleton().getCommandCompletions();

        if (commandParameter.hasCompletion()) {
            argument = argument.suggests((context, builder) -> {
                Objects.requireNonNull(commandCompletions.getCompletions(commandParameter.getCompletion(), context)).forEach(builder::suggest);

                return builder.buildFuture();
            });
        } else if (commandParameter.hasValues()) {
            argument = argument.suggests((context, builder) -> {
                Objects.requireNonNull(Arrays.stream(commandParameter.getValues().split("\\|"))).forEach(builder::suggest);

                return builder.buildFuture();
            });
        }

        return argument;
    }

    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    private static int executeCommand(FabricRegisteredCommand command, CommandContext<ServerCommandSource> context) {
        try {
            if (command.hasPermission() && !checkPermissions(context.getSource(), command.getPermission())) {
                throw new IllegalCommandException("You do not have permission for this command.");
            }

            command.getInstance().setContext(context);

            command.getMethod().invoke(command.getInstance(), resolveArgs(command, context));
        } catch (IllegalCommandException e) {
            context.getSource().sendMessage(Text.literal(e.getMessage()).formatted(Formatting.RED));
        }

        return 1;
    }

    private static Object[] resolveArgs(FabricRegisteredCommand command, CommandContext<ServerCommandSource> context) throws IllegalCommandException {
        List<Object> objects = Lists.newArrayList();

        command.getParameters().stream()
                .map(FabricCommandParameter::new)
                .forEach(parameter -> objects.add(resolveParameter(command, context, parameter)));

        return objects.toArray();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SneakyThrows(IllegalCommandException.class)
    private static Object resolveParameter(FabricRegisteredCommand command, CommandContext<ServerCommandSource> context, FabricCommandParameter parameter) {
        FabricCommandExecutionContext cec = new FabricCommandExecutionContext(command, parameter, context.getArgument(parameter.getName(), String.class), context);

        ContextResolver<?, FabricCommandExecutionContext> resolver = FabricCommandManager.getSingleton().getCommandContexts().getResolver(parameter.getParameter().getType());

        if (resolver == null) {
            return null;
        } else {
            Object resolve = resolver.resolve(cec);

            if (parameter.hasCondition()) {
                CommandOptionContext optionContext = new CommandOptionContext(parameter.getCondition());

                FabricCommandConditions.Condition condition = FabricCommandManager.getSingleton().getCommandConditions().getCondition(parameter.getParameter().getType(), optionContext.getKey());

                if (condition != null) {
                    condition.validate(optionContext, cec, resolve);
                }
            }

            return resolve;
        }
    }
}
