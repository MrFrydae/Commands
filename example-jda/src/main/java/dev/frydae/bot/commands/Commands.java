package dev.frydae.bot.commands;

import dev.frydae.commands.JDACommandManager;

public class Commands {
    /**
     * Register command related things.
     */
    public static void registerCommands() {
        new MiscCommands();
        new CountCommand();

        JDACommandManager.upsertCommands();
    }
}
