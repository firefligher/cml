package org.fir3.cml.impl.java.config;

import com.google.gson.*;
import org.fir3.cml.impl.java.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * The representation of a Java type.
 */
public final class JavaType {
    static final class Deserializer implements JsonDeserializer<JavaType> {
        private static final String PROPERTY_PRIMITIVE = "primitive";
        private static final String PROPERTY_OBJECT = "object";

        @Override
        public JavaType deserialize(
                JsonElement element,
                Type type,
                JsonDeserializationContext ctx
        ) throws JsonParseException {
            if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();

                if (!primitive.isString()) {
                    throw new JsonParseException("Expected JSON string");
                }

                return new JavaType(null, primitive.getAsString());
            }

            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                String primitiveType = null;

                if (obj.has(PROPERTY_PRIMITIVE)) {
                    primitiveType = JsonUtil.expectStringProperty(
                            obj,
                            PROPERTY_PRIMITIVE
                    );
                }

                return new JavaType(
                        primitiveType,
                        JsonUtil.expectStringProperty(obj, PROPERTY_OBJECT)
                );
            }

            throw new JsonParseException(
                    "Expected JSON primitive or JSON object"
            );
        }
    }

    private final String primitiveType;
    private final String objectType;

    /**
     * Creates a new instance of {@link JavaType}.
     *
     * @param primitiveType The primitive representation of this type; this may
     *                      be <code>null</code>, if there is none.
     *
     * @param objectType    The object representation of this type.
     *
     * @throws NullPointerException If <code>object</code> is
     *                              <code>null</code>.
     */
    public JavaType(String primitiveType, String objectType) {
        Objects.requireNonNull(objectType);

        this.primitiveType = primitiveType;
        this.objectType = objectType;
    }

    /**
     * Returns either <code>true</code>, if this type has a primitive type,
     * otherwise <code>false</code>.
     *
     * @return  Either <code>true</code>, if there is a primitive type,
     *          otherwise <code>false</code>.
     */
    public boolean hasPrimitiveType() {
        return this.primitiveType != null;
    }

    /**
     * Returns the object representation of this Java type.
     *
     * @return  The object representation of this Java type.
     */
    public String getObjectType() {
        return this.objectType;
    }

    /**
     * Returns the primitive representation of this Java type.
     *
     * @return  The primitive representation of this Java type, which may be
     *          <code>null</code>, if there is none.
     */
    public String getPrimitiveType() {
        return this.primitiveType;
    }

    @Override
    public int hashCode() {
        int hc = this.objectType.hashCode();

        if (this.primitiveType != null) {
            hc ^= this.primitiveType.hashCode();
        }

        return hc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaType) {
            JavaType type = (JavaType) obj;

            return Objects.equals(this.objectType, type.objectType) &&
                    Objects.equals(this.primitiveType, type.primitiveType);
        }

        return false;
    }
}
