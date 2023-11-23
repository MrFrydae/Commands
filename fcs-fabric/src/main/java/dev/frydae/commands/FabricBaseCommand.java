package dev.frydae.commands;

import com.mojang.brigadier.context.CommandContext;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FabricBaseCommand extends BaseCommand {
    @Getter @Setter private CommandContext<ServerCommandSource> context;

    @Override
    public void registerCommand() {
        FabricCommandManager.registerCommand(this);
    }

    protected void reply(Text text) {
        getPlayer().sendMessage(text);
    }

    protected void reply(Text text, boolean actionBar) {
        getPlayer().sendMessage(text, actionBar);
    }

    @NotNull
    public ServerCommandSource getSource() {
        return Objects.requireNonNull(context.getSource());
    }

    @NotNull
    public ServerPlayerEntity getPlayer() {
        return Objects.requireNonNull(getSource().getPlayer());
    }
}
