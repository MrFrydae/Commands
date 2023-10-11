package dev.frydae.commands.testing.extensions;

import dev.frydae.commands.JDACommandManager;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CommandTestExtension implements BeforeEachCallback {
    /**
     * Callback that is invoked <em>before</em> each test is invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        JDACommandManager.resetSingleton();
    }
}
