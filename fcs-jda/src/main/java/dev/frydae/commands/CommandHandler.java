package dev.frydae.commands;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class CommandHandler {
    /**
     * Processes an incoming {@link SlashCommandInteractionEvent}.
     *
     * @param event the {@link SlashCommandInteractionEvent} to process
     */
    public static void processSlashCommand(SlashCommandInteractionEvent event) {
        JDARegisteredCommand command = (JDARegisteredCommand) JDACommandManager.findRegisteredCommand(event.getFullCommandName());

        // This is so beyond impossible, that I cannot begin to fathom what would need to occur for this to be null.
        // The only thing that I can imagine that could cause the member to be null, is that the universe is indeed
        // a simulation and we are simply pawns in a much bigger, highly-convoluted plan. But the mere idea of the
        // entire universe being a simulation should certainly eliminate any possibility of the creators allowing
        // this variable to be null. Are we alone in the universe? Are there beings that predate our civilizations?
        // Perhaps we'll never know. The only thing we do know is that this should never be null. But then again,
        // something could've just broken with the Java Discord API that caused the member to be null. Or the command
        // was sent from a private message. That would do it too. Sorry for the rant.
        if (event.getMember() == null) {
            return;
        }

        // This could actually happen. This just means we never registered the command
        if (command == null) {
            return;
        }

        try {
            if (command.hasPermissions()) {
                if (!event.getMember().hasPermission(command.getPermissions())) {
                    throw new IllegalCommandException("You do not have permission to run this command");
                }
            }

            command.getInstance().setEvent(event);

            command.getMethod().invoke(command.getInstance(), resolveArgs(event, command));

            command.getInstance().tearDown();
        } catch (IllegalAccessException | InvocationTargetException e) {
            JDACommandManager.getLogger().error(ExceptionUtils.getStackTrace(e));
        } catch (IllegalCommandException e) {
            event.getInteraction().reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    /**
     * Resolves the arguments for a {@link JDARegisteredCommand}.
     *
     * @param event the {@link SlashCommandInteractionEvent} to process
     * @param command the {@link JDARegisteredCommand} to process
     * @return the resolved arguments
     * @throws IllegalCommandException if something goes wrong
     */
    private static Object[] resolveArgs(SlashCommandInteractionEvent event, JDARegisteredCommand command) throws IllegalCommandException {
        List<Object> objects = Lists.newArrayList();

        command.getParameters().stream()
                .map(JDACommandParameter::new)
                .forEach(parameter -> {
                    OptionMapping option = event.getOption(parameter.getName());

                    if (option == null) {
                        if (parameter.getDefaultValue() != null) {
                            objects.add(parameter.getDefaultValue());
                        } else {
                            objects.add(null);
                        }
                    } else {
                        objects.add(resolveParameter(event, command, parameter, option));
                    }
                });

        return objects.toArray();
    }

    /**
     * Resolves a parameter for a {@link JDARegisteredCommand}.
     *
     * @param event the {@link SlashCommandInteractionEvent} to process
     * @param command the {@link JDARegisteredCommand} to process
     * @param parameter the {@link JDACommandParameter} to process
     * @param option the {@link OptionMapping} to process
     * @return the resolved parameter
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SneakyThrows(IllegalCommandException.class)
    private static Object resolveParameter(SlashCommandInteractionEvent event, JDARegisteredCommand command, JDACommandParameter parameter, OptionMapping option) {
        JDACommandExecutionContext context = new JDACommandExecutionContext(command, parameter, option, event);

        ContextResolver<?, JDACommandExecutionContext> resolver = JDACommandManager.getSingleton().getCommandContexts().getResolver(parameter.getParameter().getType());

        if (resolver == null) {
            return null;
        } else {
            Object resolve = resolver.resolve(context);

            if (parameter.hasCondition()) {
                CommandOptionContext optionContext = new CommandOptionContext(parameter.getCondition());

                JDACommandConditions.Condition condition = JDACommandManager.getSingleton().getCommandConditions().getCondition(parameter.getParameter().getType(), optionContext.getKey());

                if (condition != null) {
                    condition.validate(optionContext, context, resolve);
                }
            }

            return resolve;
        }
    }
}
