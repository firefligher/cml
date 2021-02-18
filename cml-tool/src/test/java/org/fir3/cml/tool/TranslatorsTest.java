package org.fir3.cml.tool;

import org.fir3.cml.api.Translator;
import org.fir3.cml.api.model.Environment;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TranslatorsTest {
    private static final String DUMMY_TRANSLATOR_NAME = "dummy";
    private static final String COLLIDING_TRANSLATORS_NAME = "colliding";

    private static abstract class AbstractDummyTranslator implements Translator {
        @Override
        public final void translate(
                Environment environment,
                String targetDomain,
                InputStream configSource
        ) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    @Translator.Info(name = TranslatorsTest.DUMMY_TRANSLATOR_NAME)
    public static final class DummyTranslator
            extends AbstractDummyTranslator { }

    @Translator.Info(name = TranslatorsTest.COLLIDING_TRANSLATORS_NAME)
    public static final class CollidingTranslator1
            extends AbstractDummyTranslator { }

    @Translator.Info(name = TranslatorsTest.COLLIDING_TRANSLATORS_NAME)
    public static final class CollidingTranslator2
            extends AbstractDummyTranslator { }

    public static final class IgnoredTranslator
            extends AbstractDummyTranslator { }

    @Test
    public void testDiscovery() {
        Map<Translator.Info, Translator> translators =
                Translators.getInstance().getTranslators();

        // Asserting that the dummy translator was loaded

        Translator.Info info = translators.keySet()
                .stream()
                .filter(i -> Objects.equals(
                        TranslatorsTest.DUMMY_TRANSLATOR_NAME,
                        i.name()
                ))
                .findFirst()
                .orElse(null);

        assertNotNull(info);

        Translator translator = translators.get(info);
        assertSame(DummyTranslator.class, translator.getClass());
    }

    @Test
    public void testCollision() {
        Map<Translator.Info, Translator> translators =
                Translators.getInstance().getTranslators();

        // Verifying that there is only one entry in the translators map with
        // the colliding translator name.

        long count = translators.keySet()
                .stream()
                .filter(i -> Objects.equals(
                        TranslatorsTest.COLLIDING_TRANSLATORS_NAME,
                        i.name()
                ))
                .count();

        assertEquals(1, count);
    }

    @Test
    public void testIgnored() {
        Map<Translator.Info, Translator> translators =
                Translators.getInstance().getTranslators();

        // Verifying that the IgnoredTranslator was not included in the
        // translators map (as value).

        for (Translator translator : translators.values()) {
            assertNotSame(IgnoredTranslator.class, translator.getClass());
        }
    }

    @Test
    public void testUnmodifiability() {
        // Modifying the translator map of the Translators singleton should not
        // be possible or have no effect on other maps that have been queried
        // at another point.

        Map<Translator.Info, Translator> translators =
                Translators.getInstance().getTranslators();

        Preconditions.condition(
                !translators.isEmpty(),
                "No Translator implementations in classpath!"
        );

        try {
            translators.clear();
        } catch (Throwable t) {
            // Not mandatory for this test.
        }

        assertFalse(Translators.getInstance().getTranslators().isEmpty());
    }
}
