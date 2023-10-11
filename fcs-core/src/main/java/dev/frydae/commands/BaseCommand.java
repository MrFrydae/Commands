package dev.frydae.commands;

public abstract class BaseCommand {
    public BaseCommand() {
        registerCommand();
    }

    public abstract void registerCommand();
}
