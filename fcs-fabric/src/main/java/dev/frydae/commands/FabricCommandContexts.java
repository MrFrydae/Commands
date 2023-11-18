package dev.frydae.commands;

public class FabricCommandContexts extends CommandContexts<FabricCommandExecutionContext> {
    FabricCommandContexts() {
        registerContexts();
    }

    public void registerContexts() {
        registerContext(Integer.class, c -> Integer.parseInt(c.getParameterText()));
        registerContext(Double.class, c -> Double.parseDouble(c.getParameterText()));
        registerContext(Long.class, c -> Long.parseLong(c.getParameterText()));
        registerContext(String.class, FabricCommandExecutionContext::getParameterText);
        registerContext(Boolean.class, c -> CommandUtils.isTruthy(c.getParameterText()));
    }
}
