package org.fir3.cml.impl.java.config;

import com.google.gson.*;
import org.fir3.cml.impl.java.util.JsonUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The configuration type for {@link org.fir3.cml.impl.java.JavaTranslator}.
 */
public final class Configuration {
    final static class Deserializer
            implements JsonDeserializer<Configuration> {
        private static final String PROPERTY_TYPE_MAPPINGS = "typeMappings";
        private static final String PROPERTY_OUTPUT_DIRECTORY =
                "outputDirectory";

        @Override
        public Configuration deserialize(
                JsonElement element,
                Type type,
                JsonDeserializationContext ctx
        ) throws JsonParseException {
            if (!element.isJsonObject()) {
                throw new JsonParseException("Expected JSON object");
            }

            JsonObject obj = element.getAsJsonObject();

            // typeMappings

            Set<TypeMapping> typeMappings = new HashSet<>();

            if (obj.has(PROPERTY_TYPE_MAPPINGS)) {
                typeMappings.addAll(Arrays.asList(ctx.deserialize(
                        obj.get(PROPERTY_TYPE_MAPPINGS),
                        TypeMapping[].class
                )));
            }

            // outputDirectory

            File outputDirectory = null;

            if (obj.has(PROPERTY_OUTPUT_DIRECTORY)) {
                outputDirectory = new File(JsonUtil.expectStringProperty(
                        obj,
                        PROPERTY_OUTPUT_DIRECTORY
                ));
            }

            return new Configuration(typeMappings, outputDirectory);
        }
    }

    private final Set<TypeMapping> typeMappings;
    private final File outputDirectory;

    /**
     * Creates a new instance of {@link Configuration}.
     *
     * @param typeMappings      The type mappings between CML- and Java-types
     * @param outputDirectory   The output directory, where the translated
     *                          Java-files will be stored
     *
     * @throws NullPointerException If <code>typeMappings</code> is
     *                              <code>null</code>.
     */
    public Configuration(Set<TypeMapping> typeMappings, File outputDirectory) {
        Objects.requireNonNull(typeMappings);

        this.typeMappings = Collections.unmodifiableSet(new HashSet<>(
                typeMappings
        ));

        this.outputDirectory = outputDirectory;
    }

    /**
     * Returns the type mappings of this instance.
     *
     * @return  The type mappings of this instance.
     */
    public Set<TypeMapping> getTypeMappings() {
        return this.typeMappings;
    }

    /**
     * Returns the output directory of this instance.
     *
     * @return  The output directory of this instance, which may be
     *          <code>null</code>.
     */
    public File getOutputDirectory() {
        return this.outputDirectory;
    }

    /**
     * Creates a new {@link Configuration} instance from the properties of
     * both, this configuration instance and the <code>secondaryCfg</code>
     * instance.
     *
     * If both configuration instances provide an equal value for the same
     * property, this value will be copied to the resulting configuration
     * instance. Otherwise, the value of this instance's property supersedes
     * the value of the same property of <code>secondaryCfg</code>, if it is
     * not <code>null</code>. Some properties may have different merging
     * strategies.
     *
     * @param secondaryCfg  The configuration instance that provides fallback
     *                      values in case a property of the current instance
     *                      is <code>null</code>.
     *
     * @return  A new {@link Configuration} instance, whose property values are
     *          a combination of this instance' property values and the ones of
     *          <code>secondaryCfg</code>.
     *
     * @throws NullPointerException If <code>secondaryCfg</code> is
     *                              <code>null</code>.
     */
    public Configuration merge(Configuration secondaryCfg) {
        // Merging the typeMappings with the following strategy:
        //  - Combining both typeMappings lists.
        //  - If two entries collide because they map the same CML-type, the
        //    mapping of the current instance supersedes the one of
        //    secondaryCfg.

        Set<TypeMapping> typeMappings = new HashSet<>(this.typeMappings);

        secondaryCfg.typeMappings.stream()
                .filter(m -> typeMappings.stream().noneMatch(
                        m2 -> Objects.equals(m.getCmlType(), m2.getCmlType())
                ))
                .forEach(typeMappings::add);

        // Merging the output directory (nullable)

        File outputDirectory = (this.outputDirectory == null)
                ? secondaryCfg.outputDirectory
                : this.outputDirectory;

        return new Configuration(typeMappings, outputDirectory);
    }

    @Override
    public int hashCode() {
        return this.typeMappings.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Configuration) {
            Configuration cfg = (Configuration) obj;

            return this.typeMappings.equals(cfg.typeMappings);
        }

        return false;
    }
}
