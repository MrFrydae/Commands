package dev.frydae.commands;

import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

public final class FabricCommandExecutionContext extends CommandExecutionContext {
    @Getter public final String parameterText;
    @Getter public final CommandContext<ServerCommandSource> context;

    public FabricCommandExecutionContext(FabricRegisteredCommand command, FabricCommandParameter parameter, String parameterText, CommandContext<ServerCommandSource> context) {
        super(command, parameter);

        this.parameterText = parameterText;
        this.context = context;
    }
}
