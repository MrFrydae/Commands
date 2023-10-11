package dev.frydae.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class JDACommandCompletions extends CommandCompletions<Choice> {
    private final Map<String, Function<CommandAutoCompletionContext, List<Choice>>> autoResolverMap;

    /**
     * Creates a new command completion manager.
     */
    JDACommandCompletions() {
        super();

        autoResolverMap = Maps.newHashMap();

        registerCompletion("fruit", c -> {
            List<String> fruits = Lists.newArrayList("apple", "pear", "banana", "blueberry", "strawberry");

            return fruits.stream().map(f -> new Choice(f, f)).collect(Collectors.toList());
        });

        registerCompletion("range", c -> {
            String config = c.getConfig();

            String[] split = config.split("-");

            int min;
            int max;
            if (split.length == 1) {
                min = 0;
                max = Integer.parseInt(split[0]);
            } else {
                min = Integer.parseInt(split[0]);
                max = Integer.parseInt(split[1]);
            }

            return IntStream.rangeClosed(min, max).limit(25).mapToObj(i -> new Choice(String.valueOf(i), i)).collect(Collectors.toList());
        });

        registerAutoCompletion("range", c -> {
            String config = c.getConfig();

            String[] split = config.split("-");

            int min;
            int max;
            if (split.length == 1) {
                min = 0;
                max = Integer.parseInt(split[0]);
            } else {
                min = Integer.parseInt(split[0]);
                max = Integer.parseInt(split[1]);
            }

            return IntStream.rangeClosed(min, max)
                    .mapToObj(String::valueOf)
                    .filter(s -> s.startsWith(c.getCurrent()))
                    .limit(25)
                    .map(i -> new Choice(i, Integer.parseInt(i)))
                    .collect(Collectors.toList());
        });
    }

    /**
     * Registers a new auto completion resolver.
     *
     * @param input the input to search for
     * @param resolver the resolver to register
     */
    public void registerAutoCompletion(String input, Function<CommandAutoCompletionContext, List<Choice>> resolver) {
        autoResolverMap.put(input, resolver);
    }

    /**
     * Gets the auto resolver for a given input.
     *
     * @param input the input to search for
     * @return the auto resolver for this input
     */
    public Function<CommandAutoCompletionContext, List<Choice>> getAutoResolver(String input) {
        return autoResolverMap.get(input);
    }

    /**
     * Processes an auto complete event.
     *
     * @param event The {@link CommandAutoCompleteInteractionEvent} to be processed
     * @param command the found {@link JDARegisteredCommand}
     */
    public void processAutoComplete(CommandAutoCompleteInteractionEvent event, JDARegisteredCommand command) {
        String name = event.getFocusedOption().getName();
        JDACommandParameter parameter = command.getParameter(name);

        String completion = parameter.getCompletion().substring(1);

        Function<CommandAutoCompletionContext, List<Choice>> autoResolver = getAutoResolver(completion.split("\\|")[0]);

        if (autoResolver == null) {
            JDACommandManager.getLogger().error("No AutoResolver found for: (" + completion + ")");

            return;
        }

        CommandAutoCompletionContext context = new CommandAutoCompletionContext(event, completion, event.getFocusedOption().getValue());

        List<Choice> apply = autoResolver.apply(context);

        event.replyChoices(apply).queue();
    }
}
