package dev.frydae.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;

public class JDACommandParameter extends CommandParameter {

    public JDACommandParameter(@NotNull Parameter parameter, @NotNull String name, @NotNull String description, boolean optional, @Nullable String defaultValue, String completion, String condition, String values) {
        super(parameter, name, description, optional, defaultValue, completion, condition, values);
    }

    public JDACommandParameter(CommandParameter parameter) {
        super(parameter.getParameter(), parameter.getName(), parameter.getDescription(), parameter.isOptional(), parameter.getDefaultValue(), parameter.getCompletion(), parameter.getCondition(), parameter.getValues());
    }

    public boolean isAutoComplete() {
        return completion != null && completion.startsWith("@");
    }
}
