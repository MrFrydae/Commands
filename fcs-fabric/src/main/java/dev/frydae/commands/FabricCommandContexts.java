package dev.frydae.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FabricCommandContexts extends CommandContexts<FabricCommandExecutionContext> {
    FabricCommandContexts() {
        registerContexts();
    }

    public void registerContexts() {
        registerContext(Integer.class, c -> Integer.parseInt(c.getParameterText()));
        registerContext(Double.class, c -> Double.parseDouble(c.getParameterText()));
        registerContext(Float.class, c -> Float.parseFloat(c.getParameterText()));
        registerContext(Long.class, c -> Long.parseLong(c.getParameterText()));
        registerContext(String.class, FabricCommandExecutionContext::getParameterText);
        registerContext(Boolean.class, c -> CommandUtils.isTruthy(c.getParameterText()));
        registerContext(ServerPlayerEntity.class, c -> {
            ServerCommandSource source = c.getContext().getSource();
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(c.getParameterText());

            if (player == null && source.getPlayer() != null) {
                source.getPlayer().sendMessage(Text.literal("Player: " + c.getParameterText() + " cannot be found").formatted(Formatting.RED));
            }

            return player;
        });
    }
}
