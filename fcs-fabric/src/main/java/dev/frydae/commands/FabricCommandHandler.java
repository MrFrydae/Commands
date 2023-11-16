package dev.frydae.commands;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.ActionResult;
import org.apache.commons.lang3.StringUtils;

public class FabricCommandHandler {
    public static ActionResult processCommand(ServerCommandSource source, String command, String[] args) {
        System.out.printf("Source: %s\n", source.getPlayer().getName().getString());
        System.out.printf("Command: %s\n", command);
        System.out.printf("Args: %s\n", StringUtils.join(args, '|'));

        return ActionResult.PASS;
    }
}
