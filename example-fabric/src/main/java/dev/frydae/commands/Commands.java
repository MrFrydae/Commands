package dev.frydae.commands;

public class Commands {
    public static void registerCommands() {
        new MiscCommands();
        new TestCommand();

        FabricCommandManager.upsertCommands();
    }
}
