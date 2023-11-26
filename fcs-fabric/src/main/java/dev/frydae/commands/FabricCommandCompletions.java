package dev.frydae.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FabricCommandCompletions extends CommandCompletions<String> {
    private final Map<String, Function<FabricCommandOptionContext, List<String>>> smartResolverMap;

    FabricCommandCompletions() {
        super();

        smartResolverMap = Maps.newHashMap();

        registerCompletion("fruit", c -> Lists.newArrayList("apple", "pear", "banana", "blueberry", "strawberry"));

        registerCompletion("range", c -> {
            String config = c.getConfig();

            String[] split = config.split("-");

            int min;
            int max;
            if (split.length == 1) {
                min = 0;
                max = Integer.parseInt(split[0]);
            } else {
                min = Integer.parseInt(split[0]);
                max = Integer.parseInt(split[1]);
            }

            return IntStream.rangeClosed(min, max).limit(25).mapToObj(String::valueOf).collect(Collectors.toList());
        });

        registerSmartCompletion("onlineplayers", c -> {
            MinecraftServer server = c.getContext().getSource().getServer();

            return server.getPlayerManager().getPlayerList()
                    .stream()
                    .filter(p -> !c.hasConfig("other") || c.getContext().getSource().getPlayer() != p)
                    .map(p -> p.getName().getString())
                    .collect(Collectors.toList());
        });
    }

    public void registerSmartCompletion(String input, Function<FabricCommandOptionContext, List<String>> resolver) {
        smartResolverMap.put(input, resolver);
    }

    public Function<FabricCommandOptionContext, List<String>> getSmartResolver(String input) {
        return smartResolverMap.get(input);
    }

    @NotNull
    public List<String> getCompletions(String key, CommandContext<ServerCommandSource> context) {
        List<String> regularCompletions = getCompletions(key);

        if (!regularCompletions.isEmpty()) {
            return regularCompletions;
        }

        String input = key.split("\\|")[0];

        Function<FabricCommandOptionContext, List<String>> smartResolver = getSmartResolver(input);

        if (smartResolver != null) {
            FabricCommandOptionContext fabricContext = new FabricCommandOptionContext(key, context);

            return smartResolver.apply(fabricContext);
        }

        return Lists.newArrayList();
    }
}
