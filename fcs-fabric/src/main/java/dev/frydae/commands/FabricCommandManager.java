package dev.frydae.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FabricCommandManager extends CommandManager {
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

    public static int giveDiamond(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();

        final PlayerEntity self = source.getPlayer(); // If not a player than the command ends

        if(!self.getInventory().insertStack(new ItemStack(Items.DIAMOND))){
            throw new SimpleCommandExceptionType(Text.translatable("inventory.isfull")).create();
        }

        return 1;
    }

    public static void upsertCommands() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("giveMeDiamond").executes(ctx -> giveDiamond(ctx)));

            for (RegisteredCommand cmd : getRootCommands()) {
                FabricRegisteredCommand command = (FabricRegisteredCommand) cmd;

                LiteralArgumentBuilder<ServerCommandSource> builder = literal(command.getName());

                if (command.hasSubcommands()) {
                    for (RegisteredCommand scmd : command.getSubcommands()) {
                        FabricRegisteredCommand subcommand = (FabricRegisteredCommand) scmd;

                        LiteralArgumentBuilder<ServerCommandSource> subBuilder = getServerCommandSourceLiteralArgumentBuilder(subcommand, c -> executeCommand(subcommand, c));

                        if (subcommand.hasPermissions()) {
                            subBuilder = subBuilder.requires(p -> p.hasPermissionLevel(1));
                        }

                        builder = builder.then(subBuilder);
                    }
                } else {
                    builder = getServerCommandSourceLiteralArgumentBuilder(command, c -> executeCommand(command, c));

                    if (command.hasPermissions()) {
                        builder = builder.requires(p -> p.hasPermissionLevel(1));
                    }
                }

                dispatcher.register(builder);
            }
        }));
    }

    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    private static int executeCommand(FabricRegisteredCommand command, CommandContext<ServerCommandSource> context) {
        command.getInstance().setContext(context);

        try {
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

    private static LiteralArgumentBuilder<ServerCommandSource> getServerCommandSourceLiteralArgumentBuilder(FabricRegisteredCommand command, Command<ServerCommandSource> executor) {
        FabricCommandCompletions commandCompletions = FabricCommandManager.getSingleton().getCommandCompletions();

        LiteralArgumentBuilder<ServerCommandSource> subBuilder = literal(command.getName());

        RequiredArgumentBuilder<ServerCommandSource, String> argumentBuilder = null;

        for (CommandParameter param : CommandUtils.reverseList(command.getParameters())) {
            FabricCommandParameter commandParameter = (FabricCommandParameter) param;

            RequiredArgumentBuilder<ServerCommandSource, String> argument = argument(commandParameter.getName(), StringArgumentType.word());

            if (commandParameter.hasCompletion()) {
                argument = argument.suggests(((context, builder) -> {
                    Objects.requireNonNull(commandCompletions.getCompletions(commandParameter.getCompletion())).forEach(builder::suggest);

                    return builder.buildFuture();
                }));
            } else if (commandParameter.hasValues()) {
                argument = argument.suggests(((context, builder) -> {
                    Objects.requireNonNull(Arrays.stream(commandParameter.getValues().split("\\|"))).forEach(builder::suggest);

                    return builder.buildFuture();
                }));
            }

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
}
