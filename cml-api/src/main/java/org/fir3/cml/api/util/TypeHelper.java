package org.fir3.cml.api.util;

import org.fir3.cml.api.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A collection of utility methods for type instances.
 */
public final class TypeHelper {
    private static final String PARAMETER_TYPE_PREFIX = "P:";
    private static final String MODEL_TYPE_PREFIX = "M:";
    private static final String GENERIC_MODEL_TYPE_PREFIX = "GM:";
    private static final String GENERIC_LEFT_DELIMITER = "<";
    private static final String GENERIC_RIGHT_DELIMITER = ">";
    private static final String GENERIC_SEPARATOR = ",";

    /**
     * Returns the unique string representation of the specified
     * <code>type</code>.
     *
     * Note that if the normalized representation of two type instances are
     * equal, the string representations of the original (not-normalized)
     * type instances must be equal as well.
     *
     * @param type          The type whose string representation will be
     *                      returned.
     *
     * @param environment   The environment of the specified <code>type</code>.
     * @param context       The context of the specified <code>type</code>.
     *
     * @return  The unique string representation of the specified
     *          <code>type</code>.
     *
     * @throws NullPointerException     If <code>type</code> or
     *                                  <code>environment</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If the <code>type</code> depends on
     *                                  unknown models.
     */
    public static String toString(
            Type type,
            Environment environment,
            Domain context
    ) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(environment, "environment is null");

        // Normalizing the specified type to avoid non-unique string
        // representations

        type = TypeHelper.normalize(type, environment, context);

        // Creating the unique string representation from the normalized type.

        return TypeHelper.toString(type, environment);
    }

    /**
     * Resolves a type instance from its unique string representation.
     *
     * @param typeStr   The string representation of the type.
     *
     * @return  The resolved type instance
     *
     * @throws NullPointerException     If <code>typeStr</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If <code>typeStr</code> is not a valid
     *                                  string representation of a type.
     */
    public static Type fromString(String typeStr) {
        Objects.requireNonNull(typeStr, "typeStr is null");

        if (typeStr.startsWith(PARAMETER_TYPE_PREFIX)) {
            return new ParameterType(typeStr.substring(
                    PARAMETER_TYPE_PREFIX.length()
            ));
        }

        if (typeStr.startsWith(MODEL_TYPE_PREFIX)) {
            return new ModelType(
                    typeStr.substring(MODEL_TYPE_PREFIX.length()),
                    Collections.emptyList()
            );
        }

        if (typeStr.startsWith(GENERIC_MODEL_TYPE_PREFIX)) {
            int leftDelimiterIndex = typeStr.indexOf(GENERIC_LEFT_DELIMITER);
            int rightDelimiterIndex = typeStr.indexOf(GENERIC_RIGHT_DELIMITER);

            if (leftDelimiterIndex == -1 || rightDelimiterIndex == -1) {
                throw new IllegalArgumentException("Invalid typeStr");
            }

            String modelName = typeStr.substring(
                    GENERIC_MODEL_TYPE_PREFIX.length(),
                    leftDelimiterIndex
            );

            List<Type> typeParameters = Arrays.stream(typeStr.substring(
                    leftDelimiterIndex + 1,
                    rightDelimiterIndex
            ).split(GENERIC_SEPARATOR))
                    .map(TypeHelper::fromString)
                    .collect(Collectors.toList());

            return new ModelType(modelName, typeParameters);
        }

        throw new IllegalArgumentException(
                "Unknown category prefix in typeStr"
        );
    }

    /**
     * Returns a representation of the specified <code>type</code> that is
     * independent of any context- and implementation-specific details.
     *
     * The resulting normalized type instance is suitable for comparison with
     * other normalized type instances by using its {@link Type#equals(Object)}
     * method.
     *
     * @param type          The type, whose normalized representation will be
     *                      returned.
     *
     * @param environment   The environment of the specified <code>type</code>.
     * @param context       The context of the specified <code>type</code>.
     *
     * @return  The normalized representation of the specified
     *          <code>type</code>.
     *
     * @throws NullPointerException     If <code>type</code> or
     *                                  <code>environment</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If <code>type</code> depends on unknown
     *                                  models.
     */
    public static Type normalize(
            Type type,
            Environment environment,
            Domain context
    ) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(environment, "environment is null");

        return normalize(type, environment, context, new HashMap<>());
    }

    private static String toString(Type type, Environment environment) {
        StringBuilder typeStr = new StringBuilder();

        switch (type.getCategory()) {
            case Parameter:
                typeStr.append(PARAMETER_TYPE_PREFIX).append(
                        ((ParameterType) type).getTypeParameterName()
                );
                break;

            case Model:
                ModelType modelType = (ModelType) type;

                Pair<Domain, Model> pair = environment.resolveModel(
                        modelType.getModelName(),
                        null
                ).orElseThrow(() -> new IllegalArgumentException(
                        "Invalid type"
                ));

                List<Type> typeParameters = modelType.getTypeParameters();

                if (typeParameters.isEmpty()) {
                    typeStr.append(MODEL_TYPE_PREFIX)
                            .append(ModelHelper.toString(
                                    pair.getFirstComponent(),
                                    pair.getSecondComponent()
                            ));
                } else {
                    typeStr.append(GENERIC_MODEL_TYPE_PREFIX)
                            .append(ModelHelper.toString(
                                    pair.getFirstComponent(),
                                    pair.getSecondComponent()
                            ))
                            .append(GENERIC_LEFT_DELIMITER)
                            .append(
                                    typeParameters.stream()
                                            .map(t -> toString(
                                                    t,
                                                    environment
                                            ))
                                            .collect(Collectors.joining(
                                                    GENERIC_SEPARATOR
                                            ))
                            )
                            .append(GENERIC_RIGHT_DELIMITER);
                }
                break;

            default:
                throw new UnsupportedOperationException(String.format(
                        "Category not implemented: '%s'",
                        type.getCategory().name()
                ));
        }

        return typeStr.toString();
    }

    private static Type normalize(
            Type type,
            Environment environment,
            Domain context,
            Map<Type, Type> parameterTypeMappings
    ) {
        switch (type.getCategory()) {
            case Parameter:
                Type normalizedType = parameterTypeMappings.get(type);

                if (normalizedType == null) {
                    normalizedType = new ParameterType(String.format(
                            "P%d",
                            parameterTypeMappings.size() + 1
                    ));

                    parameterTypeMappings.put(type, normalizedType);
                }

                return normalizedType;

            case Model:
                ModelType modelType = (ModelType) type;

                Pair<Domain, Model> model = environment.resolveModel(
                        modelType.getModelName(),
                        context
                ).orElseThrow(() -> new IllegalArgumentException(
                        "Invalid type"
                ));

                List<Type> typeParameters = modelType.getTypeParameters()
                        .stream()
                        .map(t -> normalize(
                                t,
                                environment,
                                context,
                                parameterTypeMappings
                        ))
                        .collect(Collectors.toList());

                return new ModelType(
                        ModelHelper.toString(
                                model.getFirstComponent(),
                                model.getSecondComponent()
                        ),
                        typeParameters
                );

            default:
                throw new UnsupportedOperationException(String.format(
                        "Category not implemented: '%s'",
                        type.getCategory().name()
                ));
        }
    }

    private TypeHelper() {
        throw new IllegalStateException("Do not instantiate this class");
    }
}
