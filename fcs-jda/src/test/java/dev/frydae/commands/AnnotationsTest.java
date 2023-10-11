package dev.frydae.commands;

import dev.frydae.commands.annotations.CommandAlias;
import dev.frydae.commands.annotations.Subcommand;
import dev.frydae.commands.testing.annotations.CommandTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CommandTest
public class AnnotationsTest {
    private Annotations annotations;

    @BeforeEach
    public void setup() {
        annotations = JDACommandManager.getAnnotations();
    }

    @Test
    public void testSimpleAlias() {
        String alias = annotations.getAnnotationValue(SimpleCommandClass.class, CommandAlias.class);

        assertNotNull(alias);
        assertEquals("simple", alias);
    }

    @Test
    public void testSimpleSubcommand() throws NoSuchMethodException {
        Method subCommandMethod = SimpleCommandClass.class.getMethod("onSubCommand");

        String subcommand = annotations.getAnnotationValue(subCommandMethod, Subcommand.class);

        assertNotNull(subcommand);
        assertEquals("simple", subcommand);
    }
}
