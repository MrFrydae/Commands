package dev.frydae.bot.commands;

import dev.frydae.commands.JDABaseCommand;
import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.Description;
import dev.frydae.commands.annotations.Subcommand;

@CommandAlias("count")
@Description("counting stuff")
public class CountCommand extends JDABaseCommand {
    @Subcommand("one")
    @Description("one")
    public void onOne() {
        replyHidden("one").queue();
    }

    @Subcommand("two")
    @Description("two")
    public void onTwo() {
        replyHidden("two").queue();
    }
}
