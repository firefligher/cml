package org.fir3.cml.impl.java.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

/**
 * A collection of utility methods for handling JSON-data.
 */
public final class JsonUtil {
    /**
     * Validates that the specified <code>parent</code> object provide a string
     * property with the specified <code>propertyName</code> and returns the
     * property's value.
     *
     * @param parent        The object that must provide a string property with
     *                      the specified <code>propertyName</code>.
     *
     * @param propertyName  The name of the required string property.
     *
     * @return  The corresponding value of the property with the specified
     *          <code>propertyName</code>, that <code>parent</code> provides.
     *
     * @throws JsonParseException   If the specified <code>parent</code> does
     *                              not provide a string property with the
     *                              specified <code>propertyName</code>.
     *
     * @throws NullPointerException If <code>parent</code> and/or
     *                              <code>propertyName</code> is
     *                              <code>null</code>.
     */
    public static String expectStringProperty(
            JsonObject parent,
            String propertyName
    ) throws JsonParseException {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(propertyName);

        if (!parent.has(propertyName)) {
            throw new JsonParseException(String.format(
                    "Required property '%s' is missing",
                    propertyName
            ));
        }

        JsonElement element = parent.get(propertyName);

        if (!element.isJsonPrimitive()) {
            throw new JsonParseException(String.format(
                    "Expected property '%s' to be primitive",
                    propertyName
            ));
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();

        if (!primitive.isString()) {
            throw new JsonParseException(String.format(
                    "Expected property '%s' to be a string",
                    propertyName
            ));
        }

        return primitive.getAsString();
    }

    private JsonUtil() {
        throw new UnsupportedOperationException(
                "Do not instantiate this class"
        );
    }
}
