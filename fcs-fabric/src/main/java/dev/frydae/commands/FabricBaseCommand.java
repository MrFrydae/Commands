package dev.frydae.commands;

import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class FabricBaseCommand extends BaseCommand {
    @Getter @Setter private ServerCommandSource source;
    @Getter @Setter private CommandContext<ServerCommandSource> context;

    @Override
    public void registerCommand() {
        FabricCommandManager.registerCommand(this);
    }

    protected void reply(Text text) {
        getContext().getSource().sendMessage(text);
    }
}
