package dev.frydae;

import dev.frydae.commands.Commands;
import dev.frydae.commands.FabricCommandManager;
import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        Commands.registerCommands();
    }
}
