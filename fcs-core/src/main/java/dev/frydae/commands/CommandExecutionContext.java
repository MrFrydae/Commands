package dev.frydae.commands;

import lombok.Data;

@Data
public class CommandExecutionContext {
    private final RegisteredCommand command;
    private final CommandParameter parameter;
}
