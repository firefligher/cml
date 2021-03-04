package org.fir3.cml.impl.java.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

/**
 * The configuration type for {@link org.fir3.cml.impl.java.JavaTranslator}.
 */
public final class Configuration {
    final static class Deserializer
            implements JsonDeserializer<Configuration> {
        private static final String PROPERTY_TYPE_MAPPINGS = "typeMappings";

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
            Set<TypeMapping> typeMappings = new HashSet<>();

            if (obj.has(PROPERTY_TYPE_MAPPINGS)) {
                typeMappings.addAll(Arrays.asList(ctx.deserialize(
                        obj.get(PROPERTY_TYPE_MAPPINGS),
                        TypeMapping[].class
                )));
            }

            return new Configuration(typeMappings);
        }
    }

    private final Set<TypeMapping> typeMappings;

    /**
     * Creates a new instance of {@link Configuration}.
     *
     * @param typeMappings  The type mappings between CML- and Java-types
     *
     * @throws NullPointerException If <code>typeMappings</code> is
     *                              <code>null</code>.
     */
    public Configuration(Set<TypeMapping> typeMappings) {
        Objects.requireNonNull(typeMappings);

        this.typeMappings = Collections.unmodifiableSet(new HashSet<>(
                typeMappings
        ));
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

        return new Configuration(typeMappings);
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
