package dev.frydae.bot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

public class Config {
    // <editor-fold desc="Getters">
    private static JSONObject get() {
        InputStream in = DiscordBot.class.getResourceAsStream("/config.json");

        if (in != null) {
            Reader reader = new InputStreamReader(in);

            Object obj = null;
            try {
                obj = new JSONParser().parse(reader);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return (JSONObject) obj;
        }

        return null;
    }

    public static JSONObject getObject(String key) {
        return (JSONObject) get().get(key);
    }

    public static String getString(String key) {
        return (String) get().get(key);
    }

    public static Long getLong(String key) {
        return getLong(Objects.requireNonNull(get()), key);
    }

    public static Long getLong(JSONObject object, String key) {
        return (Long) object.get(key);
    }

    public static Float getFloat(String key) {
        return (Float) get().get(key);
    }

    public static Double getDouble(String key) {
        return (Double) get().get(key);
    }
    // </editor-fold>

    /**
     * Gets the bot token.
     *
     * @return The bot token for launch
     */
    public static String getBotToken() {
        return getString("bot.token");
    }

    public static long getGuildId() {
        return getLong("bot.guild.id");
    }

    public static Color getPrimaryEmbedColor() {
        return new Color(22, 138, 233);
    }

    public static Color getErrorEmbedColor() {
        return new Color(255, 0, 0);
    }

    public static Color getSuccessEmbedColor() {
        return new Color(0, 255, 0);
    }
}

