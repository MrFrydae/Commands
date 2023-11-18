package dev.frydae.commands;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.Map;

public final class JDACommandContexts extends CommandContexts<JDACommandExecutionContext> {
    private final Map<Class<?>, OptionType> typeMap;

    /**
     * Creates a new command context manager.
     */
    JDACommandContexts() {
        typeMap = Maps.newHashMap();

        registerContexts();
        registerMappings();
    }

    /**
     * Registers a set of contexts for basic types related to Discord.
     */
    private void registerContexts() {
        registerContext(Integer.class, c -> c.getMapping().getAsInt());
        registerContext(Double.class, c -> c.getMapping().getAsDouble());
        registerContext(Boolean.class, c -> c.getMapping().getAsBoolean());
        registerContext(Long.class, c -> c.getMapping().getAsLong());
        registerContext(String.class, c -> c.getMapping().getAsString());
        registerContext(TextChannel.class, c -> c.getMapping().getAsChannel().asTextChannel());
        registerContext(VoiceChannel.class, c -> c.getMapping().getAsChannel().asVoiceChannel());
        registerContext(User.class, c -> c.getMapping().getAsUser());
        registerContext(Member.class, c -> c.getMapping().getAsMember());
    }

    /**
     * Registers a set of mappings for basic types related to Discord.
     */
    private void registerMappings() {
        registerMapping(Integer.class, OptionType.INTEGER);
        registerMapping(Double.class, OptionType.NUMBER);
        registerMapping(Long.class, OptionType.NUMBER);
        registerMapping(Boolean.class, OptionType.BOOLEAN);
        registerMapping(String.class, OptionType.STRING);
        registerMapping(TextChannel.class, OptionType.CHANNEL);
        registerMapping(VoiceChannel.class, OptionType.CHANNEL);
        registerMapping(FileUpload.class, OptionType.ATTACHMENT);
        registerMapping(Member.class, OptionType.USER);
        registerMapping(User.class, OptionType.USER);
        registerMapping(Role.class, OptionType.ROLE);
    }

    /**
     * Registers a new mapping for a specific type.
     *
     * @param clazz the class to register
     * @param type the type to register
     * @param <T> the type to register
     */
    public <T> void registerMapping(Class<T> clazz, OptionType type) {
        typeMap.put(clazz, type);
    }

    /**
     * Gets the mapping for a specific class.
     *
     * @param clazz the class to get the mapping for
     * @return the mapping for the specified class
     */
    public OptionType getMapping(Class<?> clazz) {
        return typeMap.get(clazz);
    }
}
