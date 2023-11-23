package dev.frydae.commands;

import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.Description;
import dev.frydae.commands.annotations.Name;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MiscCommands extends FabricBaseCommand {

    @CommandAlias("fish|f")
    @Description("pickles")
    public void onFish(@Name("fish") @Description("fish") String fish) {
        getContext().getSource().sendMessage(Text.literal(fish).formatted(Formatting.AQUA).formatted(Formatting.BOLD));
    }

    @CommandAlias("ping")
    @Description("ping")
    public void onPing() {
        getContext().getSource().sendMessage(Text.literal("Pong!"));
    }
}
