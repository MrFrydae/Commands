package dev.frydae.commands;

import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

@Getter
public final class FabricCommandOptionContext extends CommandOptionContext {
    private final CommandContext<ServerCommandSource> context;

    FabricCommandOptionContext(String input, CommandContext<ServerCommandSource> context) {
        super(input);

        this.context = context;
    }
}
