package dev.frydae.commands;

import dev.frydae.commands.annotations.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;

@CommandAlias("test")
@Description("test command")
public class TestCommand extends FabricBaseCommand {

    @Subcommand("pickle")
    @Description("pickles")
    public void onPickle() {
        int i = 1;
        for (ModContainer allMod : FabricLoader.getInstance().getAllMods()) {
            reply(Text.literal(i++ + ": ").append(allMod.getMetadata().getId()));
        }
    }

    @Subcommand("banana")
    @Description("banana")
    public void onBanana(@Name("tacos") @Description("tacos") String tacos) {
        System.out.println(tacos);
    }

    @Subcommand("double")
    @Description("double")
    public void onDouble(@Name("one") @Description("one") @Completion("fruit") String one,
                         @Name("two") @Description("two") @Values("ice|cream|toilet|skibiddy") String two) {
        reply(Text.literal(one).append("|").append(two));
    }

    @Subcommand("range")
    @Description("range")
    public void onRange(@Name("num") @Description("num") @Condition("limits|min=1,max=10") Integer num) {
        reply(Text.literal(num.toString()));
    }
}
