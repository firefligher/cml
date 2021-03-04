package org.fir3.cml.impl.java.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A collection of converters that can be used to convert valid CML names to
 * valid Java names.
 */
public final class NameConverter {
    private static final Set<String> BAD_IDENTIFIERS = new HashSet<>(
            Arrays.asList(
                    "abstract", "assert", "boolean", "break", "byte", "case",
                    "catch", "char", "class", "const", "continue", "default",
                    "do", "double", "else", "enum", "extends", "final",
                    "finally", "float", "for", "goto", "if", "implements",
                    "import", "instanceof", "int", "interface", "long",
                    "native", "new", "package", "private", "protected",
                    "public", "return", "short", "static", "strictfp", "super",
                    "switch", "synchronized", "this", "throw", "throws",
                    "transient", "try", "void", "volatile", "while", "true",
                    "false", "null"
            )
    );

    /**
     * Converts a CML domain name to a Java package name.
     *
     * @param name  The name of the CML domain.
     * @return  The name of the Java package.
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    public static String convertDomainToPackage(String name) {
        Objects.requireNonNull(name);

        return Arrays.stream(name.split("\\."))
                .map(NameConverter::escapeIfBad)
                .collect(Collectors.joining("."));
    }

    /**
     * Converts a CML model name to a Java class name.
     *
     * @param name  The name of the CML model.
     * @return  The name of the Java class.
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    public static String convertModelToClass(String name) {
        Objects.requireNonNull(name);

        return Arrays.stream(name.split("\\."))
                .map(NameConverter::escapeIfBad)
                .collect(Collectors.joining("."));
    }

    /**
     * Converts a CML type parameter name to a Java type variable name.
     *
     * @param name  The name of the CML type parameter.
     * @return  The name of the Java type variable.
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    public static String convertTypeParameterToTypeVariable(String name) {
        Objects.requireNonNull(name);

        return Arrays.stream(name.split("\\."))
                .map(NameConverter::escapeIfBad)
                .collect(Collectors.joining("."));
    }

    /**
     * Converts a CML attribute name to a Java field name.
     *
     * @param name  The name of the CML attribute.
     * @return  The name of the Java field.
     * @throws NullPointerException If <code>name</code> is <code>null</code>.
     */
    public static String convertAttributeToField(String name) {
        Objects.requireNonNull(name);

        return Arrays.stream(name.split("\\."))
                .map(NameConverter::escapeIfBad)
                .collect(Collectors.joining("."));
    }

    private static String escapeIfBad(String name) {
        if (BAD_IDENTIFIERS.contains(name)) {
            name = String.format("_%s", name);
        }

        return name;
    }

    private NameConverter() {
        throw new UnsupportedOperationException(
                "Do not instantiate this class"
        );
    }
}
