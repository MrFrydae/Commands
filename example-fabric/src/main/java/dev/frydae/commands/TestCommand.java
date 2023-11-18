package dev.frydae.commands;

import dev.frydae.commands.annotations.*;
import net.minecraft.text.Text;

@CommandAlias("test")
@Description("test command")
public class TestCommand extends FabricBaseCommand {

    @Subcommand("pickle")
    @Description("pickles")
    public void onPickle() {
        System.out.println("fish");
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

    @CommandAlias("perms")
    @Subcommand("perms")
    @Description("perms")
    @CommandPermission
    public void onPerms() {
        reply(Text.literal("You have permission for this... yay"));
    }
}
