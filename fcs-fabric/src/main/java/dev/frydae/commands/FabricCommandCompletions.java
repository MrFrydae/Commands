package dev.frydae.commands;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FabricCommandCompletions extends CommandCompletions<String> {

    FabricCommandCompletions() {
        super();

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
    }
}
