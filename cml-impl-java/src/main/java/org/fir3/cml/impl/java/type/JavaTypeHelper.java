package org.fir3.cml.impl.java.type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A collection of utility methods for the {@link JavaType} interface.
 */
public final class JavaTypeHelper {
    private static final String TYPE_VARIABLE_PREFIX = "P:";
    private static final char GENERIC_LEFT_DELIMITER = '<';
    private static final char GENERIC_RIGHT_DELIMITER = '>';
    private static final char GENERIC_SEPARATOR = ',';

    /**
     * Parses the specified <code>typeStr</code> and returns the corresponding
     * {@link JavaType} instance.
     *
     * @param typeStr   The Java type that will be parsed.
     * @return  The corresponding instance of {@link JavaType} for the
     *          specified <code>typeStr</code>.
     *
     * @throws NullPointerException     If <code>typeStr</code> is
     *                                  <code>null</code>.
     *
     * @throws IllegalArgumentException If the value of <code>typeStr</code> is
     *                                  invalid.
     */
    public static JavaType fromString(String typeStr) {
        // Since it is required that type variables start with a prefix, we can
        // separate them easily.

        if (typeStr.startsWith(TYPE_VARIABLE_PREFIX)) {
            return new TypeVariableType(typeStr.substring(
                    TYPE_VARIABLE_PREFIX.length()
            ));
        }

        // If there is no prefix, we assume that the typeStr starts with a
        // normal java class name and may continue with generic type
        // parameters.

        int genericLeftDelimiter = typeStr.indexOf(GENERIC_LEFT_DELIMITER);
        int genericRightDelimiter = typeStr.lastIndexOf(
                GENERIC_RIGHT_DELIMITER
        );

        if (genericRightDelimiter < genericLeftDelimiter) {
            throw new IllegalArgumentException(
                    "Invalid specification of type parameters"
            );
        }

        String className = typeStr;
        List<JavaType> typeParameters = new ArrayList<>();

        if (genericLeftDelimiter > -1) {
            className = typeStr.substring(0, genericLeftDelimiter);
            typeParameters.addAll(
                    splitTypeParameters(
                            typeStr.substring(
                                    genericLeftDelimiter + 1,
                                    genericRightDelimiter
                            )
                    ).stream()
                            .map(JavaTypeHelper::fromString)
                            .collect(Collectors.toList())
            );
        }

        return new ClassType(className, typeParameters);
    }

    private static List<String> splitTypeParameters(String typeParams) {
        if (typeParams.isEmpty()) {
            throw new IllegalArgumentException("typeParams is empty");
        }

        List<String> typeParameters = new ArrayList<>();
        StringBuilder parameterBuilder = new StringBuilder();
        int depthCount = 0;

        for (char nextChar : typeParams.toCharArray()) {
            if (depthCount == 0 && nextChar == GENERIC_SEPARATOR) {
                typeParameters.add(parameterBuilder.toString());
                parameterBuilder.setLength(0);
                continue;
            }

            parameterBuilder.append(nextChar);

            if (nextChar == GENERIC_LEFT_DELIMITER) {
                depthCount++;
                continue;
            }

            if (nextChar == GENERIC_RIGHT_DELIMITER) {
                depthCount--;
            }
        }

        if (depthCount != 0) {
            throw new IllegalArgumentException("Invalid typeParams");
        }

        typeParameters.add(parameterBuilder.toString());
        return typeParameters;
    }

    private JavaTypeHelper() {
        throw new UnsupportedOperationException(
                "Do not instantiate this class"
        );
    }
}
