package dev.frydae.commands;

import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.Subcommand;

@CommandAlias("simple")
class SimpleCommandClass {

    @Subcommand("simple")
    public void onSubCommand() {

    }
}
