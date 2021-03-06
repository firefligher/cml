package org.fir3.cml.api.model;

import java.util.Objects;

/**
 * A parameter type depends on the value of a type parameter.
 *
 * <p>
 *     If two instances <code>a</code> and <code>b</code> of
 *     <code>ParameterType</code> expose the same type parameter name, they
 *     are considered to be equal and both, <code>a.equals(b)</code> and
 *     <code>b.equals(a)</code> must return <code>true</code>.
 * </p>
 */
public final class ParameterType implements Type {
    private final String typeParameterName;

    /**
     * Initializes a new instance of <code>ParameterType</code>.
     *
     * @param typeParameterName The name of the new parameter type
     *
     * @throws NullPointerException If <code>typeParameterName</code> is
     *                              <code>null</code>
     */
    public ParameterType(String typeParameterName) {
        Objects.requireNonNull(typeParameterName, "typeParameterName is null");

        this.typeParameterName = typeParameterName;
    }

    /**
     * Returns the name of the type parameter that this type depends on.
     *
     * @return  The name of the type parameter that this type depends on.
     */
    public String getTypeParameterName() {
        return this.typeParameterName;
    }

    @Override
    public Category getCategory() {
        return Category.Parameter;
    }

    @Override
    public int hashCode() {
        return Category.Parameter.hashCode() ^
                this.typeParameterName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParameterType) {
            ParameterType parameterType = (ParameterType) obj;

            return Objects.equals(
                    this.typeParameterName,
                    parameterType.getTypeParameterName()
            );
        }

        return false;
    }
}
