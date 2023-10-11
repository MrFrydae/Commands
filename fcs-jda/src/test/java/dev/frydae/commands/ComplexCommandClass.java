package dev.frydae.commands;

import dev.frydae.commands.annotations.Default;
import dev.frydae.commands.annotations.Subcommand;

public class ComplexCommandClass {

    @Default
    @Subcommand("help")
    public void onHelp() {

    }
}
