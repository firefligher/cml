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

    private static final char GENERIC_LEFT_DELIMITER = '<';
    private static final char GENERIC_RIGHT_DELIMITER = '>';
    private static final char GENERIC_SEPARATOR = ',';

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
            int rightDelimiterIndex = typeStr.lastIndexOf(
                    GENERIC_RIGHT_DELIMITER
            );

            if (
                    leftDelimiterIndex == -1 ||
                    rightDelimiterIndex < leftDelimiterIndex
            ) {
                throw new IllegalArgumentException("Invalid typeStr");
            }

            String modelName = typeStr.substring(
                    GENERIC_MODEL_TYPE_PREFIX.length(),
                    leftDelimiterIndex
            );

            List<Type> typeParameters = splitTypeParameterList(
                    typeStr.substring(
                            leftDelimiterIndex + 1,
                            rightDelimiterIndex
                    )
            ).stream()
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

    /**
     * Computes a score that states how generic the specified
     * <code>genericType</code> is compared to the specified
     * <code>derivedType</code>.
     *
     * <p>
     *     <b>Example 1:</b><br/>
     *     If <code>genericType</code> is the deserialized representation of
     *     <code>GM:List&lt;P:P1&gt;</code> and the <code>derivedType</code> is
     *     <code>GM:List&lt;M:Element&gt;</code>, the calculated derivation
     *     score is 1, because only one type parameter has been specified.
     * </p>
     *
     * <p>
     *     <b>Example 2:</b><br>
     *     If <code>genericType</code> is the deserialized representation of
     *     <code>GM:Map&lt;P:P1,P:P2&gt</code> and the <code>derivedType</code>
     *     is <code>GM:Map&lt;GM:List&lt;M:KeyMode&gt;,M:ValueModel&gt;</code>,
     *     the calculated derivation score is 2, because two type parameters of
     *     the original <code>genericType</code> have been specified.
     * </p>
     *
     * @param derivedType   The normalized derived type.
     * @param genericType   The normalized generic type.
     *
     * @return  The calculated derivation score. If the
     *          <code>derivedType</code> cannot be derived from the
     *          <code>genericType</code>, the score is <code>-1</code>. If the
     *          <code>derivedType</code> and the <code>genericType</code> are
     *          equal, the score is <code>0</code>. In all other cases, the
     *          score is positive.
     *
     * @throws NullPointerException If either <code>derivedType</code>,
     *                              <code>genericType</code>, or both are
     *                              <code>null</code>.
     */
    public static int computeDerivationScore(
            Type derivedType,
            Type genericType
    ) {
        Objects.requireNonNull(derivedType);
        Objects.requireNonNull(genericType);

        switch (genericType.getCategory()) {
            case Parameter:
                if (derivedType.getCategory() == Type.Category.Parameter) {
                    return 0;
                }

                return 1;

            case Model:
                if (derivedType.getCategory() != Type.Category.Model) {
                    return -1;
                }

                ModelType genericModelType = (ModelType) genericType;
                ModelType derivedModelType = (ModelType) derivedType;

                // If both model types differ from each other, the types must
                // be incompatible.

                if (!Objects.equals(
                        genericModelType.getModelName(),
                        derivedModelType.getModelName()
                )) {
                    return -1;
                }

                // The sum of the parameter's derivation score is the final
                // score. If the derivation score of any type parameter is -1,
                // that parameter is incompatible, which leads to a total
                // incompatibility of the specified types.

                List<Type> genericTypeParameters =
                        genericModelType.getTypeParameters();

                List<Type> derivedTypeParameters =
                        derivedModelType.getTypeParameters();

                if (genericTypeParameters.size() !=
                        derivedTypeParameters.size()) {
                    // If both types have a different number of type parameters
                    // although they reference the same model, at least one
                    // type must be invalid and both are incompatible with each
                    // other.

                    return -1;
                }

                int totalScore = 0;

                for (
                        int paramIndex = 0;
                        paramIndex < genericTypeParameters.size();
                        paramIndex++
                ) {
                    int paramScore = computeDerivationScore(
                            derivedTypeParameters.get(paramIndex),
                            genericTypeParameters.get(paramIndex)
                    );

                    if (paramScore == -1) {
                        return -1;
                    }

                    totalScore += paramScore;
                }

                return totalScore;

            default:
                throw new UnsupportedOperationException(
                        "Category not supported"
                );
        }
    }

    /**
     * Checks, if <code>limitedType</code> is a derivation from the
     * <code>genericType</code>.
     *
     * A type <code>a</code> is considered to be derived from a type
     * <code>b</code>, if <code>a</code> is an equal or more specific,
     * still compatible, representation of <code>b</code>.
     *
     * @param limitedType   The limited type, which will be checked, if it is a
     *                      derivation of <code>genericType</code>.
     *
     * @param genericType   The generic type.
     *
     * @return  Either <code>true</code>, if <code>limitedType</code> is a
     *          derivation of <code>genericType</code>, otherwise
     *          <code>false</code>.
     *
     * @throws NullPointerException If any of the parameters is
     *                              <code>null</code>.
     */
    public static boolean isDerivation(
            Type limitedType,
            Type genericType
    ) {
        Objects.requireNonNull(limitedType);
        Objects.requireNonNull(genericType);

        return computeDerivationScore(limitedType, genericType) >= 0;
    }

    /**
     * Returns the most generic {@link Type} that can be derived from the
     * specified {@link Model}.
     *
     * @param environment   The environment of the <code>domain</code> and
     *                      <code>model</code>.
     *
     * @param domain        The domain of the specified <code>model</code>.
     * @param model         The model, whose most generic {@link Type} will be
     *                      returned.
     *
     * @return  The normalized variant of the most generic {@link Type} that
     *          can be derived from the specified <code>model</code>.
     *
     * @throws NullPointerException If <code>model</code> is <code>null</code>.
     */
    public static Type from(
            Environment environment,
            Domain domain,
            Model model
    ) {
        Objects.requireNonNull(model);

        ModelType type = new ModelType(
                model.getName(),
                model.getTypeParameters()
                        .stream()
                        .map(TypeParameter::getName)
                        .map(ParameterType::new)
                        .collect(Collectors.toList())
        );

        return normalize(type, environment, domain);
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
                                                    Character.toString(
                                                            GENERIC_SEPARATOR
                                                    )
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

    private static List<String> splitTypeParameterList(
            String typeParameterStr
    ) {
        List<String> typeParameters = new ArrayList<>();
        StringBuilder typeBuilder = new StringBuilder();
        int depthCounter = 0;

        for (char nextChar : typeParameterStr.toCharArray()) {
            if (depthCounter == 0 && nextChar == GENERIC_SEPARATOR) {
                typeParameters.add(typeBuilder.toString());
                typeBuilder.setLength(0);
                continue;
            }

            typeBuilder.append(nextChar);

            if (nextChar == GENERIC_LEFT_DELIMITER) {
                depthCounter++;
                continue;
            }

            if (nextChar == GENERIC_RIGHT_DELIMITER) {
                depthCounter--;
            }
        }

        if (depthCounter > 0) {
            throw new IllegalArgumentException("typeParameterStr is invalid");
        }

        typeParameters.add(typeBuilder.toString());
        return typeParameters;
    }

    private TypeHelper() {
        throw new IllegalStateException("Do not instantiate this class");
    }
}
