package dev.frydae.commands;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CommandUtils {
    private CommandUtils() {}

    /**
     * Makes each word in a string start with an uppercase letter.
     *
     * @param line String to convert
     * @return a String with each word starting with a uppercase letter
     */
    @NotNull
    public static String ucfirst(String line) {
        return Arrays
                .stream(line.toLowerCase().split(" "))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1) + ' ')
                .collect(Collectors.joining())
                .trim();
    }

    /**
     * Parses a String to an Integer if valid.
     *
     * @param var string to parse
     * @param def default to return if var is null
     * @return an {@link Integer} version of input string
     */
    @Contract("null, _ -> param2")
    public static Integer parseInt(String var, Integer def) {
        if (var == null) {
            return def;
        }

        try {
            return Integer.parseInt(var);
        } catch (NumberFormatException ignored) {
            // Do nothing because this shouldn't happen
        }

        return def;
    }

    public static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }

    public static boolean isTruthy(String input) {
        return switch (input.toLowerCase()) {
            case "yes", "y", "true", "t", "on" -> true;
            default -> false;
        };
    }

    public static <T> List<T> reverseList(List<T> list) {
        List<T> copy = Lists.newArrayList();
        copy.addAll(list);
        Collections.reverse(copy);

        return copy;
    }
}
