package dev.frydae.commands.testing.annotations;

import dev.frydae.commands.testing.extensions.CommandTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(CommandTestExtension.class)
public @interface CommandTest {
}
