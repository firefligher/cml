package org.fir3.cml.tool;

import org.fir3.cml.api.Translator;
import org.fir3.cml.tool.util.ClassHelper;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The manager that discovers {@link org.fir3.cml.api.Translator}
 * implementations inside the current classpath and makes them accessible.
 */
public final class Translators {
    private static final Logger LOG = Logger.getLogger(
            Translators.class.getName()
    );

    private static Translators INSTANCE;

    /**
     * Returns the singleton instance of this class.
     *
     * @return  The singleton instance of this class.
     */
    public static Translators getInstance() {
        if (Translators.INSTANCE == null) {
            Translators.INSTANCE = new Translators();
        }

        return Translators.INSTANCE;
    }

    private static Map<Translator.Info, Translator> discoverTranslators() {
        Map<Translator.Info, Translator> translators = new HashMap<>();

        // Using the Java SPI to discover all implementations of the Translator
        // interface inside the current classpath.

        ServiceLoader<Translator> translatorLoader = ServiceLoader.load(
                Translator.class
        );

        // For each Translator instance: Checking, if it was annotated with the
        // Info annotation. If that's the case and there is no collision with
        // another Translator with an equal Info annotation, we can safely add
        // it to the translators map.

        for (Translator translator : translatorLoader) {
            Class<? extends Translator> implClass = translator.getClass();

            // Attempting to extract the annotation

            Translator.Info translatorInfo = implClass.getAnnotation(
                    Translator.Info.class
            );

            if (translatorInfo == null) {
                LOG.log(
                        Level.INFO,
                        "Ignoring translator implementation ''{0}'' because " +
                                "it does not provide the ''{1}'' annotation.",
                        new Object[] {
                                implClass.getName(),
                                Translator.Info.class.getName()
                        }
                );

                continue;
            }

            // Validating that there is no other Translator implementation in
            // the translators map, whose Info annotation collides with the
            // one of the current Translator.

            Translator collidingTranslator = translators.get(translatorInfo);

            if (collidingTranslator != null) {
                Class<? extends Translator> collidingClass =
                        collidingTranslator.getClass();

                LOG.log(
                        Level.WARNING,
                        "Translator ''{0}'' ({1}) is ignored, because it " +
                                "collides with translator ''{2}'' ({3})",
                        new Object[] {
                                implClass.getName(),
                                ClassHelper.getOrigin(implClass)
                                        .map(Object::toString)
                                        .orElse("unknown origin"),

                                collidingClass.getName(),
                                ClassHelper.getOrigin(collidingClass)
                                        .map(Objects::toString)
                                        .orElse("unknown origin")
                        }
                );

                continue;
            }

            translators.put(translatorInfo, translator);
        }

        return Collections.unmodifiableMap(translators);
    }

    private final Map<Translator.Info, Translator> translators;

    private Translators() {
        this.translators = Translators.discoverTranslators();
    }

    /**
     * Returns all {@link Translator} implementations that have been
     * (successfully) discovered by this manager.
     *
     * @return  All discovered {@link Translator} implementations.
     */
    public Map<Translator.Info, Translator> getTranslators() {
        return this.translators;
    }
}
