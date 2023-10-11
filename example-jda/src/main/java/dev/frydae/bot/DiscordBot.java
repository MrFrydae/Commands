package dev.frydae.bot;

import dev.frydae.bot.commands.Commands;
import dev.frydae.bot.listeners.CommandListener;
import dev.frydae.bot.utils.CaselessHashMap;
import dev.frydae.bot.utils.GuildUtil;
import dev.frydae.commands.JDACommandManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Scanner;

public class DiscordBot {
    private static JDA jda;
    private static Logger logger;

    @SneakyThrows
    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(DiscordBot.class);

        setupJDA();

        JDACommandManager.setJDA(jda);
        JDACommandManager.setLogger(logger);

        jda.awaitReady();

        try {
            Commands.registerCommands();
        } catch (Exception e) {
            e.printStackTrace();
            GuildUtil.shutdown();
        }

        startTerminalListener();
    }

    private static void setupJDA() {
        JDABuilder builder = JDABuilder.createDefault(Config.getBotToken());
        builder.enableIntents(EnumSet.allOf(GatewayIntent.class));
        builder.addEventListeners(new CommandListener());
        jda = builder.build();
    }

    private static void startTerminalListener() {
        CaselessHashMap<Runnable> actions = new CaselessHashMap<>();

        actions.put("stop", GuildUtil::shutdown);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Type stop to exit");

        new Thread(() -> {
            while (true) {
                String line = scanner.nextLine();

                if (actions.containsKey(line)) {
                    actions.get(line).run();
                }
            }
        }).start();
    }


    // <editor-fold desc="Getters">
    public static JDA getJDA() {
        return jda;
    }

    public static Logger getLogger() {
        return logger;
    }
    // </editor-fold>
}
