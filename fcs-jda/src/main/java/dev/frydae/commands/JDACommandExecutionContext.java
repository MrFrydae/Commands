package dev.frydae.commands;

import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public final class JDACommandExecutionContext extends CommandExecutionContext {
    @Getter private final OptionMapping mapping;
    @Getter private final SlashCommandInteractionEvent event;

    public JDACommandExecutionContext(JDARegisteredCommand command, JDACommandParameter parameter, OptionMapping mapping, SlashCommandInteractionEvent event) {
        super(command, parameter);
        this.mapping = mapping;
        this.event = event;
    }
}
