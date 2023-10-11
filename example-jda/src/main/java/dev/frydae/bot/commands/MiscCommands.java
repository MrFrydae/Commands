package dev.frydae.bot.commands;

import dev.frydae.bot.utils.GuildUtil;
import dev.frydae.commands.annotations.*;
import dev.frydae.commands.JDABaseCommand;
import net.dv8tion.jda.api.Permission;

public class MiscCommands extends JDABaseCommand {
    @CommandAlias("ping")
    @Description("Pong!")
    @GlobalCommand
    public void onPing() {
        replyHidden(":ping_pong: Pong!").queue();
    }

    @CommandAlias("values")
    @Description("test values")
    public void onValues(@Values("fish|turtle|penguin|ice cream") @Name("fish") @Description("fish") String fish) {
        replyHidden(fish).queue();
    }

    @CommandAlias("range")
    @Description("test")
    public void onTest(@Completion("range|1-5") @Name("range") @Description("range") Integer fish) {
        replyHidden(String.valueOf(fish)).queue();
    }

    @CommandAlias("arange")
    @Description("test")
    public void onAutoRange(@Completion("@range|1-100") @Name("range") @Description("range") Integer fish) {
        replyHidden(String.valueOf(fish)).queue();
    }

    @CommandAlias("limit")
    @Description("limit")
    public void onLimit(@Condition("limits|min=1,max=10") @Name("fish") @Description("fish") Integer fish) {
        replyHidden(String.valueOf(fish)).queue();
    }

    @CommandAlias("stop")
    @Description("Stops the bot")
    @CommandPermission(Permission.ADMINISTRATOR)
    public void onStop() {
        replyHidden("Shutting down now").queue(success -> {
            GuildUtil.shutdown();
        });
    }
}
