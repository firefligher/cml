package org.fir3.cml.impl.java.config;

import com.google.gson.*;
import org.fir3.cml.api.model.Type;

import java.util.Objects;

/**
 * A mapping between a CML-type and its corresponding Java-type.
 */
public final class TypeMapping {
    final static class Deserializer implements JsonDeserializer<TypeMapping> {
        private static final String PROPERTY_CML_TYPE = "cmlType";
        private static final String PROPERTY_JAVA_TYPE = "javaType";

        @Override
        public TypeMapping deserialize(
                JsonElement element,
                java.lang.reflect.Type type,
                JsonDeserializationContext ctx
        ) throws JsonParseException {
            if (!element.isJsonObject()) {
                throw new JsonParseException("Expected JSON object");
            }

            JsonObject obj = element.getAsJsonObject();

            if (!obj.has(PROPERTY_CML_TYPE)) {
                throw new JsonParseException("No cmlType specified");
            }

            if (!obj.has(PROPERTY_JAVA_TYPE)) {
                throw new JsonParseException("No javaType specified");
            }

            return new TypeMapping(
                    ctx.deserialize(obj.get(PROPERTY_CML_TYPE), Type.class),
                    ctx.deserialize(
                            obj.get(PROPERTY_JAVA_TYPE),
                            JavaType.class
                    )
            );
        }
    }

    private final Type cmlType;
    private final JavaType javaType;

    /**
     * Creates a new instance of {@link TypeMapping}.
     *
     * @param cmlType   The CML-type that is mapped to the specified
     *                  <code>javaType</code>.
     *
     * @param javaType  The Java-type that is mapped to the specified
     *                  <code>cmlType</code>.
     *
     * @throws NullPointerException If either <code>cmlType</code> and/or
     *                              <code>javaType</code> is <code>null</code>.
     */
    public TypeMapping(Type cmlType, JavaType javaType) {
        Objects.requireNonNull(cmlType);
        Objects.requireNonNull(javaType);

        this.cmlType = cmlType;
        this.javaType = javaType;
    }

    /**
     * Returns the CML-type that is being mapped by this {@link TypeMapping}
     * instance.
     *
     * @return  The CML-type that is being mapped by this {@link TypeMapping}
     *          instance.
     */
    public Type getCmlType() {
        return this.cmlType;
    }

    /**
     * Returns the Java-type that is being mapped by this {@link TypeMapping}
     * instance.
     *
     * @return  The Java-type that is being mapped by this {@link TypeMapping}
     *          instance.
     */
    public JavaType getJavaType() {
        return this.javaType;
    }

    @Override
    public int hashCode() {
        return this.cmlType.hashCode() ^ this.javaType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeMapping) {
            TypeMapping mapping = (TypeMapping) obj;

            return Objects.equals(this.cmlType, mapping.cmlType) &&
                    Objects.equals(this.javaType, mapping.javaType);
        }

        return false;
    }
}
