package dev.frydae.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public abstract class CommandManager {
    private static Logger logger;
    private static Annotations annotations;
    @Getter private static final List<RegisteredCommand> rootCommands = Lists.newArrayList();
    @Getter private static final Map<String, RegisteredCommand> commandCache = Maps.newHashMap();

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        CommandManager.logger = logger;
    }

    /**
     * @return the command manager's annotations manager
     */
    public static Annotations getAnnotations() {
        if (annotations == null) {
            annotations = new Annotations();
        }

        return annotations;
    }

    /**
     * @return the command manager's command contexts manager
     */
    public abstract CommandContexts<? extends CommandExecutionContext> getCommandContexts();

    /**
     * @return the command manager's command completions manager
     */
    public abstract CommandCompletions<?> getCommandCompletions();

    /**
     * Searches through the {@link CommandManager#commandCache} for the command.
     * </p>
     * If one isn't found, recursively search through all registered commands for one.
     * If one is then found, add it to {@link CommandManager#commandCache}
     *
     * @param fullCommandName the full discord command name
     * @return a {@link RegisteredCommand} if found, null otherwise
     */
    @Nullable
    static RegisteredCommand findRegisteredCommand(String fullCommandName) {
        if (getCommandCache().containsKey(fullCommandName)) {
            return getCommandCache().get(fullCommandName);
        }

        for (RegisteredCommand registeredCommand : getRootCommands()) {
            if (registeredCommand.getFullName().equalsIgnoreCase(fullCommandName)) {
                getCommandCache().put(fullCommandName, registeredCommand);

                return registeredCommand;
            }

            if (registeredCommand.hasSubcommands()) {
                for (RegisteredCommand subcommand : registeredCommand.getSubcommands()) {
                    if (subcommand.getFullName().equalsIgnoreCase(fullCommandName)) {
                        getCommandCache().put(fullCommandName, subcommand);

                        return subcommand;
                    }
                }
            }
        }

        return null;
    }
}
