package org.fir3.cml.impl.java.type;

/**
 * Represents a Java type.
 */
public interface JavaType {
    /**
     * The category of a {@link JavaType}.
     */
    enum Category {
        /**
         * The type is derived from a class, interface, etc.
         */
        ClassType,

        /**
         * The type represents a generic type variable.
         */
        TypeVariable
    }

    /**
     * Returns the category of this type.
     *
     * @return  The category of this type.
     */
    Category getCategory();

    /**
     * Returns the string representation of this type that can be used in Java
     * source code.
     *
     * @return  The string representation of this type.
     */
    String toJavaType();
}
