package dev.frydae.bot.utils;

import dev.frydae.bot.DiscordBot;

public class GuildUtil {
    public static void shutdown() {
        DiscordBot.getJDA().shutdownNow();
        System.exit(0);
    }
}
