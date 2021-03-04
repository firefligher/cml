package org.fir3.cml.impl.java.config;

import com.google.gson.*;
import org.fir3.cml.api.model.Type;
import org.fir3.cml.api.util.TypeHelper;

final class TypeDeserializer implements JsonDeserializer<Type> {
    @Override
    public Type deserialize(
            JsonElement element,
            java.lang.reflect.Type type,
            JsonDeserializationContext ctx
    ) throws JsonParseException {
        if (!element.isJsonPrimitive()) {
            throw new JsonParseException("Expected JSON primitive");
        }

        JsonPrimitive primitive = element.getAsJsonPrimitive();

        if (!primitive.isString()) {
            throw new JsonParseException("Expected JSON string");
        }

        return TypeHelper.fromString(primitive.getAsString());
    }
}
