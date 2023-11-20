package dev.frydae.commands;

import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.CommandPermission;
import dev.frydae.commands.annotations.Description;
import dev.frydae.commands.annotations.Subcommand;
import net.minecraft.text.Text;

@CommandAlias("perms")
@Description("perms")
public class PermsCommand extends FabricBaseCommand {

    @Subcommand("simple")
    @Description("simple")
    @CommandPermission
    public void onSimplePerms() {
        reply(Text.literal("You have permission for this... yay"));
    }

    @Subcommand("complex")
    @Description("complex")
    @CommandPermission("sample.permission")
    public void onComplexPerms() {
        reply(Text.literal("You have permission for this... yay"));
    }

    @Subcommand("more")
    @Description("complex")
    @CommandPermission("sample.permission.more")
    public void onMorePerms() {
        reply(Text.literal("You have permission for this... yay"));
    }
}
