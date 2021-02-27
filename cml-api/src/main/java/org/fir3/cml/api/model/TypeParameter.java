package org.fir3.cml.api.model;

import java.util.Objects;

/**
 * A type parameter is a placeholder for an actual type that may be specified
 * at the definition of a model and then may be used for specifying types
 * attributes inside that particular model.
 *
 * <p>
 *     If two instances <code>a</code> and <code>b</code> have an equal name,
 *     those instances are considered to be equal and both,
 *     <code>a.equals(b)</code> and <code>b.equals(a)</code>, must return
 *     <code>true</code>.
 * </p>
 */
public final class TypeParameter {
    private final String name;

    /**
     * Initializes a new instance of <code>TypeParameter</code>.
     *
     * @param name  The name of the new type parameter instance
     *
     * @throws NullPointerException If <code>name</code> is <code>null</code>
     */
    public TypeParameter(String name) {
        Objects.requireNonNull(name, "name is null");

        this.name = name;
    }

    /**
     * Returns the name of this type parameter.
     *
     * @return  The name of this type parameter.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeParameter) {
            TypeParameter typeParameter = (TypeParameter) obj;

            return Objects.equals(this.name, typeParameter.getName());
        }

        return false;
    }
}
