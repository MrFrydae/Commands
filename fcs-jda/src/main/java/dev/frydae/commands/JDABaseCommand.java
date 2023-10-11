package dev.frydae.commands;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;

public class JDABaseCommand extends BaseCommand {
    @Getter @Setter private SlashCommandInteractionEvent event;

    @Override
    public void registerCommand() {
        JDACommandManager.registerCommand(this);
    }

    void tearDown() {
        this.event = null;
    }

    public Member getSender() {
        return event.getMember();
    }

    @CheckReturnValue
    protected ReplyCallbackAction replyHidden(String message) {
        return getEvent().getInteraction().reply(message).setEphemeral(true);
    }

    @CheckReturnValue
    protected ReplyCallbackAction replyHidden(MessageEmbed... embeds) {
        return getEvent().getInteraction().replyEmbeds(Arrays.asList(embeds)).setEphemeral(true);
    }
}
