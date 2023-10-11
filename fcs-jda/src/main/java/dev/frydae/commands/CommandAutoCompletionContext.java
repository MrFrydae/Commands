package dev.frydae.commands;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public class CommandAutoCompletionContext extends CommandOptionContext {
    @Getter private final CommandAutoCompleteInteractionEvent event;
    @Getter private final String current;

    /**
     * Creates a new auto completion context.
     *
     * @param event the event that triggered the auto completion
     * @param input the input that the user has typed
     * @param current the current word that the user is trying to complete
     */
    CommandAutoCompletionContext(CommandAutoCompleteInteractionEvent event, String input, String current) {
        super(input);
        this.event = event;
        this.current = current;
    }
}
