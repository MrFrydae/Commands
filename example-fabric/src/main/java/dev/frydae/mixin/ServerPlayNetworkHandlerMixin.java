package dev.frydae.mixin;

import com.mojang.brigadier.ParseResults;
import dev.frydae.commands.FabricCommandHandler;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow protected abstract ParseResults<ServerCommandSource> parse(String command);

    @Inject(method = "handleCommandExecution", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;parse(Ljava/lang/String;)Lcom/mojang/brigadier/ParseResults;"), cancellable = true)
    public void onPreCommandExecute(CommandExecutionC2SPacket packet, LastSeenMessageList lastSeenMessages, CallbackInfo info) {
        String command = packet.command();

        ActionResult result = FabricCommandHandler.processCommand(parse(command).getContext().getSource(), command.substring(0, command.indexOf(' ')), command.substring(command.indexOf(' ') + 1).split(" "));

        if (result != ActionResult.PASS) {
            info.cancel();
        }
    }
}
