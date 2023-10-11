package dev.frydae.commands;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;
import java.util.List;

public final class JDACommandManager extends CommandManager {
    private static JDACommandManager singleton;
    @Getter private final JDACommandContexts<JDACommandExecutionContext> commandContexts;
    @Getter private final JDACommandCompletions commandCompletions;
    @Getter private final JDACommandConditions commandConditions;
    private static JDA jda;

    /**
     * Creates a new command manager.
     */
    private JDACommandManager() {
        this.commandContexts = new JDACommandContexts<>();
        this.commandCompletions = new JDACommandCompletions();
        this.commandConditions = new JDACommandConditions();
    }

    public static JDACommandManager getSingleton() {
        if (singleton == null) {
            singleton = new JDACommandManager();
        }

        return singleton;
    }

    @TestOnly
    public static void resetSingleton() {
        singleton = null;
    }

    public static JDA getJDA() {
        return JDACommandManager.jda;
    }

    public static void setJDA(JDA jda) {
        JDACommandManager.jda = jda;
    }

    /**
     * Searches for a matching {@link JDARegisteredCommand} and delegates to {@link JDACommandCompletions#processAutoComplete}.
     *
     * @param event the {@link CommandAutoCompleteInteractionEvent}
     */
    public static void processCommandAutoComplete(CommandAutoCompleteInteractionEvent event) {
        JDARegisteredCommand command = (JDARegisteredCommand) findRegisteredCommand(event.getFullCommandName());

        if (command != null) {
            getSingleton().getCommandCompletions().processAutoComplete(event, command);
        }
    }

    /**
     * Registers out commands with Discord.
     */
    public static void upsertCommands() {
        CommandListUpdateAction commandListUpdateAction = getJDA().updateCommands();

        for (RegisteredCommand cmd : getRootCommands()) {
            JDARegisteredCommand command = (JDARegisteredCommand) cmd;

            SlashCommandData commandData = Commands.slash(command.getName(), command.getDescription());

            if (command.getPermissions() != null) {
                commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getPermissions()));
            }

            if (!command.hasSubcommands()) {
                command.getParameters().stream()
                        .map(JDACommandParameter::new)
                        .map(JDACommandManager::getOptionData)
                        .forEachOrdered(commandData::addOptions);
            } else {
                for (RegisteredCommand subcommand : command.getSubcommands()) {
                    SubcommandData subcommandData = new SubcommandData(subcommand.getName(), subcommand.getDescription());

                    subcommand.getParameters().stream()
                            .map(JDACommandParameter::new)
                            .map(JDACommandManager::getOptionData)
                            .forEachOrdered(subcommandData::addOptions);

                    commandData.addSubcommands(subcommandData);
                }
            }

            commandData.setGuildOnly(!command.isGlobal());

            commandListUpdateAction.addCommands(commandData);
        }

        commandListUpdateAction.queue(success -> {
            System.out.println("Commands Updated :)");
        });
    }

    @NotNull
    private static OptionData getOptionData(JDACommandParameter parameter) {
        JDACommandContexts<JDACommandExecutionContext> commandContexts = JDACommandManager.getSingleton().getCommandContexts();
        JDACommandCompletions commandCompletions = JDACommandManager.getSingleton().getCommandCompletions();

        OptionData optionData = new OptionData(commandContexts.getMapping(parameter.getParameter().getType()), parameter.getName(), parameter.getDescription(), !parameter.isOptional());

        if (parameter.hasCompletion()) {
            if (parameter.isAutoComplete()) {
                optionData.setAutoComplete(true);
            } else {
                List<Choice> choices = commandCompletions.getCompletions(parameter.getCompletion());

                if (choices != null) {
                    optionData.addChoices(choices);
                }
            }
        } else if (parameter.hasValues()) {
            List<Choice> choices = Arrays.stream(parameter.getValues().split("\\|")).map(c -> new Choice(c, c)).toList();

            optionData.addChoices(choices);
        }

        return optionData;
    }

    public static void registerCommand(JDABaseCommand baseCommand) {
        JDACommandRegistration.registerCommandAliases(baseCommand);
        JDACommandRegistration.registerSubCommands(baseCommand);
    }
}
