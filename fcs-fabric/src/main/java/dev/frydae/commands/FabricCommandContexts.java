package dev.frydae.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

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
        registerContext(ServerPlayerEntity.class, c -> c.getContext().getSource().getServer().getPlayerManager().getPlayer(c.getParameterText()));
    }
}
