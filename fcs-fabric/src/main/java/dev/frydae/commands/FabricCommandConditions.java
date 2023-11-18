package dev.frydae.commands;

public class FabricCommandConditions extends CommandConditions<FabricCommandExecutionContext> {

    @Override
    public void addConditions() {
        addCondition(Integer.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }

            if (c.hasConfig("min") && c.getConfig("min", 0) > value) {
                throw new IllegalCommandException("Min value must be " + c.getConfig("min", 0));
            }

            if (c.hasConfig("max") && c.getConfig("max", 5) < value) {
                throw new IllegalCommandException("Max value must be " + c.getConfig("max", 5));
            }
        });
    }
}
